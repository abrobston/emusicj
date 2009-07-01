package nz.net.kallisti.emusicj.urls;

import java.net.URL;

/**
 * <p>
 * This supplies a URL that may be changed.
 * </p>
 * 
 * @author robin
 */
public interface IDynamicURL {

	/**
	 * Get the current URL
	 * 
	 * @return the current URL. May be <code>null</code>
	 */
	public URL getURL();

	/**
	 * Set the URL
	 * 
	 * @param url
	 *            the new url. May be <code>null</code> to indicate that there
	 *            isn't one any more
	 */
	public void setURL(URL url);

	/**
	 * Adds a listener that will be notified when the URL changes
	 * 
	 * @param listener
	 *            the listener to notify
	 */
	public void addListener(IDynamicURLListener listener);

	/**
	 * Removes a previously added listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(IDynamicURLListener listener);

}
