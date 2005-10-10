package nz.net.kallisti.emusicj.download;

/**
 * <p>This is class that is instantiated by an IDownloader subclass to allow the
 * state of the download progress to be tracked by something.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public interface DownloadMonitor {

    public enum DLState { NOTSTARTED, CONNECTING, DOWNLOADING, PAUSED, STOPPED,
        FINISHED } 
    
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

}
