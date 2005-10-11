package nz.net.kallisti.emusicj.download.test;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;

/**
 * <p>A simple test class that always returns the string it was created with.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class TestDownloadMonitor implements IDownloadMonitor {

	private String name;

	public TestDownloadMonitor(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getDownloadState()
	 */
	public DLState getDownloadState() {
		// TODO Auto-generated method stub
		return null;
	}

}
