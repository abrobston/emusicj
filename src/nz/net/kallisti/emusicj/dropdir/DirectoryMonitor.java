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
package nz.net.kallisti.emusicj.dropdir;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

/**
 * <p>This class monitors a directory, checking it every so often (default 30 
 * seconds). Whenever it sees a file that matches a supplied {@link FilenameFilter} 
 * pattern, it starts watching it. If that file doesn't change after a while
 * (default also 30 seconds), it notifies a {@link IDirectoryMonitorListener}
 * about it. This filename will not be notified about again, unless it is seen
 * to be deleted and then come back.</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class DirectoryMonitor implements IDirectoryMonitor {

	private FilenameFilter filenameFilter;
	private File dirToMonitor;
	private IDirectoryMonitorListener listener;
	private Set<File> ignoreList = Collections.synchronizedSet(new HashSet<File>());
	private int checkTime = 30000;
	private int verifyTime = 30000;
	private MonitorThread monitorThread;
	private Set<FileMonitorThread> fileMonitorThreads =
		Collections.synchronizedSet(new HashSet<FileMonitorThread>());

	/**
	 * Default constructor. Watches for all files in the current directory.
	 * Won't notify about any files that are currently in the directory
	 * (there's not really much point, as there is noone to tell). Probably
	 * best to avoid this one, it's not that useful.
	 */
	public DirectoryMonitor() {
		super();
		filenameFilter = new FilenameFilter() {
			public boolean accept(File file, String name) {
				return true;
			}
		};
		dirToMonitor = new File(".");
		buildIgnoreList();
		startMonitor();
	}
	
	/**
	 * Creates an instance that monitors the current directory, will notify
	 * of any files found at startup, has no filter.
	 * @param listener the listener to notify when files are found
	 */
	public DirectoryMonitor(IDirectoryMonitorListener listener) {
		this.listener = listener;
		filenameFilter = new FilenameFilter() {
			public boolean accept(File file, String name) {
				return true;
			}
		};
		dirToMonitor = new File(".");
		startMonitor();
	}
	
	/**
	 * Creates an instance, notifies of any files found at startup.
	 * @param listener the listener to notify
	 * @param filter the filter to use
	 * @param dir the directory to monitor
	 */
	public DirectoryMonitor(IDirectoryMonitorListener listener, 
			FilenameFilter filter, File dir) {
		this(listener, filter, dir, true);
	}
	
	/**
	 * This constructor is created with guice, with its filter supplied. 
	 * @param filter the filter that recognises files we want
	 */
	@Inject
	public DirectoryMonitor(FilenameFilter filter) {
		this(null, filter, null, true);
	}
	
	/**
	 * Creates an instance
	 * @param listener the listener to notify
	 * @param filter the filename filter to use
	 * @param dir the directory to monitor
	 * @param initial if true then files found at startup are reported, if
	 * false they aren't.
	 */
	public DirectoryMonitor(IDirectoryMonitorListener listener, 
			FilenameFilter filter, File dir, boolean initial) {
		this.listener = listener;
		filenameFilter = filter;
		dirToMonitor = dir;
		if (dir != null) {
			if (!initial)
				buildIgnoreList();
			startMonitor();
		}
	}
	
	/**
	 * Adds the files currently in the directory to the ignore list so that
	 * they won't be picked up later.
	 */
	protected void buildIgnoreList() {
		File[] files = dirToMonitor.listFiles(filenameFilter);
		for (File f : files)
			ignoreList.add(f);
	}
	
	/**
	 * Starts the monitor thread running.
	 */
	protected void startMonitor() {
		monitorThread = new MonitorThread(checkTime);
		monitorThread.start();
	}
	
	/**
	 * Used to shut down the monitor. Must be called prior to the program being
	 * shut down in order to allow the JVM to exit.
	 */
	public void stopMonitor() {
		if (monitorThread != null)
			monitorThread.shutdown();
		for (FileMonitorThread fm : fileMonitorThreads)
			fm.shutdown();
	}

	/**
	 * This allows the directory being monitored to be changed.
	 * @param file the new directory to monitor
	 */
	public void setDirToMonitor(File dir) {
		dirToMonitor = dir;
		if (monitorThread == null) {
			startMonitor();
		}
	}
	
	public void setListener(IDirectoryMonitorListener listener) {
		this.listener = listener;
	}
	
	protected void startFileMonitor(File f) {
		FileMonitorThread fm = new FileMonitorThread(verifyTime, f);
		fileMonitorThreads.add(fm);
		fm.start();
	}
	
	protected void notifyListeners(File f) {
		if (listener != null)
			listener.newFile(this, f);
	}
	
	/**
	 * This thread monitors a directory, watching for new files. Whenever a new 
	 * file is found it calls {@link startFileMonitor} which can then watch that
	 * new file to see what happens.  
	 */
	protected class MonitorThread extends Thread {
		
		private int checkTime;
		private volatile boolean stop = false;

		/**
		 * Creates an instance of the monitor thread with initial parameters.
		 * @param checkTime the time to delay between checking the directory
		 * @param verifyTime the interval during which a file must not change
		 * modified time in order to be notified about. 
		 */
		public MonitorThread(int checkTime) {
			this.checkTime = checkTime;
		}
		
		public void run() {
			this.setName("Monitoring "+dirToMonitor);
			while (!stop) {
				if (dirToMonitor != null) {
					File[] files = dirToMonitor.listFiles(filenameFilter);
					if (files != null) {
						HashSet<File> set = new HashSet<File>(files.length);
						for(File f : files)
							set.add(f);
						set.removeAll(ignoreList);
						for (File f : set) {
							ignoreList.add(f);
							startFileMonitor(f);
						}
					}
				}
				try {
					Thread.sleep(checkTime);
				} catch (InterruptedException e) {}
			}
		}
		
		public void shutdown() {
			stop = true;
			this.interrupt();
		}
	}
	
	protected class FileMonitorThread extends Thread {

		private boolean stop = false;
		private int verifyTime;
		private File file;
		
		public FileMonitorThread(int verifyTime, File file) {
			this.verifyTime = verifyTime;
			this.file = file;
		}
		
		public void run() {
			this.setName("Monitoring file "+file);
			long time = file.lastModified();
			if (time == 0L) {
				System.err.println("Error monitoring file: "+file);
				shutdown();
				return; // Error, but we have no way of notifying, so we ignore
			}
			long newTime = time;
			do {
				time = newTime;
				try {
					Thread.sleep(verifyTime);
				} catch (InterruptedException e) {}
				newTime = file.lastModified();
				if (newTime == 0L) {
					System.err.println("Error monitoring file: "+file);
					shutdown();
					return;					
				}
			} while (newTime != time && !stop);
			if (stop) return;
			notifyListeners(file);
			file.deleteOnExit();
			shutdown();
		}
		
		public void shutdown() {
			stop = true;
			this.interrupt();
			fileMonitorThreads.remove(this);
		}
		
	}

}
