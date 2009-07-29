package nz.net.kallisti.emusicj.updater;

import java.net.URL;

/**
 *
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IUpdateFetcher {

	/**
	 * Sets the listener to be notified if there is an updated available
	 * @param listener the object to notify
	 */
	public void setListener(IUpdateFetcherListener listener);

	/**
	 * Specifies the URL to query for an update
	 * @param url the usr to check for updates from
	 */
	public void setUpdateUrl(URL url);

	/**
	 * Initiates the version check.
	 * @param currVersion the current version of the application to check 
	 * against
	 */
	public void check(String currVersion);

}