package nz.net.kallisti.emusicj.download;

/**
 * <p>This is class that is instantiated by an IDownloader subclass to allow the
 * state of the download progress to be tracked by something.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public interface IDownloadMonitor {

    public enum DLState { NOTSTARTED, CONNECTING, DOWNLOADING, PAUSED, STOPPED,
        FINISHED, FAILED } 
    
    /**
     * Gets a nice name of the download
     * @return a String describing the download
     */
    public String getName();
    
    /**
     * Gets the percentage complete of the download.
     * @return download progress as a percentage
     */
    public double getDownloadPercent();
    
    /**
     * Gets the bytes downloaded so far
     * @return bytes downloaded
     */
    public long getBytesDown();
    
    /**
     * Gets the total bytes in this transfer.
     * @return the total bytes in this transfer, or -1 if unknown.
     */
    public long getTotalBytes();
    
    /**
     * <p>Gets the state of the download.
     * @return the download state as a DLState enum value.
     */
    public DLState getDownloadState();
    
    /**
     * Adds a listener to the monitor. This listener is triggered on 
     * state changes only, not on data being downloaded. That must be polled
     * for.
     * @param listener the listener to add
     */
    public void addStateListener(IDownloadMonitorListener listener);

    /**
     * Remove a listener from the monitor.
     * @param listener the lisener to remove
     */
    public void removeStateListener(IDownloadMonitorListener listener);
    
    /**
     * Returns the downloader that this monitor corresponds to
     * @return the downloader instance
     */
    public IDownloader getDownloader();
}
