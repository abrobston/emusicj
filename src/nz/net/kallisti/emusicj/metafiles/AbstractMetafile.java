package nz.net.kallisti.emusicj.metafiles;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.ICoverDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.urls.IDynamicURL;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.images.IImageFactory;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IURLDynamicImageProvider;

import com.google.inject.Provider;

/**
 * <p>
 * This provides some basic functions, and centralises some more complex ones,
 * that are used when processing metafiles
 * </p>
 * 
 * @author robin
 */
public abstract class AbstractMetafile implements IMetafile {

	private final IImageFactory images;
	private final Logger logger;
	private final IURLFactory urls;
	// If the file is in the cache, then we know we don't need to create
	// downloaders for it
	private static Set<File> coverArtCache = Collections
			.synchronizedSet(new HashSet<File>());
	private final IStrings strings;
	private final Provider<ICoverDownloader> coverDownloaderProvider;

	public AbstractMetafile(IImageFactory images, IURLFactory urls,
			IStrings strings, Provider<ICoverDownloader> coverDownloaderProvider) {
		// This is needed so we can update the logo if the .col file tells us
		// it's changed. This is annoyingly tightly coupled to the SWT view
		// implementation, and so the design may need to be revisited in the
		// future.
		this.images = images;
		this.urls = urls;
		this.strings = strings;
		this.coverDownloaderProvider = coverDownloaderProvider;
		logger = LogUtils.getLogger(this);
	}

	/**
	 * This sets the logo to that of the provided URL.
	 * 
	 * @param url
	 *            the URL containing the new logo
	 */
	protected void setLogo(URL url) {
		IDynamicImageProvider logoProvider = images
				.getApplicationLogoProvider();
		if (logoProvider instanceof IURLDynamicImageProvider) {
			((IURLDynamicImageProvider) logoProvider).changeURL(url);
		} else {
			logger
					.warning("Logo change requested on non-URL-based dynamic image (logoProvider.getClass()="
							+ logoProvider.getClass());
		}

	}

	/**
	 * This processes the date that is contained in the string, and turns it
	 * into a real date. It expects RFC 3339 dates.
	 * 
	 * @param dateStr
	 *            the string form of the date
	 * @return the parsed date, or <code>null</code> if it can't be parsed.
	 */
	protected Date parseDate(String dateStr) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		ParsePosition pos = new ParsePosition(0);
		if (dateStr == null || "".equals(dateStr))
			return null;
		// because SimpleDateFormat is stupid we need to remove the ':' from the
		// timezone
		dateStr = dateStr.replaceFirst("([+-]\\d\\d):(\\d\\d)$", "$1$2");
		// If people send a date with a 'T' separator, that needs to be replaced
		// with a space.
		dateStr = dateStr.replaceFirst("T", " ");
		// If the timezone is given as 'Z', we turn that into something useful
		dateStr = dateStr.replaceFirst("Z$", "+0000");
		Date date = df.parse(dateStr, pos);
		if (date == null) {
			logger.warning("Unable to parse date (" + dateStr
					+ "), error in pos " + pos.getErrorIndex());
			return null;
		}
		return date;
	}

	/**
	 * Sets the banner, along with its clickability.
	 * 
	 * @param imgUrl
	 *            the URL to source the image from. If <code>null</code> the
	 *            entire call is ignored.
	 * @param clickUrl
	 *            the URL that the user is taken to when they click on the
	 *            banner. If <code>null</code>, it won't be clickable.
	 */
	protected void setBanner(URL imgUrl, URL clickUrl) {
		if (imgUrl == null)
			return;
		IURLDynamicImageProvider bannerImage = images.getBannerProvider();
		bannerImage.changeURL(imgUrl);
		IDynamicURL bannerClick = urls.getBannerClickURL();
		bannerClick.setURL(clickUrl);
	}

	/**
	 * This is passed a String URL of where to find the coverart for a track. It
	 * turns it into a filename. If the file doesn't exist, and we haven't
	 * already created a downloader for it, then a download is added to the
	 * list.
	 * 
	 * @param downloaders
	 *            a list that the cover downloader will be added to
	 * @param coverArt
	 *            the string form of the URL to load
	 * @param artist
	 *            the artist this cover is for
	 * @param album
	 *            the album it's from
	 * @param title
	 *            the title of the track
	 * @param trackNum
	 *            the number of the track
	 * @param disk
	 *            the disk number that this track belongs to
	 * @param diskCount
	 *            the total number of disks in this album. If either this or
	 *            <code>disk</code> is <code>null</code>, then this is ignored.
	 *            Also, if this &lt;=1 it is ignored.
	 * @return a file corresponding the coverart. It may not exist yet, but that
	 *         is where it eventually will be. If we aren't going to be
	 *         downloading the cover art, this will return <code>null</code>.
	 */
	protected File getCoverArtCached(List<IDownloader> downloaders,
			final String coverArt, IPreferences prefs, int trackNum,
			String title, String album, String artist, Integer disk,
			Integer diskCount) {
		if (!prefs.downloadCoverArt())
			return null;
		URL coverUrl;
		try {
			coverUrl = new URL(coverArt);
		} catch (MalformedURLException e) {
			logger
					.log(Level.WARNING, "Malformed cover art URL: " + coverArt,
							e);
			return null;
		}
		File coverFile;
		File savePath = prefs.getPathFor(trackNum, title, album, artist, disk,
				diskCount);
		int dotPos = coverArt.lastIndexOf(".");
		if (dotPos != -1) {
			String filetype = coverArt.substring(dotPos);
			if (filetype.equalsIgnoreCase(".jpeg"))
				filetype = ".jpg"; // who the hell uses ".jpeg" as an extension
			// anyway?
			coverFile = new File(savePath, strings.getCoverArtName() + filetype);
			// No need for a downloader (we can't check this based on the URL,
			// as sometimes one URL will supply multiple albums, especially in
			// the case of multidisk stuff)
			if (coverArtCache.contains(coverFile))
				return coverFile;

		} else {
			return null;
		}
		if (!coverFile.exists()) {
			coverArtCache.add(coverFile);
			// add the downloader
			ICoverDownloader dl = coverDownloaderProvider.get();
			// Create a monitor that will remove this entry from the cache when
			// the download has finished
			IDownloadMonitor mon = dl.getMonitor();
			mon.addStateListener(new IDownloadMonitorListener() {
				// This is a bit icky, but it'll do the job in 99% of cases.
				// It allows the cover art to be redownloaded if the user
				// deletes it. Why they'd do this I don't know, but it came
				// up in testing.
				public void monitorStateChanged(IDownloadMonitor monitor) {
					if (monitor.getDownloadState() == DLState.CANCELLED
							|| monitor.getDownloadState() == DLState.FINISHED
							|| monitor.getDownloadState() == DLState.EXPIRED) {
						coverArtCache.remove(coverArt);
					}
				}
			});
			dl.setDownloader(coverUrl, coverFile);
			downloaders.add(dl);
		}
		return coverFile;
	}

}
