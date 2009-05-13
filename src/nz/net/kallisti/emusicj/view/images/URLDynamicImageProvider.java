package nz.net.kallisti.emusicj.view.images;

import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloadListener;
import nz.net.kallisti.emusicj.network.http.downloader.ISimpleDownloader;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageChangeListener;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;

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
public class URLDynamicImageProvider implements IDynamicImageProvider, IURLDynamicImageProvider {

	private final List<IDynamicImageChangeListener> listeners;
	private final Logger logger;
	private final Provider<ISimpleDownloader> downloaderProvider;
	private Display display;
	private Image image;

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

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.view.images.IURLDynamicImageProvider#setParams(java.net.URL, java.io.File, org.eclipse.swt.widgets.Display)
	 */
	public void setParams(URL url, File cacheDir, Display display) {
		this.display = display;
		// First thing, check cache - files in the cache are known by an MD5 of
		// their URL.
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(url.toString().getBytes());
			Base64 base64 = new Base64();
			String filename = new String(base64.encode(md5.digest()));
			File cacheFile = new File(cacheDir, filename);
			if (cacheFile.exists()) {
				setImage(cacheFile);
			}
			// Download the file to the cache
			ISimpleDownloader downloader = downloaderProvider.get();
			downloader.setURL(url);
			downloader.setOutputFile(cacheFile);
			downloader.addListener(new ISimpleDownloadListener() {
				public void downloadFailed(ISimpleDownloader downloader) {
				}

				public void downloadSucceeded(ISimpleDownloader downloader,
						File file) {
					setImage(file);
				}
			});
		} catch (NoSuchAlgorithmException e) {
			logger
					.log(
							Level.SEVERE,
							"Unable to use cache to save images - missing hashing algorithm",
							e);
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
