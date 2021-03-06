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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.bindingtypes.WatchFiles;
import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloadMonitorListener;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.dropdir.IDirectoryMonitor;
import nz.net.kallisti.emusicj.dropdir.IDirectoryMonitorListener;
import nz.net.kallisti.emusicj.files.cleanup.ICleanupFiles;
import nz.net.kallisti.emusicj.ipc.IIPCListener;
import nz.net.kallisti.emusicj.ipc.IPCServerClient;
import nz.net.kallisti.emusicj.metafiles.IMetafileLoader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.models.IDownloadsModel;
import nz.net.kallisti.emusicj.models.IDownloadsModelListener;
import nz.net.kallisti.emusicj.strings.IStrings;
import nz.net.kallisti.emusicj.updater.IUpdateFetcher;
import nz.net.kallisti.emusicj.updater.IUpdateFetcherListener;
import nz.net.kallisti.emusicj.urls.IURLFactory;
import nz.net.kallisti.emusicj.view.IEmusicjView;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * This is the main controller for the application. It routes stuff around,
 * ensuring that the view is kept up to date with the system, and that the state
 * is kept up to date with user requests.
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class EmusicjController implements IEmusicjController,
		IDownloadMonitorListener, IDownloadsModelListener,
		IUpdateFetcherListener, IDirectoryMonitorListener,
		IPreferenceChangeListener, IIPCListener {

	private final IEmusicjView view;
	private final IDownloadsModel downloadsModel;
	private boolean noAutoStartDownloads = false;
	private final IPreferences prefs;
	private boolean shuttingDown = false;
	private IPCServerClient server;
	private PollDownloads pollThread;
	private final IDirectoryMonitor dropDirMon;
	private int maxDownloadFailures;
	private final Object monitorStateChangedLock = new Object();
	private boolean monitorStateChangedIsRunning = false;
	private final IMetafileLoader metafileLoader;
	private final IUpdateFetcher updateCheck;
	private final IURLFactory urlFactory;
	private final IStrings strings;
	private final Logger logger;
	private final ICleanupFiles cleanupFiles;
	private final AtomicInteger deferredMetafileStack = new AtomicInteger();
	private final List<String> deferredMetafileLoads = new ArrayList<String>();

	@Inject
	public EmusicjController(IEmusicjView view, IPreferences preferences,
			IDownloadsModel downloadsModel,
			@WatchFiles Provider<IDirectoryMonitor> dropDirMonProvider,
			IMetafileLoader metafileLoader, IUpdateFetcher updateCheck,
			IURLFactory urlFactory, IStrings strings, ICleanupFiles cleanupFiles) {
		this.view = view;
		this.prefs = preferences;
		this.downloadsModel = downloadsModel;
		this.urlFactory = urlFactory;
		this.strings = strings;
		this.cleanupFiles = cleanupFiles;
		this.dropDirMon = dropDirMonProvider.get();
		this.dropDirMon.setListener(this);
		this.metafileLoader = metafileLoader;
		this.updateCheck = updateCheck;
		logger = LogUtils.getLogger(this);
	}

	public void run(String[] args) {
		// Initialise the system
		prefs.addListener(this);
		if (!prefs.getProperty("noServer", "0").equals("1")) {
			// First see if another instance is running, if so, pass our
			// args on
			server = new IPCServerClient(this, new File(prefs.getStatePath()
					+ "port"));
			if (server.getState() == IPCServerClient.CONNECTED) {
				// we just pass the args in and quit
				server.sendData(args);
				return;
			}
		}
		// Any number of errors can occur in here, so we at least allow a clean
		// shutdown
		try {
			maxDownloadFailures = prefs.getMaxDownloadFailures();
			if (view != null)
				view.setState(IEmusicjView.ViewState.STARTUP);
			try {
				downloadsModel.loadState(new FileInputStream(prefs
						.getStatePath()
						+ "downloads.xml"));
			} catch (FileNotFoundException e) {
			}
			downloadsModel.addListener(this);
			pollThread = new PollDownloads();
			pollThread.start();
			for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
				mon.addStateListener(this);
				// add an auto-remove timer if we need to (fixes #41)
				if (mon.getDownloadState() == DLState.FINISHED
						&& prefs.removeCompletedDownloads())
					attachAutoRemoveTimer(mon.getDownloader());
			}
			// Start the drop directory monitoring, if that's what we want to
			// do.
			String dd = prefs.getDropDir();
			if (dd != null && !dd.equals("")) {
				// dropDirMon.setListener(this);
				dropDirMon.setDirToMonitor(new File(dd));
			}
			// Pass the system state on to the view to ensure it's up to date
			if (view != null) {
				view.setDownloadsModel(downloadsModel);
				view.setState(IEmusicjView.ViewState.RUNNING);
				view.pausedStateChanged(noAutoStartDownloads);
			}

			monitorStateChanged(null);
			// Check for updates
			if (prefs.checkForUpdates()) {
				updateCheck.setListener(this);
				updateCheck.setUpdateUrl(urlFactory.getUpdateURL());
				updateCheck.check(strings.getVersion());
			}
			// Load files passed on the command line (this must be pretty much
			// the last thing to happen, in some cases starting without a view
			// configured may cause issues)
			for (String file : args)
				loadMetafile(file);
			// Call the view's event loop
			if (view != null)
				view.processEvents(this);
			// Clean up the program
		} catch (Throwable e) {
			logger
					.log(Level.SEVERE, "Unexpected exception causing shutdown",
							e);
		} finally {
			shutdown();
		}
	}

	/**
	 * This tells all the downloaders to finish, otherwise the threads will keep
	 * running
	 */
	private void shutdown() {
		shuttingDown = true;
		if (server != null)
			server.stopServer();
		if (dropDirMon != null)
			dropDirMon.stopMonitor();
		try {
			saveState();
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
		cleanupFiles.deleteFiles();
		System.exit(0);
	}

	/**
	 * Saves the state of the downloads in progress so that they can be restored
	 * on the next startup.
	 * 
	 * @throws FileNotFoundException
	 */
	private void saveState() throws FileNotFoundException {
		downloadsModel.saveState(new FileOutputStream(prefs.getStatePath()
				+ "downloads.xml"), noAutoStartDownloads);
	}

	/**
	 * Loads a metafile. A metafile may contain any number of files to download.
	 * 
	 * @param file
	 *            the filename of the metafile to load
	 */
	public void loadMetafile(String file) {
		if (deferredMetafileStack.get() > 0) {
			synchronized (deferredMetafileLoads) {
				deferredMetafileLoads.add(file);
			}
			return;
		}
		String fixedFile = file;
		try {
			// Snow Leopard (OSX 10.6 or so) adds 'file://localhost' to the
			// start of pathnames, because it's stupid. We need to remove it.
			String prefix = "file://localhost";
			if (file.startsWith(prefix)) {
				fixedFile = file.substring(prefix.length());
			}
			newDownloads(metafileLoader.load(this, new File(fixedFile)));
		} catch (IOException e) {
			error("Error reading file", e.getMessage());
			logger.log(Level.WARNING, "Unable to read metafile (" + fixedFile
					+ ")", e);
		} catch (UnknownFileException e) {
			error("Error reading file", "The file is of an unknown type\n"
					+ file);
			logger.log(Level.WARNING, "Unknown metafile type (" + fixedFile
					+ ")", e);
		} catch (Exception e) {
			// error("Error reading file", "Something failed while reading the "
			// + "file\n" + file
			// + "\nError details have been written to the " + "terminal.");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			error("Error reading file", "Something failed while reading the "
					+ "file\n" + file + "\nError details are below:\n"
					+ sw.toString());
			logger.log(Level.WARNING, "Unexpected failure reading metafile ("
					+ fixedFile + ")", e);
		}
		// this means that if the machine crashes, the downloads in progress
		// are recorded.
		try {
			saveState();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error saving downloader state", e);
		}
	}

	public void loadMetafile(String path, String[] fileNames) {
		for (String f : fileNames)
			loadMetafile(path + File.separatorChar + f);
	}

	/**
	 * Invokes the view to notify the user of an error condition. If the system
	 * is still initialising, then the errors will be queued up and displayed
	 * later.
	 * 
	 * @param msgTitle
	 *            The title of the error
	 * @param msg
	 *            The message contents
	 */
	private void error(String msgTitle, String msg) {
		view.error(msgTitle, msg);
	}

	/**
	 * Adds a new set of downloaders to the model.
	 * 
	 * @param downloaders
	 *            the downloaders to add
	 */
	public void newDownloads(List<IDownloader> downloaders) {
		if (downloaders == null)
			return;
		boolean oldState = noAutoStartDownloads;
		noAutoStartDownloads = true;
		for (IDownloader dl : downloaders) {
			downloadsModel.addDownload(dl);
			dl.getMonitor().addStateListener(this);
		}
		noAutoStartDownloads = oldState;
		monitorStateChanged(null);
	}

	/**
	 * This is triggered when a download changes state. We count the number that
	 * are in the state DLState.DOWNLOADING or CONNECTING. If there is less than
	 * the minimum (defined in Constants), we start the first one that is in a
	 * state of NOTSTARTED.
	 * 
	 * @param monitor
	 *            the monitor that changed status. This may safely be null in
	 *            order to just ensure that downloads are happening.
	 */
	public void monitorStateChanged(IDownloadMonitor monitor) {
		if (shuttingDown)
			return;
		synchronized (monitorStateChangedLock) {
			if (monitorStateChangedIsRunning)
				return;
			// Poor-mans synchronization, can't use synchronized on the method
			// as it may cause deadlocks
			monitorStateChangedIsRunning = true;
		}
		try {
			// If we're supposed to auto-remove downloads, then we fire up a
			// thread for that here.
			if (monitor != null
					&& monitor.getDownloadState() == DLState.FINISHED
					&& prefs.removeCompletedDownloads()) {
				final IDownloader downloader = monitor.getDownloader();
				attachAutoRemoveTimer(downloader);
			}
			int inProgress = 0, finished = 0;
			int total = downloadsModel.getDownloadMonitors().size();
			for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
				if (mon.getDownloadState() == DLState.DOWNLOADING
						|| mon.getDownloadState() == DLState.CONNECTING) {
					inProgress++;
				}
				if (mon.getDownloadState() == DLState.FINISHED) {
					finished++;
				}
			}
			if (view != null)
				view.downloadCount(inProgress, finished, total);
			// This is down here so that the view still gets notified about
			// what's going on
			if (noAutoStartDownloads)
				return;
			int num = prefs.getMinDownloads() - inProgress;
			if (num > 0) {
				for (IDownloadMonitor mon : downloadsModel
						.getDownloadMonitors()) {
					// Find downloads that are not started, or failed and not
					// the same one that just changed
					if (mon.getDownloadState() == DLState.NOTSTARTED
							|| ((monitor != mon) && mon.getDownloadState() == DLState.FAILED)
							&& mon.getFailureCount() < maxDownloadFailures) {
						mon.getDownloader().start();
						num--;
						if (num <= 0)
							break;
					}
				}
			}
		} finally {
			monitorStateChangedIsRunning = false;
		}
	}

	/**
	 * This adds a timer thread that will automatically remove the provided
	 * downloader from the system after 30 seconds.
	 * 
	 * @param downloader
	 *            the download that will be removed
	 */
	private void attachAutoRemoveTimer(final IDownloader downloader) {
		Thread removalThread = new Thread() {
			@Override
			public void run() {
				try {
					// Wait 30 seconds
					Thread.sleep(30000);
					downloadsModel.removeDownload(downloader);
				} catch (InterruptedException e) {
				}
			}
		};
		removalThread.setDaemon(true);
		removalThread.start();
	}

	public void startDownload(IDownloader dl) {
		dl.resetFailureCount();
		dl.start();
	}

	public void pauseDownload(IDownloader dl) {
		dl.pause();
	}

	public void cancelDownload(IDownloader dl) {
		dl.stop();
	}

	public void requeueDownload(IDownloader dl) {
		dl.resetFailureCount();
		dl.requeue();
	}

	public void pauseDownloads() {
		noAutoStartDownloads = true;
		view.pausedStateChanged(noAutoStartDownloads);
		for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
			if (mon.getDownloadState() == DLState.DOWNLOADING
					|| mon.getDownloadState() == DLState.CONNECTING) {
				mon.getDownloader().pause();
			}
		}
		monitorStateChanged(null);
	}

	public void resumeDownloads() {
		for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
			if (mon.getDownloadState() == DLState.PAUSED) {
				mon.getDownloader().start();
			}
		}
		noAutoStartDownloads = false;
		view.pausedStateChanged(noAutoStartDownloads);
		// This hopefully stops twice as many tracks restarting after resuming
		// from pause. My guess is that it was getting to here fast enough
		// the the download threads hadn't had a chance to properly fire up.
		// This shouldn't really be needed anyway. (Bug #15)
		// monitorStateChanged(null);
	}

	public void cancelDownloads() {
		boolean oldState = noAutoStartDownloads;
		noAutoStartDownloads = true; // make sure crazy doesn't happen
		for (IDownloadMonitor mon : downloadsModel.getDownloadMonitors()) {
			if (mon.getDownloadState() != DLState.FINISHED)
				mon.getDownloader().stop();
		}
		noAutoStartDownloads = oldState;
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

	public void removeFailedDownloads() {
		ArrayList<IDownloader> toRemove = new ArrayList<IDownloader>();
		for (IDownloader dl : downloadsModel.getDownloaders()) {
			if (dl.getMonitor().getDownloadState() == DLState.FAILED
					&& dl.getFailureCount() >= maxDownloadFailures) {
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

	public void newFile(IDirectoryMonitor mon, File file) {
		loadMetafile(file.toString());
		file.delete();
	}

	public void preferenceChanged(Pref pref) {
		if (pref == Pref.DROP_DIR) {
			String dd = prefs.getDropDir();
			if (dropDirMon != null)
				if (dd != null && !dd.equals(""))
					dropDirMon.setDirToMonitor(new File(dd));
				else
					dropDirMon.setDirToMonitor(null);
		}
	}

	public void deferMetafileLoad() {
		deferredMetafileStack.incrementAndGet();
	}

	public void restoreMetafileLoad() {
		int count = deferredMetafileStack.decrementAndGet();
		if (count <= 0) {
			if (count < 0) {
				logger
						.warning("Deferred metafile stack count underflow: this shouldn't be possible");
				deferredMetafileStack.set(0);
			}
			processDeferredMetafileLoads();
		}
	}

	/**
	 * This runs through the list of deferred metafiles and forces them to be
	 * loaded now, clearing the list afterwards.
	 */
	private void processDeferredMetafileLoads() {
		synchronized (deferredMetafileLoads) {
			for (String mf : deferredMetafileLoads)
				loadMetafile(mf);
			deferredMetafileLoads.clear();
		}
	}

	/**
	 * Every two minutes this thread makes the controller check the downloads
	 * and ensure that the minimum is currently active. Its main use is to
	 * ensure that if a download fails, and if it is the only one going, that it
	 * gets retried.
	 */
	public class PollDownloads extends Thread {

		private boolean done = false;

		@Override
		public void run() {
			try {
				while (!done) {
					Thread.sleep(120000);
					monitorStateChanged(null);
				}
				// If we get interrupted, then shut down the thread
			} catch (InterruptedException e) {
			}
		}

		public void finish() {
			done = true;
			this.interrupt();
		}

	}

	/**
	 * Catch data coming over the IPC system, load it as files. It's possible
	 * for other options to be in here too, although not yet.
	 * 
	 * @param data
	 *            the data
	 */
	public void ipcData(String[] data) {
		for (String file : data) {
			loadMetafile(file);
		}
	}

	public void networkIssuesDetected() {
		view.connectionIssues();
	}
}
