package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 *
 * 
 * $Id:$
 *
 * @author robin
 */
public class CoverDownloadMonitor extends HTTPDownloadMonitor implements
		IDownloadMonitor, IDisplayableDownloadMonitor {

	/**
	 * 
	 */
	public CoverDownloadMonitor() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param downloader
	 */
	public CoverDownloadMonitor(HTTPDownloader downloader) {
		super(downloader);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDisplayableDownloadMonitor#getImageFile()
	 */
	public File getImageFile() {
		return downloader.getOutputFile();
	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDisplayableDownloadMonitor#getText()
	 */
	public String[][] getText() {
		return new String[0][0];
	}

}
