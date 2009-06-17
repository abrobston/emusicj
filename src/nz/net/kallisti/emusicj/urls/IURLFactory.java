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

	public static final String UPDATEURL_KEY = "updateurl";
	public static final String MANUALURL_KEY = "manualurl";
	public static final String APPURL_KEY = "appurl";
	public static final String TOOLBARICONSOURCE_KEY = "toolbariconsrc";
	public static final String TOOLBARICONDEST_KEY = "toolbaricondest";
	public static final String CUSTOMERSUPPORTURL_KEY = "customersupporturl";

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

	/**
	 * Gets the URL that is the source for the application icon on the toolbar
	 * 
	 * @return the URL for the application icon. This may be <code>null</code>.
	 */
	public URL getToolbarIconSourceURL();

	/**
	 * Gets the URL that a browser is opened to when it app icon on the toolbar
	 * is clicked.
	 * 
	 * @return the URL to open when the toolbar icon is clicked. May be
	 *         <code>null</code>.
	 */
	public URL getToolbarIconClickURL();

	/**
	 * Gets the URL that the user will be taken to if they select 'customer
	 * support' from the help menu.
	 * 
	 * @return the URL to open when the user want support. May be
	 *         <code>null</code> if there is no such entry, otherwise it should
	 *         be there.
	 */
	public URL getCustomerSupportURL();

}
