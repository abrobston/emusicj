package nz.net.kallisti.emusicj.models.test;

import java.util.ArrayList;
import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.test.TestDownloadMonitor;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;

/**
 * <p>A simple test download model, that returns a number of 
 * TestDownloadMonitors containing test strings.</p> 
 * 
 * $Id:$
 *
 * @author robin
 */
public class TestDownloadsModel implements IDownloadsModel {

	List<IDownloadMonitor> dm;
	/**
	 * Initialise the class, and create some {@link TestDownloadMonitor}s.
	 * @param n the number of monitors to create
	 */
	public TestDownloadsModel(int n) {
		dm = new ArrayList<IDownloadMonitor>();
		for (int i=0; i<n; i++) {
			dm.add(new TestDownloadMonitor("TestDownloadMonitor "+i));
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
		// TODO Auto-generated method stub
		return null;
	}

	public List<IDownloadMonitor> getDownloadMonitors() {
		return dm;
	}

}
