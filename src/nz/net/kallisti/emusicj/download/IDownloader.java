package nz.net.kallisti.emusicj.download;

import java.io.File;
import java.net.URL;


/**
 * <p>The interface for classes that download files</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public interface IDownloader {

    public IDownloadMonitor getMonitor();
    
    public void start();
    
    /**
     * This tells the downloader to shut down any downloads. This should stop
     * any threads running.
     */
    public void stop();

	/**
	 * This tells the downloader to pause any downloads. This may or may not stop
	 * threads.
	 */
	public void pause();
    
    public URL getURL();
    
    public File getOutputFile();
    
}
