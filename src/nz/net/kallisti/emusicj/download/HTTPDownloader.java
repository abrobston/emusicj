/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat, Curtis Cooley

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

import java.awt.datatransfer.MimeTypeParseException;
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
import nz.net.kallisti.emusicj.download.mime.IMimeType;
import nz.net.kallisti.emusicj.download.mime.MimeType;

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
	protected int failureCount = 0;
	private IMimeType[] mimeType;
	
	/**
	 * Initialise, but do not start, the downloader
	 * @param url the URL to download
	 * @param outputFile the file to save the output to
	 * @param mimeType the MIME type to restrict the downloading to. Anything
	 * else will be considered an error.
	 */
	public HTTPDownloader(URL url, File outputFile, IMimeType[] mimeType) {
		super();
		this.url = url;
		this.outputFile = outputFile;
		this.mimeType = mimeType;
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
		if (!"".equals(tUrl))
			url = new URL(tUrl);
		else
			throw new MalformedURLException("Missing URL");
		String tFname = el.getAttribute("outputfile");
		if (!"".equals(tFname))
			outputFile = new File(tFname);
		else
			throw new MalformedURLException("Missing output filename");
		createMonitor();
		setState(DLState.NOTSTARTED);
		String tState = el.getAttribute("state");
		if (!"".equals(tState)) {
			if (tState.equals("CONNECTING") || tState.equals("DOWNLOADING")) {
				setState(DLState.CONNECTING);
				start();
			} else if (tState.equals("STOPPED") || tState.equals("CANCELLED")) {
                // in v.0.14-svn, STOPPED became CANCELLED. This double-check is
                // for backwards compatability
				setState(DLState.CANCELLED);
			} else if (tState.equals("PAUSED")) {
				setState(DLState.PAUSED);
			} else if (tState.equals("FINISHED")) {
				setState(DLState.FINISHED);
			}
		}
		String tOut = el.getAttribute("outputfile");
		if (!"".equals(tOut))
			outputFile = new File(tOut);
		String tMime = el.getAttribute("mimetype");
		if (!"".equals(tMime)) {
			try {
				String[] parts = tMime.split(",");
				mimeType = new IMimeType[parts.length];
				for (int i=0; i<parts.length; i++)
					mimeType[i] = new MimeType(parts[i]);
			} catch (MimeTypeParseException e) {
				e.printStackTrace();
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
		el.setAttribute("outputfile", outputFile.toString());
		if (mimeType != null) {
			String out = "";
			for (int i=0; i<mimeType.length; i++) {
				if (i!=0) out+=",";
				out += mimeType[i].toString();
			}
			el.setAttribute("mimetype", out);
		}
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
		state = DLState.CANCELLED;
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
		state = DLState.CANCELLED;
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
        System.err.println(dlThread+": A download error occurred:");
		e.printStackTrace();
		failureCount++;
		state = DLState.FAILED;
		monitor.setState(state);
		dlThread = null;
	}
	
	private void downloadError(String s) {
		System.err.println(dlThread+": "+s);
		failureCount++;
		state = DLState.FAILED;
		monitor.setState(state);
		dlThread = null;
	}
	
	/**
	 * Determines if this instance is the equivalent of another one. The
	 * comparison is made based on the output filename.
	 * @param o the oject to compare to
	 * @return true if the output paths are the same, false otherwise
	 */
	public boolean equals(Object o) {
		if (o==null) return false;
		if (!(o instanceof HTTPDownloader)) return false;
		HTTPDownloader d = (HTTPDownloader)o;
		return outputFile.equals(d.outputFile);
	}
	
	public int hashCode() {
		return outputFile.hashCode();
	}
	
	public int getFailureCount() {
		return failureCount;
	}
	
	public void resetFailureCount() {
		failureCount = 0;
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
					resumePoint = partFile.length();
					needToResume = (resumePoint != 0);
				}
				out = new BufferedOutputStream(new FileOutputStream(partFile, needToResume));
			} catch (FileNotFoundException e) {
				downloadError(e);
                try {
                    out.close();
                } catch (Exception e2) {}
				return;
			}
			while (pause && !abort);
			if (abort) return;
            HttpClient http = new HttpClient();
            Preferences prefs = Preferences.getInstance();
            if (prefs.usingProxy()) {
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
				if (!((statusCode == HttpStatus.SC_OK && !needToResume) ||
						(statusCode == HttpStatus.SC_PARTIAL_CONTENT && needToResume))) {
					get.abort();
					get.releaseConnection();
					downloadError("Download failed: server returned code "+statusCode);
                    out.close();
					return;
				}
				/*if (statusCode == HttpStatus.SC_OK && needToResume) {
					// It seems we can't resume. Start the file over
					resumePoint = 0;
				} -- we no longer allow non-resuming, although we probably should */
				Header[] responseHeaders = get.getResponseHeaders();
				boolean isFile = mimeType == null;
				for (int i=0; i<responseHeaders.length; i++){
					String hLine = responseHeaders[i].toString();
					String[] hParts = hLine.split(" ");
					if (hParts[0].equals("Content-Length:")) {
						fileLength = Long.parseLong(hParts[1].
								substring(0,hParts[1].length()-2)) +
								resumePoint; // resumePoint will be 0 if no resume
					}
//					if (hParts[0].equals("Content-Disposition:")) {
//						isFile = isFile || hParts[1].equals("attachment;");
//					}
					if (hParts[0].equals("Content-Type:") && mimeType != null) {
						try {
							// Substring is to remove the \r\n off the end
							String m = hParts[1].substring(0,hParts[1].length()-2);
							for (IMimeType t : mimeType)
								isFile = isFile || t.matches(m);
							if (!isFile) {
								System.err.print("MIME error: got "+m+", expecting one of:");
								for (IMimeType t : mimeType)
									System.err.print(" "+t);
								System.err.println();
							}
						} catch (MimeTypeParseException e) {
							e.printStackTrace();
						}
					}
				}
				if (!isFile) {
					downloadError("Result isn't a file");
					get.abort();
					out.close();
					get.releaseConnection();
					return;
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
                get.abort();
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
                get.abort();
				if (!hardAbort) {
					try { in.close(); } catch (IOException e) {}
					get.releaseConnection();
				}
				return;
			}
			byte[] buff = new byte[512]; // we'll work in 512b chunks
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
                get.abort();
				try {
					out.close();
					in.close();
				} catch (Exception ex) {}
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
