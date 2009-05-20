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
	 * Sets up this dynamic image provider
	 * </p>
	 * 
	 * @param url
	 *            the URL that is to be displayed.
	 * @param cacheDir
	 *            the directory that will be used to cache the file
	 */
	public abstract void setParams(URL url, File cacheDir, Display display);

}