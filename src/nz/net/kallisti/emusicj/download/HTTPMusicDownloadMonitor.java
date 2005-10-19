package nz.net.kallisti.emusicj.download;

import java.util.ArrayList;


/**
 * <p></p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class HTTPMusicDownloadMonitor implements IDownloadMonitor {

    private DLState state;
    private HTTPMusicDownloader downloader;
    private ArrayList<IDownloadMonitorListener> listeners =
        new ArrayList<IDownloadMonitorListener>();

    /**
     * 
     */
    public HTTPMusicDownloadMonitor() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param downloader
     */
    public HTTPMusicDownloadMonitor(HTTPMusicDownloader downloader) {
        this.downloader = downloader;
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getDownloadPercent()
     */
    public double getDownloadPercent() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getBytesDown()
     */
    public long getBytesDown() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getTotalBytes()
     */
    public long getTotalBytes() {
        // TODO Auto-generated method stub
        return 0;
    }

    public DLState getDownloadState() {
        return state;
    }

    public void addStateListener(IDownloadMonitorListener listener) {
        listeners.add(listener);
    }

    public void removeStateListener(IDownloadMonitorListener listener) {
        listeners.remove(listener);
    }

    void setState(DLState st) {
        state = st;
        for (IDownloadMonitorListener l : listeners)
            l.monitorStateChanged(this);
    }

    public IDownloader getDownloader() {
        return downloader;
    }

}
