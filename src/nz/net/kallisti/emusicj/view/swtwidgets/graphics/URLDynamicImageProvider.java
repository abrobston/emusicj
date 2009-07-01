package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloadListener;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This is a standard dynamic image provider that loads the image to show from a
 * URL. It also is able to cache the image so that it can be shown before the
 * URL has been loaded, if it's already been loaded in the past. If the
 * downloaded image has changed, then an event is sent to the listeners. Note
 * that it only checks once, on initialisation, for the image.
 * </p>
 * 
 * @author robin
 */
public class URLDynamicImageProvider implements IDynamicImageProvider,
		IURLDynamicImageProvider {

	private final List<IDynamicImageChangeListener> listeners;
	private final Logger logger;
	private final Provider<ISimpleDownloader> downloaderProvider;
	private Display display;
	private Image image;
	private File cacheDir;
	/**
	 * This prevents multiple downloads happening at once. It locks across the
	 * application in case two instances try to pull down one file.
	 */
	private static final Object downloadLock = new Object();
	/**
	 * This tracks URLs that have already been downloaded this session so that
	 * multiple requests aren't made.
	 */
	private final Set<URL> requested = Collections
			.synchronizedSet(new HashSet<URL>());

	/**
	 * <p>
	 * This creates an instance of the image provider. It can be configured
	 * using {@link #setParams(URL, File)}.
	 * </p>
	 */
	@Inject
	public URLDynamicImageProvider(
			Provider<ISimpleDownloader> downloaderProvider) {
		this.downloaderProvider = downloaderProvider;
		listeners = Collections
				.synchronizedList(new ArrayList<IDynamicImageChangeListener>());
		logger = LogUtils.getLogger(this);
	}

	public void setParams(Display display, URL url, File cacheDir) {
		this.cacheDir = cacheDir;
		this.display = display;
		if (cacheDir == null)
			throw new IllegalArgumentException("cacheDir cannot be null");
		downloadImage(url);
	}

	/**
	 * Downloads the image and sets it all up. If there's a cached version
	 * already there, this will display it first, and notify listeners.
	 * Otherwise, listeners will be notified when it's ready.
	 * 
	 * @param url
	 *            the URL to source the image from
	 */
	private void downloadImage(final URL url) {
		synchronized (downloadLock) {
			// If the URL is null, then we can't download anything.
			if (url == null)
				return;
			// First thing, check cache - files in the cache are known by an MD5
			// of
			// their URL.
			MessageDigest md5;
			try {
				md5 = MessageDigest.getInstance("MD5");
				md5.update(url.toString().getBytes());
				Base64 base64 = new Base64();
				String filename = new String(base64.encode(md5.digest()))
						.replace('/', '_');
				File cacheFile = new File(this.cacheDir, filename);
				if (cacheFile.exists()) {
					setImage(cacheFile);
				}
				if (requested.contains(url))
					return;
				// Download the file to the cache
				ISimpleDownloader downloader = downloaderProvider.get();
				downloader.setURL(url);
				downloader.setOutputFile(cacheFile);
				downloader.addListener(new ISimpleDownloadListener() {
					public void downloadFailed(ISimpleDownloader downloader) {
					}

					public void downloadSucceeded(ISimpleDownloader downloader,
							File file) {
						requested.add(url);
						setImage(file);
					}
				});
				downloader.start();
			} catch (NoSuchAlgorithmException e) {
				logger
						.log(
								Level.SEVERE,
								"Unable to use cache to save images - missing hashing algorithm",
								e);
			}
		}
	}

	public void changeURL(URL url) {
		synchronized (downloadLock) {
			if (cacheDir == null)
				throw new IllegalArgumentException(
						"setParams must be called first");
			downloadImage(url);
		}
	}

	/**
	 * This generates an image from the provided file.
	 * 
	 * @param file
	 */
	private synchronized void setImage(File file) {
		Image tempImage;
		try {
			tempImage = new Image(display, file.toString());
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unable to create image resource", e);
			return;
		}
		if (image != null) {
			image.dispose();
		}
		image = tempImage;
		for (IDynamicImageChangeListener l : listeners) {
			l.newImage(this, image);
		}
	}

	public void addListener(IDynamicImageChangeListener listener) {
		listeners.add(listener);
	}

	public Image getImage() {
		return image;
	}

	public void removeListener(IDynamicImageChangeListener listener) {
		listeners.remove(listener);
	}

}
