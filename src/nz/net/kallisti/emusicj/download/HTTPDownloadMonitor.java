package nz.net.kallisti.emusicj.download;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <p></p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class HTTPDownloadMonitor implements IDownloadMonitor {
	
	private DLState state;
	private HTTPDownloader downloader;
	private List<IDownloadMonitorListener> listeners;
	
	public HTTPDownloadMonitor() {
		super();
		listeners = Collections.synchronizedList(new ArrayList<IDownloadMonitorListener>());
	}
	
	/**
	 * @param downloader
	 */
	public HTTPDownloadMonitor(HTTPDownloader downloader) {
		this();
		this.downloader = downloader;
	}
	
	public String getName() {
		return downloader.getOutputFile().toString();
	}
	
	public double getDownloadPercent() {
		return downloader.fileLength==-1?
				-1:((double)downloader.bytesDown/downloader.fileLength)*100;
	}
	
	public long getBytesDown() {
		synchronized (downloader) {
			return downloader.bytesDown;
		}
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
		IDownloadMonitorListener[] listenArr = 
			new IDownloadMonitorListener[listeners.size()];
		listenArr = listeners.toArray(listenArr);
		for (IDownloadMonitorListener l : listenArr)
			l.monitorStateChanged(this);
	}
	
	public IDownloader getDownloader() {
		return downloader;
	}
	
}
