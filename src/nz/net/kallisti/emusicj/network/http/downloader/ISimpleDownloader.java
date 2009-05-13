package nz.net.kallisti.emusicj.network.http.downloader;

import java.io.File;
import java.net.URL;

/**
 * <p>
 * This is an interface to a simple downloader. It isn't as advanced as the
 * IDownloader stuff but it's also easier to use.
 * </p>
 * <p>
 * The general procedure is to add a listener (
 * {@link #addListener(ISimpleDownloadListener)}, define a URL to download from
 * ({@link #setURL(URL)}, set a file to save to ({@link #setOutputFile(File)},
 * and then call {@link #start()}. When the download has completed, the
 * listeners you have added will be told about it.
 * </p>
 * 
 * @author robin
 */
public interface ISimpleDownloader {

	/**
	 * Adds a listener to be notified when a download completes
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(ISimpleDownloadListener listener);

	/**
	 * Removes a previously added listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeListener(ISimpleDownloadListener listener);

	/**
	 * Sets the URL to download from. In most cases, this should be an HTTP URL.
	 * 
	 * @param url
	 *            the URL to download from
	 */
	public void setURL(URL url);

	/**
	 * The file to save to. If it already exists, it may be overwritten. If it
	 * can't be written to, a failure is registered.
	 * 
	 * @param file
	 *            the file to save to
	 */
	public void setOutputFile(File file);

	/**
	 * Starts the download. This will return immediately, and the download
	 * itself will occur in its own thread.
	 * 
	 * @throws IllegalStateException
	 *             if the URL and file haven't been configured yet
	 */
	public void start() throws IllegalStateException;

}
