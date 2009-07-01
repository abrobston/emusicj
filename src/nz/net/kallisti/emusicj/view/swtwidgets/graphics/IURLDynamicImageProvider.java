package nz.net.kallisti.emusicj.view.swtwidgets.graphics;

import java.io.File;
import java.net.URL;

import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * Implementations of this are able to load images from a URL and possibly cache
 * it.
 * </p>
 * 
 * @author robin
 */
public interface IURLDynamicImageProvider extends IDynamicImageProvider {

	/**
	 * <p>
	 * Sets up this dynamic image provider. This may be called multiple time to
	 * change, but must be called at least once.
	 * </p>
	 * 
	 * @param display
	 *            the display instance that is used to generate the images.
	 * @param url
	 *            the URL that is to be displayed. May be <code>null</code>.
	 * @param cacheDir
	 *            the directory that will be used to cache the file. Should not
	 *            be <code>null</code>.
	 * 
	 * @see #changeURL(URL)
	 */
	public abstract void setParams(Display display, URL url, File cacheDir);

	/**
	 * Allows the URL to be updated. <code>setParams</code> must be called first
	 * for the initial configuration.
	 * 
	 * @param url
	 *            the new URL
	 * @see #setParams(Display, URL, File)
	 */
	public abstract void changeURL(URL url);

}