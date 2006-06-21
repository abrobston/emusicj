/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */
package nz.net.kallisti.emusicj.download.test;

import java.util.ArrayList;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloader;

/**
 * <p>A simple test class that always returns the string it was created with.</p>
 * 
 * $Id$
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

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getFailureCount()
	 */
	public int getFailureCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#resetFailureCount()
	 */
	public void resetFailureCount() {
		// TODO Auto-generated method stub
		
	}

}
