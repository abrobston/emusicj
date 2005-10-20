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

    public HTTPMusicDownloadMonitor() {
        super();
    }

    /**
     * @param downloader
     */
    public HTTPMusicDownloadMonitor(HTTPMusicDownloader downloader) {
        this.downloader = downloader;
    }

    public String getName() {
        return downloader.getTrackName()+" - "+downloader.getAlbumName() +
        " - "+downloader.getArtistName();
    }

    public double getDownloadPercent() {
        return downloader.fileLength==-1?
                -1:((double)downloader.bytesDown/downloader.fileLength)*100;
    }

    public long getBytesDown() {
        return downloader.bytesDown;
    }

    public long getTotalBytes() {
        return downloader.fileLength;
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
