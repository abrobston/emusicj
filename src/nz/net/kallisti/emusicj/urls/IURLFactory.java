package nz.net.kallisti.emusicj.urls;

import java.net.URL;

/**
 * <p>
 * Implementations of this factory supply URLs to the program.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public interface IURLFactory {

	/**
	 * Gets the URL to check for updates
	 * 
	 * @return the URL to check for updates
	 */
	public URL getUpdateURL();

	/**
	 * Gets the URL that is the application homepage
	 * 
	 * @return the URL that is the application homepage
	 */
	public URL getAppURL();

	/**
	 * Gets the URL that is the user manual
	 * 
	 * @return the URL that is the user manual
	 */
	public URL getManualURL();

}
