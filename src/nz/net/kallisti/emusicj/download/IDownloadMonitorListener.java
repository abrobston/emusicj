package nz.net.kallisti.emusicj.download;

/**
 * <p>Allows an object to register itself as a listener to a download
 * monitor</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public interface IDownloadMonitorListener {

    public void monitorStateChanged(IDownloadMonitor monitor);
    
}
