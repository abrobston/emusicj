package nz.net.kallisti.emusicj.network.http.downloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.google.inject.Inject;

/**
 * <p>
 * This is a simple downloader that can fetch HTTP URLs to a file.
 * </p>
 * 
 * @author robin
 */
public class SimpleDownloader implements ISimpleDownloader {

	List<ISimpleDownloadListener> listeners = new ArrayList<ISimpleDownloadListener>();
	private final IHttpClientProvider httpClientProvider;
	private File outputFile;
	private URL url;
	private final Logger logger;

	@Inject
	public SimpleDownloader(IHttpClientProvider httpClientProvider) {
		this.httpClientProvider = httpClientProvider;
		logger = LogUtils.getLogger(this);
	}

	public void addListener(ISimpleDownloadListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ISimpleDownloadListener listener) {
		listeners.remove(listener);
	}

	public void setOutputFile(File file) {
		this.outputFile = file;
	}

	public void setURL(URL url) {
		this.url = url;
	}

	public void start() throws IllegalStateException {
		if (url == null)
			throw new IllegalStateException("The URL was not defined");
		if (outputFile == null)
			throw new IllegalStateException("The output file was not defined");
		new DownloadThread().start();
	}

	private class DownloadThread extends Thread {

		@Override
		public void run() {
			setName("Download - " + url);
			HttpClient http = httpClientProvider.getHttpClient();
			HttpMethodParams params = new HttpMethodParams();
			// Two minute timeout if no data is received
			params.setSoTimeout(120000);
			HttpMethod get = new GetMethod(url.toString());
			get.setParams(params);
			int statusCode;
			try {
				statusCode = http.executeMethod(get);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to fetch " + url, e);
				for (ISimpleDownloadListener l : listeners)
					l.downloadFailed(SimpleDownloader.this);
				return;
			}
			if (statusCode != HttpStatus.SC_OK) {
				get.releaseConnection();
				logger.log(Level.WARNING, "Failed to fetch " + url
						+ ", status code " + statusCode);
				for (ISimpleDownloadListener l : listeners)
					l.downloadFailed(SimpleDownloader.this);
				return;
			}
			// Download the file
			BufferedOutputStream out;
			try {
				out = new BufferedOutputStream(new FileOutputStream(outputFile));
			} catch (FileNotFoundException e) {
				get.releaseConnection();
				logger.log(Level.WARNING, "Failed to fetch " + url
						+ ": unable to write output file", e);
				for (ISimpleDownloadListener l : listeners)
					l.downloadFailed(SimpleDownloader.this);
				return;
			}
			InputStream in;
			try {
				in = get.getResponseBodyAsStream();
			} catch (IOException e) {
				get.releaseConnection();
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				outputFile.delete();
				logger.log(Level.WARNING, "Failed to fetch " + url
						+ ": error opening stream", e);
				for (ISimpleDownloadListener l : listeners)
					l.downloadFailed(SimpleDownloader.this);
				return;
			}
			byte[] buff = new byte[1024];
			int count;
			try {
				while ((count = in.read(buff)) != -1) {
					out.write(buff, 0, count);
				}
			} catch (IOException e) {
				get.releaseConnection();
				logger.log(Level.WARNING, "Failed to fetch " + url
						+ ": error reading stream", e);
				for (ISimpleDownloadListener l : listeners)
					l.downloadFailed(SimpleDownloader.this);
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				outputFile.delete();
				return;
			}
			// All done
			get.releaseConnection();
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Error closing file for " + url, e);
			}
			for (ISimpleDownloadListener l : listeners)
				l.downloadSucceeded(SimpleDownloader.this, outputFile);
		}

	}

}
