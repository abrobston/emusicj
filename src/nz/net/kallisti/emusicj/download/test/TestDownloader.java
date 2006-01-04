package nz.net.kallisti.emusicj.download.test;

import java.io.File;
import java.net.URL;

import nz.net.kallisti.emusicj.download.IDownloadMonitor;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;

/**
 * <p>This simulates a download in progress</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class TestDownloader implements IDownloader {
	
	private int inital;
	private double speed;
	
	String name;
	double pc;
	DLState state;
	private TestDownloadMonitor monitor;
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
		this.inital = i*1000;
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
	
	/* (non-Javadoc)
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
		
		public void run() {
			setState(DLState.CONNECTING);
			try {
				Thread.sleep(inital);
			} catch (InterruptedException e) {}
			if (done) return;
			while (pause) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
			if (done) return;
			setState(DLState.DOWNLOADING);
			for (int i=0; i<100 && !done; i++) {
				try {
					while (pause) 
						Thread.sleep(1000);
					Thread.sleep((int)speed);
				} catch (InterruptedException e) { }
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
	
	
	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloader#getURL()
	 */
	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.download.IDownloader#getOutputFile()
	 */
	public File getOutputFile() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
