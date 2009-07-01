package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * This is a combination of the stream and the URL image providers. It allows
 * the interfaces of both of them to be combined, and displays both streams and
 * URLs.
 * </p>
 * 
 * @author robin
 */
public interface IStreamAndURLDynamicImageProvider extends
		IDynamicImageProvider, IURLDynamicImageProvider {

	/**
	 * Sets up the initial parameters for what to show.
	 * 
	 * @param display
	 *            the display instance used to generate the images
	 * @param url
	 *            the URL to display. May be <code>null</code> in which case the
	 *            stream is used initially
	 * @param stream
	 *            the stream to display. May be <code>null</code> in which case
	 *            the URL is used. If both are <code>null</code> then this
	 *            starts off blank.
	 * @param cacheDir
	 *            the directory to cache downloaded images in
	 */
	public void setParams(Display display, URL url, InputStream stream,
			File cacheDir);

	/**
	 * Allows the URL to be updated. <code>setParams</code> must be called first
	 * for the initial configuration.
	 * 
	 * @param url
	 *            the new URL
	 * @see #setParams(Display, URL, InputStream, File)
	 */
	public void changeURL(URL url);

}
