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
package nz.net.kallisti.emusicj.download;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class HTTPDownloadMonitor implements IDownloadMonitor {

	protected DLState state;
	protected HTTPDownloader downloader;
	private final List<IDownloadMonitorListener> listeners;

	public HTTPDownloadMonitor() {
		super();
		listeners = Collections
				.synchronizedList(new ArrayList<IDownloadMonitorListener>());
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
		return downloader.fileLength == -1 ? -1
				: ((double) downloader.bytesDown / downloader.fileLength) * 100;
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
		IDownloadMonitorListener[] listenArr = new IDownloadMonitorListener[listeners
				.size()];
		listenArr = listeners.toArray(listenArr);
		for (IDownloadMonitorListener l : listenArr)
			l.monitorStateChanged(this);
	}

	public IDownloader getDownloader() {
		return downloader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.download.IDownloadMonitor#getFailureCount()
	 */
	public int getFailureCount() {
		return downloader.getFailureCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nz.net.kallisti.emusicj.download.IDownloadMonitor#resetFailureCount()
	 */
	public void resetFailureCount() {
		downloader.resetFailureCount();
	}

}
