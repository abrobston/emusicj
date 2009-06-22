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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.download.IDownloadMonitor.DLState;
import nz.net.kallisti.emusicj.download.mime.IMimeType;
import nz.net.kallisti.emusicj.download.mime.MimeType;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Inject;

/**
 * <p>
 * Downloads files from a URL from an HTTP server
 * </p>
 * 
 * @author robin
 */
public class HTTPDownloader implements IDownloader {

	URL url;
	protected HTTPDownloadMonitor monitor;
	File outputFile;
	private volatile DownloadThread dlThread;
	protected volatile DLState state;
	volatile long fileLength = -1;
	volatile long bytesDown = 0;
	protected int failureCount = 0;
	IMimeType[] mimeType;
	protected IPreferences prefs;
	protected Logger logger;
	private final IHttpClientProvider clientProvider;
	private Date expiry;

	@Inject
	public HTTPDownloader(IPreferences prefs, IHttpClientProvider clientProvider) {
		this.prefs = prefs;
		this.clientProvider = clientProvider;
		this.logger = Logger.getLogger("nz.net.kallisti.emusicj.download");
		createMonitor();
	}

	private static volatile int threadNumber = 0;

	/**
	 * This provides a new thread number. This is mostly used for debugging, in
	 * order to see where threads get created.
	 * 
	 * @return a thread number. This will be different every time you call it.
	 */
	private synchronized static int getNextThreadNumber() {
		return threadNumber++;
	}

	/**
	 * Initialise, but do not start, the downloader
	 * 
	 * @param url
	 *            the URL to download
	 * @param outputFile
	 *            the file to save the output to
	 * @param mimeType
	 *            the MIME type to restrict the downloading to. Anything else
	 *            will be considered an error.
	 */
	public void setDownloader(URL url, File outputFile, IMimeType[] mimeType) {
		this.url = url;
		this.outputFile = outputFile;
		this.mimeType = mimeType;
		state = DLState.NOTSTARTED;
		monitor.setState(state);
	}

	/**
	 * Loads the downloader state from the provided element
	 * 
	 * @param el
	 *            the element to load from
	 * @throws MalformedURLException
	 *             if the URL in the XML is wrong or missing
	 */
	public void setDownloader(Element el) throws MalformedURLException {
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
		String tOut = el.getAttribute("outputfile");
		if (!"".equals(tOut))
			outputFile = new File(tOut);
		String tMime = el.getAttribute("mimetype");
		if (!"".equals(tMime)) {
			try {
				String[] parts = tMime.split(",");
				mimeType = new IMimeType[parts.length];
				for (int i = 0; i < parts.length; i++)
					mimeType[i] = new MimeType(parts[i]);
			} catch (MimeTypeParseException e) {
				e.printStackTrace();
			}
		}
		String tExpiry = el.getAttribute("expiry");
		if (!"".equals(tExpiry)) {
			try {
				expiry = new Date(Long.parseLong(tExpiry));
			} catch (NumberFormatException e) {
				logger.warning("Invalid expiry value in state file (" + tExpiry
						+ ") - ignoring");
			}
		}
		// This should come last to ensure everything is set up before we risk
		// executing start()
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
			} else if (tState.equals("EXPIRED")) {
				setState(DLState.EXPIRED);
			}
		}
		hasExpired();
	}

	protected void createMonitor() {
		this.monitor = new HTTPDownloadMonitor(this);
	}

	/**
	 * Saves the important bits of this object to the provided element
	 * 
	 * @param el
	 *            the element to save to
	 * @param doc
	 *            the document this is a part of
	 */
	public void saveTo(Element el, Document doc) {
		el.setAttribute("url", url.toString());
		el.setAttribute("state", state.toString());
		el.setAttribute("outputfile", outputFile.toString());
		if (mimeType != null) {
			String out = "";
			for (int i = 0; i < mimeType.length; i++) {
				if (i != 0)
					out += ",";
				out += mimeType[i].toString();
			}
			el.setAttribute("mimetype", out);
		}
		if (expiry != null) {
			el.setAttribute("expiry", expiry.getTime() + "");
		}
	}

	public IDownloadMonitor getMonitor() {
		return monitor;
	}

	public synchronized void start() {
		if (hasExpired())
			return;
		if (dlThread == null) {
			dlThread = new DownloadThread();
			dlThread.start();
		} else {
			setState(DLState.DOWNLOADING);
		}
	}

	void setState(DLState state) {
		this.state = state;
		monitor.setState(state);
	}

	public synchronized void stop() {
		if (hasExpired())
			return;
		if (dlThread != null)
			dlThread.finish();
		state = DLState.CANCELLED;
		monitor.setState(state);
		dlThread = null;
	}

	public synchronized void requeue() {
		if (hasExpired())
			return;
		if (dlThread != null)
			dlThread.finish();
		state = DLState.NOTSTARTED;
		monitor.setState(state);
		dlThread = null;
	}

	public synchronized void hardStop() {
		if (dlThread != null)
			dlThread.hardFinish();
		state = DLState.CANCELLED;
		monitor.setState(state);
		dlThread = null;
	}

	public synchronized void pause() {
		if (hasExpired())
			return;
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
	 * 
	 * @return the file, or null if the download hasn't started yet.
	 */
	public File getOutputFile() {
		return outputFile;
	}

	private synchronized void downloadError(Exception e) {
		logger.log(Level.WARNING, dlThread + ": A download error occurred", e);
		failureCount++;
		state = DLState.FAILED;
		monitor.setState(state);
		dlThread = null;
	}

	private synchronized void downloadError(String s, Exception ex) {
		if (ex == null) {
			logger.log(Level.WARNING, dlThread + ": " + s);
		} else {
			logger.log(Level.WARNING, dlThread + ": " + s, ex);
		}
		failureCount++;
		state = DLState.FAILED;
		monitor.setState(state);
		dlThread = null;
	}

	/**
	 * Determines if this instance is the equivalent of another one. The
	 * comparison is made based on the output filename.
	 * 
	 * @param o
	 *            the oject to compare to
	 * @return true if the output paths are the same, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof HTTPDownloader))
			return false;
		HTTPDownloader d = (HTTPDownloader) o;
		return outputFile.equals(d.outputFile);
	}

	@Override
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
	 * After this date, the state will be set to 'expired' and not much more
	 * will be able to happen.
	 * 
	 * @param expiry
	 *            the date to define as expiry, or <code>null</code> if there is
	 *            none.
	 */
	protected void setExpiry(Date expiry) {
		this.expiry = expiry;
		hasExpired();
	}

	public boolean hasExpired() {
		if (state == DLState.EXPIRED)
			return true;
		if (expiry == null)
			return false;
		// If we're in the process of downloading, we don't expire
		if (state == DLState.CONNECTING || state == DLState.DOWNLOADING)
			return false;
		if (expiry.compareTo(new Date()) < 0) {
			setState(DLState.EXPIRED);
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * This class does the actual downloading of the file
	 * </p>
	 */
	public class DownloadThread extends Thread {

		private volatile boolean abort = false;
		private volatile boolean hardAbort = false;

		public DownloadThread() {
			super();
		}

		@Override
		public void run() {
			setName(outputFile.toString() + " (#" + getNextThreadNumber() + ")");
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
				partFile = new File(outputFile + ".part");
				if (!partFile.exists() && outputFile.exists()) {
					// see if we have a plain old file instead, if so, resume on
					// it
					needToRename = false;
					partFile = outputFile;
				}
				if (partFile.exists()) {
					resumePoint = partFile.length();
					needToResume = (resumePoint != 0);
				}
				if (needToResume) {
					logger.log(Level.FINER, this
							+ ": Download will be resuming, " + "resumePoint="
							+ resumePoint);
				} else {
					logger.log(Level.FINER, this
							+ ": Download will not be resumed, "
							+ "no .part file or it's zero length");
				}
				out = new BufferedOutputStream(new FileOutputStream(partFile,
						needToResume));
			} catch (FileNotFoundException e) {
				downloadError("needToResume=" + needToResume + " needToRename="
						+ needToRename + " resumePoint=" + resumePoint, e);
				return;
			}
			if (abort)
				return;
			HttpClient http = clientProvider.getHttpClient();
			HttpMethodParams params = new HttpMethodParams();
			// Two minute timeout if no data is received
			params.setSoTimeout(120000);
			HttpMethod get = new GetMethod(url.toString());
			logger.log(Level.FINER, this + ": using server URL: "
					+ url.toString());
			get.setParams(params);
			if (needToResume)
				get.setRequestHeader("Range", "bytes=" + resumePoint + "-");
			InputStream in;
			try {
				int statusCode = http.executeMethod(get);
				logger.log(Level.FINER, this + ": got status code "
						+ statusCode);
				if (statusCode == HttpStatus.SC_OK && needToResume) {
					// we've got an 'OK' code, rather than a 'partial content'
					// code. This means resume isn't supported, so we
					// start the download again.
					logger.log(Level.FINER, this
							+ ": we expected to resume, but aren't");
					needToResume = false;
					resumePoint = 0;
					if (out != null)
						out.close();
					out = new BufferedOutputStream(new FileOutputStream(
							partFile, needToResume));
				}
				// If not a code we expect, abort
				if (statusCode != HttpStatus.SC_OK
						&& statusCode != HttpStatus.SC_PARTIAL_CONTENT) {
					get.abort();
					get.releaseConnection();
					downloadError("Download failed: server returned code "
							+ statusCode, null);
					if (out != null)
						out.close();
					return;
				}
				Header[] responseHeaders = get.getResponseHeaders();
				boolean isFile = mimeType == null;
				long contentLength = -1;
				for (int i = 0; i < responseHeaders.length; i++) {
					String hLine = responseHeaders[i].toString();
					String[] hParts = hLine.split(" ");
					if (hParts[0].equals("Content-Length:")) {
						contentLength = Long.parseLong(hParts[1].substring(0,
								hParts[1].length() - 2));
						fileLength = contentLength + resumePoint;
						// resumePoint will be 0 if no resume
						logger.log(Level.FINER, this
								+ ": Content-Length header "
								+ "tells us there will be " + contentLength
								+ " bytes of content, making a "
								+ "total filesize of " + fileLength);

					}
					// if (hParts[0].equals("Content-Disposition:")) {
					// isFile = isFile || hParts[1].equals("attachment;");
					// }
					if (hParts[0].equals("Content-Type:") && mimeType != null) {
						try {
							// Substring is to remove the \r\n off the end
							String m = hParts[1].substring(0, hParts[1]
									.length() - 2);
							for (IMimeType t : mimeType)
								isFile = isFile || t.matches(m);
							if (!isFile) {
								System.err.print("MIME error: got " + m
										+ ", expecting one of:");
								for (IMimeType t : mimeType)
									System.err.print(" " + t);
								System.err.println();
							}
						} catch (MimeTypeParseException e) {
							e.printStackTrace();
						}
					}
				}
				if (contentLength == 0 && !isFile
						&& statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
					// This hopefully fixes an odd issue where if you request
					// an already complete file from emusic, it will not
					// provide a MIME header. This makes a small amount of
					// sense, but kinda violates 'least surprise'.
					// We let the download proceed so that we don't get a
					// failure when it wasn't really.
					isFile = true;
					logger.log(Level.FINER, this + ": we're pretending a file "
							+ "was given when it wasn't, indicating an "
							+ "already complete file");
				}
				if (!isFile) {
					downloadError("Result isn't a file", null);
					get.abort();
					out.close();
					get.releaseConnection();
					return;
				}
				if (abort) {
					try {
						out.close();
					} catch (IOException e) {
					}
					if (!hardAbort) {
						get.abort();
						get.releaseConnection();
					}
					out.close();
					return;
				}
				if (fileLength == -1) {
					downloadError("Didn't get a Content-Length: header.", null);
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
					out = new BufferedOutputStream(new FileOutputStream(
							partFile));
				}
				in = get.getResponseBodyAsStream();
			} catch (IOException e) {
				get.abort();
				get.releaseConnection();
				downloadError(e);
				try {
					out.close();
				} catch (IOException e2) {
				}
				return;
			}
			if (abort) {
				try {
					out.close();
				} catch (IOException e) {
				}
				get.abort();
				if (!hardAbort) {
					try {
						in.close();
					} catch (IOException e) {
					}
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
					bytesDown += count;
					out.write(buff, 0, count);
					if (abort) {
						get.abort();
						try {
							out.close();
						} catch (IOException e) {
						}
						if (!hardAbort) {
							try {
								in.close();
							} catch (IOException e) {
							}
							get.releaseConnection();
						}
						return;
					}
				}
				logger.log(Level.FINER, this
						+ ": Download finished, bytesDown=" + bytesDown);
				if (bytesDown == fileLength) {
					setState(DLState.FINISHED);
					out.close();
					in.close();
					if (needToRename)
						partFile.renameTo(outputFile);
					get.releaseConnection();
				} else {
					// if we didn't get the whole file, mark it and it'll
					// be tried again later
					downloadError(
							"File downloaded not the size it should have "
									+ "been: got " + bytesDown + ", expected "
									+ fileLength, null);
					out.close();
					in.close();
					get.releaseConnection();
				}
			} catch (IOException e) {
				get.abort();
				try {
					out.close();
					in.close();
				} catch (Exception ex) {
				}
				get.releaseConnection();
				downloadError(e);
				return;
			}
		}

		public synchronized void finish() {
			this.abort = true;
			this.interrupt();
		}

		public synchronized void hardFinish() {
			this.hardAbort = true;
			this.abort = true;
			this.interrupt();
		}

	}

}
