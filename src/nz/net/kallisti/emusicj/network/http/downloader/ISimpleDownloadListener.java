package nz.net.kallisti.emusicj.network.http.downloader;

import java.io.File;

/**
 * <p>
 * Implementors of this can be notified when downloads complete
 * </p>
 * 
 * @author robin
 */
public interface ISimpleDownloadListener {

	/**
	 * This is called when a download successfully completes.
	 * 
	 * @param downloader
	 *            the downloader that has completed
	 * @param file
	 *            the file that was created
	 */
	public void downloadSucceeded(ISimpleDownloader downloader, File file);

	/**
	 * This is called when a download fails.
	 * 
	 * @param downloader
	 *            the downloader that was trying to download
	 */
	public void downloadFailed(ISimpleDownloader downloader);

}
