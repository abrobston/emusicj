package nz.net.kallisti.emusicj.download;


/**
 * <p>The interface for classes that download files</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public interface IDownloader {

    public IDownloadMonitor getMonitor();
    
    public void start();
    
    public void stop();
    
}
