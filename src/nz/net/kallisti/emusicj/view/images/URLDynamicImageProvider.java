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
import nz.net.kallisti.emusicj.network.http.IHttpClientProvider;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageChangeListener;
import nz.net.kallisti.emusicj.view.swtwidgets.graphics.IDynamicImageProvider;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.eclipse.swt.graphics.Image;

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
public class URLDynamicImageProvider implements IDynamicImageProvider {

	private final List<IDynamicImageChangeListener> listeners;
	private final Logger logger;

	/**
	 * <p>
	 * This creates an instance of the image provider that shows the specified
	 * URL.
	 * </p>
	 * 
	 * @param url
	 *            the URL that is to be displayed.
	 * @param httpClientProvider
	 *            the provider that supplies the {@link HttpClient} instance
	 *            that will be used to download the image
	 * @param cacheDir
	 *            the directory that will be used to cache the file
	 */
	public URLDynamicImageProvider(URL url,
			IHttpClientProvider httpClientProvider, File cacheDir) {
		listeners = Collections
				.synchronizedList(new ArrayList<IDynamicImageChangeListener>());
		logger = LogUtils.getLogger(this);
		// First thing, check cache - files in the cache are known by an MD5 of
		// their URL.
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(url.toString().getBytes());
			Base64 base64 = new Base64();
			String filename = new String(base64.encode(md5.digest()));
		} catch (NoSuchAlgorithmException e) {
			logger
					.log(
							Level.SEVERE,
							"Unable to use cache to save images - missing hashing algorithm",
							e);
		}
	}

	public void addListener(IDynamicImageChangeListener listener) {
		listeners.add(listener);
	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeListener(IDynamicImageChangeListener listener) {
		listeners.remove(listener);
	}

}
