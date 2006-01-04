package nz.net.kallisti.emusicj.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nz.net.kallisti.emusicj.Constants;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.ipc.IPCServerClient;
import nz.net.kallisti.emusicj.metafiles.MetafileLoader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;
import nz.net.kallisti.emusicj.models.DownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;
import nz.net.kallisti.emusicj.updater.IUpdateCheckListener;
import nz.net.kallisti.emusicj.updater.UpdateCheck;
import nz.net.kallisti.emusicj.view.IEMusicView;

/**
 * <p>This is the main controller for the application. It routes stuff around,
 * ensuring that the view is kept up to date with the system, and that the
 * state is kept up to date with user requests.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class EMusicController implements IEMusicController, 
IDownloadMonitorListener, IDownloadsModelListener, IUpdateCheckListener {
	
	private IEMusicView view;
	private IDownloadsModel downloadsModel = new DownloadsModel();
	private boolean noAutoStartDownloads = false;
	private Preferences prefs = Preferences.getInstance();
	private boolean shuttingDown = false;
	private IPCServerClient server;
	private PollDownloads pollThread;
	
	public EMusicController() {
		super();
	}
	
	public void setView(IEMusicView view) {
		this.view = view;
		view.setController(this);
	}
	
	public void run(String[] args) {
		// Initialise the system
		// First see if another instance is running, if so, pass our args on
		server = new IPCServerClient(this);
		if (server.getState() == IPCServerClient.CONNECTED) {
			// we just pass the args in and quit
			server.sendData(args);
			return;
		}
		if (view != null)
			view.setState(IEMusicView.ViewState.STARTUP);
		try {
			downloadsModel.loadState(new FileInputStream(prefs.statePath+"downloads.xml"));
		} catch (FileNotFoundException e) {	}
		downloadsModel.addListener(this);
		pollThread = new PollDownloads();
		pollThread.start();
		for (String file : args)
			loadMetafile(file);
		for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
			mon.addStateListener(this);
		}
		// Pass the system state on to the view to ensure it's up to date
		if (view != null)
			view.setDownloadsModel(downloadsModel);
		
		if (view != null)
			view.setState(IEMusicView.ViewState.RUNNING);
		//List<IDownloader> downloads = downloadsModel.getDownloaders();
		//if (downloads.size() > 0)
		//	downloads.get(0).start();
		monitorStateChanged(null);
		// Check for updates
		if (prefs.checkForUpdates()) {
			UpdateCheck update = new UpdateCheck(this, Constants.UPDATE_URL);
			update.check(Constants.VERSION);
		}
		// Call the view's event loop
		if (view != null)
			view.processEvents(this);
		// Clean up the program
		shutdown();
	}
	
	/**
	 * This tells all the downloaders to finish, otherwise the threads
	 * will keep running
	 */
	private void shutdown() {
		shuttingDown  = true;
		if (server != null)
			server.stopServer();
		try {
			downloadsModel.saveState(new FileOutputStream(prefs.statePath+"downloads.xml"));
		} catch (FileNotFoundException e) {
			System.err.println("Warning: error saving download information");
			e.printStackTrace();
		}
		pollThread.finish();
		List<IDownloader> dls = downloadsModel.getDownloaders();
		for (IDownloader dl : dls) {
			dl.hardStop();
		}
		prefs.save();
	}
	
	/**
	 * Loads a metafile. A metafile may contain any number of files to download.
	 * @param file the filename of the metafile to load
	 */
	public void loadMetafile(String file) {
		try {
			newDownloads(MetafileLoader.load(this, new File(file)));
		} catch (IOException e) {
			error("Error reading file",e.getMessage());
		} catch (UnknownFileException e) {
			error("Error reading file","The file is of an unknown type\n"+file);
		}
	}
	
	public void loadMetafile(String path, String[] fileNames) {
		for (String f : fileNames)
			loadMetafile(path+File.separatorChar+f);
	}
	
	
	/**
	 * Invokes the view to notify the user of an error condition. If the system
	 * is still initialising, then the errors will be queued up and displayed
	 * later.
	 * @param msgTitle The title of the error 
	 * @param msg The message contents
	 */
	private void error(String msgTitle, String msg) {
		view.error(msgTitle, msg);
	}
	
	/**
	 * Adds a new set of downloaders to the model.
	 * @param downloaders the downloaders to add
	 */
	public void newDownloads(List<IDownloader> downloaders) {
		if (downloaders == null)
			return;
		for (IDownloader dl : downloaders) {
			downloadsModel.addDownload(dl);
			dl.getMonitor().addStateListener(this);
		}
	}
	
	/**
	 * This is triggered when a download changes state. We count the
	 * number that are in the state DLState.DOWNLOADING or CONNECTING. If there 
	 * is less than the minimum (defined in Constants), we start the first one 
	 * that is in a state of NOTSTARTED.
	 *  
	 * @param monitor the monitor that changed status. This may safely be null
	 * in order to just ensure that downloads are happening. 
	 */
	public void monitorStateChanged(IDownloadMonitor monitor) {
		if (noAutoStartDownloads)
			return;
		if (shuttingDown)
			return;
		int count = 0, finished = 0;
		int total = downloadsModel.getDownloadMonitors().size();
		for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
			if (mon.getDownloadState() == DLState.DOWNLOADING ||
					mon.getDownloadState() == DLState.CONNECTING) {
				count++;
			}
			if (mon.getDownloadState() == DLState.FINISHED) {
				finished++;
			}
		}
		if (view != null)
			view.downloadCount(count, finished, total);
		int num = prefs.getMinDownloads() - count;
		if (num > 0) {
			for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
				// Find downloads that are not started, or failed and not
				// the same one that just changed
				if (mon.getDownloadState() == DLState.NOTSTARTED ||
						((monitor != mon) && mon.getDownloadState() == DLState.FAILED)) {
					mon.getDownloader().start();
					num--;
					if (num <= 0)
						break;
				}
			}
		}
	}
	
	public void startDownload(IDownloader dl) {
		dl.start();
	}
	
	public void pauseDownload(IDownloader dl) {
		dl.pause();
	}
	
	public void stopDownload(IDownloader dl) {
		dl.stop();
	}
	
	public void requeueDownload(IDownloader dl) {
		dl.requeue();
	}
	
	public void pauseDownloads() {
		noAutoStartDownloads = true;
		for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
			if (mon.getDownloadState() == DLState.DOWNLOADING ||
					mon.getDownloadState() == DLState.CONNECTING) {
				mon.getDownloader().pause();
			}
		}
	}
	
	public void resumeDownloads() {
		for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
			if (mon.getDownloadState() == DLState.PAUSED) {
				mon.getDownloader().start();
			}
		}
		noAutoStartDownloads = false;
		monitorStateChanged(null);
	}
	
    public void cancelDownloads() {
        for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
            if (mon.getDownloadState() == DLState.FINISHED)
                mon.getDownloader().stop();           
        }
        monitorStateChanged(null);
    }
    
	public void removeDownloads(DLState state) {
		ArrayList<IDownloader> toRemove = new ArrayList<IDownloader>();
		for (IDownloader dl : downloadsModel.getDownloaders()) {
			if (dl.getMonitor().getDownloadState() == state) {
				toRemove.add(dl);
			}
		}
		downloadsModel.removeDownloads(toRemove);
	}
	
	public void downloadsModelChanged(IDownloadsModel model) {
		monitorStateChanged(null);
	}

	public void updateAvailable(String newVersion) {
		if (view != null)
			view.updateAvailable(newVersion);
	}

	
	/**
	 * Every two minutes this thread makes the controller check the downloads
	 * and ensure that the minimum is currently active. Its main use is to
	 * ensure that if a download fails, and if it is the only one going, that
	 * it gets retried. 
	 */
	public class PollDownloads extends Thread {

		private boolean done = false;
		
		public void run() {
			try {
				while (!done) {
					Thread.sleep(120000);
					monitorStateChanged(null);
				}
				// If we get interrupted, then shut down the thread
			} catch (InterruptedException e) {}
		}
		
		public void finish() {
			done = true;
			this.interrupt();
		}
		
	}
	
}
