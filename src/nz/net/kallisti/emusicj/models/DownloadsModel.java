package nz.net.kallisti.emusicj.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.test.TestDownloadMonitor;

/**
 * <p>This is the download model. It keeps tabs on what downloads exists
 * and notifies listeners of changes.</p> 
 * 
 * $Id: TestDownloadsModel.java 21 2005-10-17 21:42:23 +1300 (Mon, 17 Oct 2005) robin $
 *
 * @author robin
 */
public class DownloadsModel implements IDownloadsModel {

	List<IDownloader> downloads;
    private List<IDownloadsModelListener> listeners = 
        new ArrayList<IDownloadsModelListener>();
    
	/**
	 * Initialise the class, and create some {@link TestDownloadMonitor}s.
	 * @param n the number of monitors to create
	 */
	public DownloadsModel() {
		downloads = new ArrayList<IDownloader>();
	}
	
	public void addListener(IDownloadsModelListener listener) {
	    listeners.add(listener);
	}

	public void removeListener(IDownloadsModelListener listener) {
	    listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.models.IDownloadsModel#getDownloaders()
	 */
	public List<IDownloader> getDownloaders() {		
		return Collections.unmodifiableList(downloads);
	}

	public List<IDownloadMonitor> getDownloadMonitors() {
        ArrayList<IDownloadMonitor> dm = new ArrayList<IDownloadMonitor>();
        for (IDownloader d : downloads)
            dm.add(d.getMonitor());
		return dm;
	}

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.models.IDownloadsModel#addDownload(nz.net.kallisti.emusicj.download.IMusicDownloader)
     */
    public void addDownload(IDownloader dl) {
        downloads.add(dl);
        notifyListeners();
    }

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.models.IDownloadsModel#removeDownloads(java.util.List)
     */
    public void removeDownloads(List<IDownloader> toRemove) {
        if (toRemove.size() != 0) {
            for (IDownloader dl : toRemove)
                downloads.remove(dl);
            notifyListeners();
        }
    }

    public void removeDownload(IDownloader dl) {
        downloads.remove(dl);
        notifyListeners();
    }
    
    private void notifyListeners() {
        for (IDownloadsModelListener l : listeners)
            l.downloadsModelChanged(this);
    }

}
