package nz.net.kallisti.emusicj.controller;

import java.util.List;

import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IMusicDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.view.IEMusicView;

/**
 * <p>This interface defines the functions that need to be provided by a
 * controller. The purpose of the controller is to tell the view what to
 * display, and update the state of the system in response to what the
 * user inputs (typically done via the view also)</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public interface IEMusicController {

    /**
     * Ties a view in to the controller. If the view isn't present, then the
     * controller should do something sensible, such as not try to talk to it,
     * abort on a call to run(), or run in some form of headless mode.
     * @param view the view used to interact with the user
     */
    public void setView(IEMusicView view);

    /**
     * This starts the controller running. It will perform any initialisation 
     * tasks that need to be done, and then pass control to the event loop
     * of the view (or it may do that in a seperate thread if it needs to).
     * This will only return when the program is to shut down. 
     * @param args the command line arguments, they should just be filenames
     * of metafiles to add to the queue
     */
    public void run(String[] args);

    /**
     * Tells the controller about a set of new {@link IMusicDownloader}
     * instances.
     * @param downloaders the new downloaders for the controller to be aware of
     */
    public void newDownloads(List<IMusicDownloader> downloaders);

	/**
	 * Tells the controller to load a metadata file and add the downloads
	 * contained in it to the download system.
	 * @param file the metadata file to load
	 */
	public void loadMetafile(String file);

	/**
	 * Tells the download to start
	 * @param dl
	 */
	public void startDownload(IDownloader dl);

	/**
	 * Tells the download to pause
	 * @param dl
	 */
	public void pauseDownload(IDownloader dl);

	/**
	 * Tells the download to stop (functionally the same as pause, but may
	 * be displayed differently, and more likely to be removed)
	 * @param dl
	 */
	public void stopDownload(IDownloader dl);

    /**
     * Pauses all the downloads that are currently running, and sets a flag
     * to say that no more will be automatically started.
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
     * @param state the state of the downloads to be removed 
     */
    public void removeDownloads(DLState state);

}
