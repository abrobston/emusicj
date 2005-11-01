package nz.net.kallisti.emusicj.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nz.net.kallisti.emusicj.controller.Preferences;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * <p>Downloads files from a URL from an HTTP server</p>
 * 
 * <p>$Id$</p>
 *
 * @author robin
 */
public class HTTPDownloader implements IDownloader {
	
	private URL url;
	protected HTTPDownloadMonitor monitor;
	private File outputFile;
	private DownloadThread dlThread;
	protected volatile DLState state;
	volatile long fileLength = -1;
	volatile long bytesDown = 0;
	
	public HTTPDownloader(URL url, File outputFile) {
		super();
		this.url = url;
		this.outputFile = outputFile;
		createMonitor();
		state = DLState.NOTSTARTED;
		monitor.setState(state);
	}
	
	/**
	 * Loads the downloader state from the provided element
	 * @param el the element to load from
	 * @throws MalformedURLException if the URL in the XML is wrong or missing
	 */
	public HTTPDownloader(Element el) throws MalformedURLException {
		super();		
		String tUrl = el.getAttribute("url");
		if (tUrl != null)
			url = new URL(tUrl);
		else
			throw new MalformedURLException("Missing URL");
		String tFname = el.getAttribute("outputfile");
		if (tFname != null)
			outputFile = new File(tFname);
		else
			throw new MalformedURLException("Missing output filename");
		createMonitor();
		setState(DLState.NOTSTARTED);
		String tState = el.getAttribute("state");
		if (tState != null) {
			if (tState.equals("CONNECTING") || tState.equals("DOWNLOADING")) {
				setState(DLState.CONNECTING);
				start();
			} else if (tState.equals("STOPPED")) {
				setState(DLState.STOPPED);
			} else if (tState.equals("PAUSED")) {
				setState(DLState.PAUSED);
			} else if (tState.equals("FINISHED")) {
				setState(DLState.FINISHED);
			} else if (tState.equals("FAILED")) {
				setState(DLState.FAILED);
			}
		}		
	}

	protected void createMonitor() {
		this.monitor = new HTTPDownloadMonitor(this);		
	}
	
	/**
	 * Saves the important bits of this object to the provided element
	 * @param el the element to save to
	 * @param doc the document this is a part of
	 */
	public void saveTo(Element el, Document doc) {
		el.setAttribute("url", url.toString());
		el.setAttribute("state", state.toString());
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
			setState(DLState.DOWNLOADING);
		}
	}
	
	private void setState(DLState state) {
		this.state = state;
		monitor.setState(state);
	}
	
	public void stop() {
		if (dlThread != null)
			dlThread.finish();
		state = DLState.STOPPED;
		monitor.setState(state);
		dlThread = null;
	}
	
	public void requeue() {
		if (dlThread != null)
			dlThread.finish();
		state = DLState.NOTSTARTED;
		monitor.setState(state);
		dlThread = null;	
	}
	
	public void hardStop() {
		if (dlThread != null)
			dlThread.hardFinish();
		state = DLState.STOPPED;
		monitor.setState(state);
		dlThread = null;
	}
	
	public void pause() {
		if (dlThread != null)
			dlThread.finish();
		state = DLState.PAUSED;
		monitor.setState(state);
		dlThread = null;
	}
	
	public URL getURL() {
		return url;
	}
	
	/**
	 * Returns the file that the download will be saved to.
	 * @return the file, or null if the download hasn't started yet.
	 */
	public File getOutputFile() {
		return outputFile;
	}
	
	private void downloadError(Exception e) {
		e.printStackTrace();
		state = DLState.FAILED;
		monitor.setState(state);
		dlThread = null;
	}
	
	private void downloadError(String s) {
		System.err.println(s);
		state = DLState.FAILED;
		monitor.setState(state);
		dlThread = null;
	}
	
	/**
	 * <p>This class does the actual downloading of the file</p>
	 */
	public class DownloadThread extends Thread {
		
		private volatile boolean pause = false;
		private volatile boolean abort = false;
		private volatile boolean hardAbort = false;
		
		public DownloadThread() {
			super();
		}
		
		public void run() {
			setName(outputFile.toString());
	        setState(DLState.CONNECTING);
			BufferedOutputStream out = null;
			File partFile;
			boolean needToResume = false;
			boolean needToRename = true;
			long resumePoint = 0;
			try {
				File parent = outputFile.getParentFile();
				if (parent != null)
					parent.mkdirs();
				partFile = new File(outputFile+".part");
				if (!partFile.exists() && outputFile.exists()) {
					// see if we have a plain old file instead, if so, resume on it
					needToRename = false;
					partFile = outputFile;
				}
				if (partFile.exists()) {
					needToResume = true;
					resumePoint = partFile.length();
				}
				out = new BufferedOutputStream(new FileOutputStream(partFile, needToResume));
			} catch (FileNotFoundException e) {
				downloadError(e);
                try {
                    out.close();
                } catch (IOException e2) {}
				return;
			}
			while (pause && !abort);
			if (abort) return;
            HttpClient http = new HttpClient();
            Preferences prefs = Preferences.getInstance();
            if (!prefs.getProxyHost().equals("")) {
                HostConfiguration hostConf = new HostConfiguration();
                hostConf.setProxy(prefs.getProxyHost(), prefs.getProxyPort());
                http.setHostConfiguration(hostConf);
            }
			HttpMethodParams params = new HttpMethodParams();
			// Two minute timeout if no data is received
			params.setSoTimeout(120000);
			HttpMethod get = new GetMethod(url.toString());
			get.setParams(params);
			if (needToResume)
				get.setRequestHeader("Range","bytes="+resumePoint+"-");
			InputStream in;
			try {
				int statusCode = http.executeMethod(get);
				if (statusCode != HttpStatus.SC_OK &&
						statusCode != HttpStatus.SC_PARTIAL_CONTENT) {
					get.abort();
					get.releaseConnection();
					downloadError("Download failed: server returned code "+statusCode);
                    out.close();
					return;
				}
				if (statusCode == HttpStatus.SC_OK && needToResume) {
					// It seems we can't resume. Start the file over
					resumePoint = 0;
				}
				Header[] responseHeaders = get.getResponseHeaders();                
				for (int i=0; i<responseHeaders.length; i++){
					String hLine = responseHeaders[i].toString();
					String[] hParts = hLine.split(" ");
					if (hParts[0].equals("Content-Length:")) {
						fileLength = Long.parseLong(hParts[1].
								substring(0,hParts[1].length()-2)) +
								resumePoint; // resumePoint will be 0 if no resume
					}
				}
				while (pause && !abort);
				if (abort) {
					try { out.close(); } catch (IOException e) {}
					if (!hardAbort) {
						get.abort();
						get.releaseConnection();
					}
                    out.close();
					return;
				}
				// TODO better checking that we are getting what we expect
				if (fileLength == -1) {
					downloadError("Didn't get a Content-Length: header.");
					get.abort();
					out.close();
					get.releaseConnection();
					return;
				}
				if (statusCode == HttpStatus.SC_OK && needToResume) {
					// This test has to come after the test above so that
					// we don't zero out the file by mistake.
					needToResume = false;
					out.close();
					out = new BufferedOutputStream(new FileOutputStream(partFile));
				}
				in = get.getResponseBodyAsStream();
			} catch (IOException e) {
				get.releaseConnection();
				downloadError(e);
                try {
                    out.close();
                } catch (IOException e2) {}
				return;
			}
			while (pause && !abort);
			if (abort) {
				try { out.close(); } catch (IOException e) {}
				if (!hardAbort) {
					get.abort();
					try { in.close(); } catch (IOException e) {}
					get.releaseConnection();
				}
				return;
			}
			byte[] buff = new byte[8192]; // we'll work in 8K chunks
	        setState(DLState.DOWNLOADING);
			int count;
			bytesDown = resumePoint;
			try {
				while ((count = in.read(buff)) != -1) {
					synchronized (HTTPDownloader.this) {
						// synch because an assignment to a long isn't atomic
						bytesDown += count;
					}
					out.write(buff, 0, count);
					try {
						while (pause && !abort) sleep(100);
					} catch(InterruptedException e) {}
					if (abort) {
						get.abort();
						try { out.close(); } catch (IOException e) {}
						if (!hardAbort) {
							try { in.close(); } catch (IOException e) {}
							get.releaseConnection();
						}
						return;
					}
				}
				if (bytesDown == fileLength) {
					setState(DLState.FINISHED);
					if (needToRename)
						partFile.renameTo(outputFile);
					out.close();
					in.close();
					get.releaseConnection();
				} else { // if we didn't get the whole file, mark it and it'll
						// be tried again later
					//setState(DLState.FAILED);
					downloadError("File downloaded not the size it should have "+
							"been: got "+bytesDown+", expected "+fileLength);
					out.close();
					in.close();
					get.releaseConnection();					
				}
			} catch (IOException e) {
				try {
					out.close();
					in.close();
				} catch (Exception ex) {}
				get.abort();
				get.releaseConnection();
				downloadError(e);
				return;
			}
		}
		
		public synchronized void pause(boolean pause) {
			this.pause = pause;
		}
		
		public synchronized void finish() {
			this.abort  = true;
			this.interrupt();
		}
		
		public synchronized void hardFinish() {
			this.hardAbort = true;
			this.abort = true;
			this.interrupt();
		}
		
	}
	
}
