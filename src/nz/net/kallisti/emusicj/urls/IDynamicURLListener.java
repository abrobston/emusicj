package nz.net.kallisti.emusicj.urls;

import java.net.URL;

/**
 * <p>
 * Instances of this may be notified when a dynamic URL changes
 * </p>
 * 
 * @author robin
 */
public interface IDynamicURLListener {

	/**
	 * This is called when a monitored dynamic URL changes
	 * 
	 * @param dynamicUrl
	 *            the dynamic URL that changed
	 * @param url
	 *            the new URL
	 */
	public void newURL(IDynamicURL dynamicUrl, URL url);

}
