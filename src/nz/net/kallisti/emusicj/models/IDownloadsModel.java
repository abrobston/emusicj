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
package nz.net.kallisti.emusicj.models;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;

/**
 * This is a model that tracks the downloads that are currently in the system.
 * It has listeners that are signalled if something changes (such as downloads
 * being added or removed)
 * 
 * $Id$
 * 
 * @author robin
 */
public interface IDownloadsModel {

	public void addListener(IDownloadsModelListener listener);

	public void removeListener(IDownloadsModelListener listener);

	public List<IDownloader> getDownloaders();

	public List<IDownloadMonitor> getDownloadMonitors();

	public void addDownload(IDownloader dl);

	public void removeDownload(IDownloader dl);

	/**
	 * Removes downloads from the model
	 * 
	 * @param toRemove
	 *            the downloads to remove
	 */
	public void removeDownloads(List<IDownloader> toRemove);

	/**
	 * This requests the model to save the state of all the downloaders to the
	 * provided stream
	 * 
	 * @param stream
	 *            the stream to save to
	 * @param ignorePause
	 *            if true, then downloads in a 'paused' state will be instead
	 *            saved as 'not started'
	 * @return true on success, false on failure
	 */
	public boolean saveState(OutputStream stream, boolean ignorePause);

	public void loadState(InputStream stream);

}
