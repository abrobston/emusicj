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
package nz.net.kallisti.emusicj.controller;

import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;

/**
 * <p>
 * This interface defines the functions that need to be provided by a
 * controller. The purpose of the controller is to tell the view what to
 * display, and update the state of the system in response to what the user
 * inputs (typically done via the view also)
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public interface IEmusicjController {

	/**
	 * This starts the controller running. It will perform any initialisation
	 * tasks that need to be done, and then pass control to the event loop of
	 * the view (or it may do that in a seperate thread if it needs to). This
	 * will only return when the program is to shut down.
	 * 
	 * @param args
	 *            the command line arguments, they should just be filenames of
	 *            metafiles to add to the queue
	 */
	public void run(String[] args);

	/**
	 * Tells the controller about a set of new {@link IMusicDownloader}
	 * instances.
	 * 
	 * @param downloaders
	 *            the new downloaders for the controller to be aware of
	 */
	public void newDownloads(List<IDownloader> downloaders);

	/**
	 * Tells the controller to load a metadata file and add the downloads
	 * contained in it to the download system.
	 * 
	 * @param file
	 *            the metadata file to load
	 */
	public void loadMetafile(String file);

	/**
	 * Tells the download to start
	 * 
	 * @param dl
	 */
	public void startDownload(IDownloader dl);

	/**
	 * Tells the download to pause
	 * 
	 * @param dl
	 */
	public void pauseDownload(IDownloader dl);

	/**
	 * Tells the download to stop (functionally the same as pause, but may be
	 * displayed differently, and more likely to be removed)
	 * 
	 * @param dl
	 */
	public void cancelDownload(IDownloader dl);

	/**
	 * Tells the download to be put back into the waiting state
	 * 
	 * @param dl
	 */
	public void requeueDownload(IDownloader dl);

	/**
	 * Pauses all the downloads that are currently running, and sets a flag to
	 * say that no more will be automatically started.
	 */
	public void pauseDownloads();

	/**
	 * Restarts all the paused downloads, and unsets the flag used in
	 * {@link pauseDownloads}, so that downloads are automatically started
	 * again.
	 */
	public void resumeDownloads();

	/**
	 * Removes all downloads of a given state from the download model
	 * 
	 * @param state
	 *            the state of the downloads to be removed
	 */
	public void removeDownloads(DLState state);

	/**
	 * This removes all downloads that are in a fail state, and have their
	 * failure count value at the maximum
	 */
	public void removeFailedDownloads();

	/**
	 * @param path
	 * @param fileNames
	 */
	public void loadMetafile(String path, String[] fileNames);

	/**
	 * Marks all downloads that are currently on the screen as 'Cancelled',
	 * unless they're already marked 'Finished'
	 */
	public void cancelDownloads();

	/**
	 * If network connectivity issues are detected, this should be notified.
	 */
	public void networkIssuesDetected();

	/**
	 * If there is some reason that metafiles shouldn't be loaded just yet, such
	 * as if file paths are about to be changed, this should be called. Note
	 * that it stacks, so two calls of this will require two calls to
	 * {@link #restoreMetafileLoad()} before they are processed.
	 */
	public void deferMetafileLoad();

	/**
	 * This undoes a call to {@link #deferMetafileLoad()}. If this cancels out
	 * the remaining defer call, then any deferred loads are processed now.
	 */
	public void restoreMetafileLoad();

}
