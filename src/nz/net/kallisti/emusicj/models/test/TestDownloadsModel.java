package nz.net.kallisti.emusicj.models.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.test.TestDownloadMonitor;
import nz.net.kallisti.emusicj.download.test.TestDownloader;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;

/**
 * <p>A simple test download model, that returns a number of 
 * TestDownloadMonitors containing test strings.</p> 
 * 
 * $Id$
 *
 * @author robin
 */
public class TestDownloadsModel implements IDownloadsModel {

	List<IDownloader> dl;
	/**
	 * Initialise the class, and create some {@link TestDownloadMonitor}s.
	 * @param n the number of monitors to create
	 */
	public TestDownloadsModel(int n) {
		dl = new ArrayList<IDownloader>();
		for (int i=0; i<n; i++) {
            TestDownloader d = new TestDownloader("TestDownloader "+i,i*2,500);
			dl.add(d);
		}
	}
	
	public void addListener(IDownloadsModelListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.models.IDownloadsModel#removeListener(nz.net.kallisti.emusicj.models.IDownloadsModelListener)
	 */
	public void removeListener(IDownloadsModelListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.models.IDownloadsModel#getDownloaders()
	 */
	public List<IDownloader> getDownloaders() {		
		return Collections.unmodifiableList(dl);
	}

	public List<IDownloadMonitor> getDownloadMonitors() {
        ArrayList<IDownloadMonitor> dm = new ArrayList<IDownloadMonitor>();
        for (IDownloader d : dl)
            dm.add(d.getMonitor());
		return dm;
	}

    /* (non-Javadoc)
     * @see nz.net.kallisti.emusicj.models.IDownloadsModel#addDownload(nz.net.kallisti.emusicj.download.IMusicDownloader)
     */
    public void addDownload(IMusicDownloader dl) {
        // TODO Auto-generated method stub
        
    }

}
