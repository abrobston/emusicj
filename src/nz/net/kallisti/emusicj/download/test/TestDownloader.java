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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.download.mime.IMimeType;

import org.w3c.dom.Element;

/**
 * <p>
 * This simulates a download in progress
 * </p>
 * 
 * <p>
 * $Id$
 * </p>
 * 
 * @author robin
 */
public class TestDownloader implements IDownloader {

	private int inital;
	private double speed;

	String name;
	double pc;
	DLState state;
	private final TestDownloadMonitor monitor;
	private DownloadThread dlThread;

	/**
	 * 
	 */
	public TestDownloader() {
		super();
		monitor = new TestDownloadMonitor(this);
	}

	/**
	 * @param string
	 * @param i
	 * @param d
	 */
	public TestDownloader(String name, int i, int d) {
		this();
		this.name = name;
		this.inital = i * 1000;
		this.speed = d;
		this.state = DLState.NOTSTARTED;
		monitor.setState(state);
	}

	public IDownloadMonitor getMonitor() {
		return monitor;
	}

	public void start() {
		if (dlThread == null) {
			dlThread = new DownloadThread();
			dlThread.start();
		} else {
			dlThread.pause(false);
		}
		setState(DLState.DOWNLOADING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.download.IDownloader#stop()
	 */
	public void stop() {
		setState(DLState.CANCELLED);
		if (dlThread != null) {
			dlThread.finish();
			dlThread.interrupt();
		}
	}

	public void hardStop() {
		stop();
	}

	public void requeue() {
		dlThread.pause(true);
		state = DLState.NOTSTARTED;
		monitor.setState(state);
		dlThread.interrupt();
	}

	public void pause() {
		dlThread.pause(true);
		state = DLState.PAUSED;
		monitor.setState(state);
		dlThread.interrupt();
	}

	private void downloadFinished() {
		dlThread.finish();
		state = DLState.FINISHED;
		monitor.setState(state);
	}

	private void setState(DLState state) {
		this.state = state;
		monitor.setState(state);
	}

	public class DownloadThread extends Thread {

		private boolean done = false;
		private boolean pause = false;

		@Override
		public void run() {
			setState(DLState.CONNECTING);
			try {
				Thread.sleep(inital);
			} catch (InterruptedException e) {
			}
			if (done)
				return;
			while (pause) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			if (done)
				return;
			setState(DLState.DOWNLOADING);
			for (int i = 0; i < 100 && !done; i++) {
				try {
					while (pause)
						Thread.sleep(1000);
					Thread.sleep((int) speed);
				} catch (InterruptedException e) {
				}
				pc = i;
			}
			if (!done)
				downloadFinished();
		}

		public void finish() {
			done = true;
		}

		public void pause(boolean p) {
			pause = p;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.download.IDownloader#getURL()
	 */
	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.download.IDownloader#getOutputFile()
	 */
	public File getOutputFile() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.download.IDownloader#getFailureCount()
	 */
	public int getFailureCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.download.IDownloader#resetFailureCount()
	 */
	public void resetFailureCount() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nz.net.kallisti.emusicj.download.IDownloader#setDownloader(java.net.URL,
	 * java.io.File, nz.net.kallisti.emusicj.download.mime.IMimeType[])
	 */
	public void setDownloader(URL url, File outputFile, IMimeType[] mimeType) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nz.net.kallisti.emusicj.download.IDownloader#setDownloader(org.w3c.dom
	 * .Element)
	 */
	public void setDownloader(Element el) throws MalformedURLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nz.net.kallisti.emusicj.download.IDownloader#hasExpired()
	 */
	public boolean hasExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nz.net.kallisti.emusicj.download.IDownloader#updateFrom(nz.net.kallisti
	 * .emusicj.download.IDownloader)
	 */
	public void updateFrom(IDownloader dl) {
		// TODO Auto-generated method stub

	}

}
