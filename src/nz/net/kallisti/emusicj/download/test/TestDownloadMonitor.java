package nz.net.kallisti.emusicj.download.test;

import java.util.ArrayList;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloader;

/**
 * <p>A simple test class that always returns the string it was created with.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class TestDownloadMonitor implements IDownloadMonitor {
	
	private TestDownloader downloader;
    private ArrayList<IDownloadMonitorListener> listeners =
        new ArrayList<IDownloadMonitorListener>();
    private DLState state;

    /**
     * This one gets all its info from a TestDownloader
     * @param downloader
     */
    public TestDownloadMonitor(TestDownloader downloader) {
        this.downloader = downloader;
        this.state = downloader.state;
    }

    public String getName() {
		return downloader.name;
	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getDownloadPercent()
	 */
	public double getDownloadPercent() {
		return downloader.pc;
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

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getDownloadState()
	 */
	public DLState getDownloadState() {
		return state;
	}
    
    void setState(DLState st) {
        state = st;
        for (IDownloadMonitorListener l : listeners)
            l.monitorStateChanged(this);
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#addListener(nz.net.kallisti.emusicj.download.IDownloadMonitorListener)
     */
    public void addStateListener(IDownloadMonitorListener listener) {
        listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#removeListener(nz.net.kallisti.emusicj.download.IDownloadMonitorListener)
     */
    public void removeStateListener(IDownloadMonitorListener listener) {
        listeners.remove(listener);
    }

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getDownloader()
	 */
	public IDownloader getDownloader() {
		return downloader;
	}

}
