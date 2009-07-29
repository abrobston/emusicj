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
package nz.net.kallisti.emusicj.updater;

import java.io.IOException;
import java.net.URL;
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
 * This fetches the version update information from a URL, and passes it on to
 * an {@link IUpdateChecker} that will determine if we are really up to date.
 * </p>
 * <p>
 * Note that if the update request fails, this will fail silently (with errors
 * logged)
 * </p>
 * 
 * $Id$
 * 
 * @author robin
 */
public class URLUpdateFetcher implements IUpdateFetcher {

	private IUpdateFetcherListener listener;
	private URL updateUrl;
	private final IHttpClientProvider clientProvider;
	private final Logger logger;
	private final IUpdateChecker checker;

	@Inject
	public URLUpdateFetcher(IHttpClientProvider clientProvider,
			IUpdateChecker checker) {
		this.clientProvider = clientProvider;
		this.checker = checker;
		logger = LogUtils.getLogger(this);
	}

	public void setListener(IUpdateFetcherListener listener) {
		this.listener = listener;
	}

	public void setUpdateUrl(URL updateUrl) {
		this.updateUrl = updateUrl;
	}

	public void check(String currVersion) {
		UpdateCheckThread updateThread = new UpdateCheckThread(currVersion);
		updateThread.start();
	}

	private void notifyListener(String version) {
		listener.updateAvailable(version);
	}

	/**
	 * <p>
	 * Performs the actual update checking, as a thread.
	 */
	public class UpdateCheckThread extends Thread {

		private String currVersion = null;

		public UpdateCheckThread(String currVersion) {
			this.currVersion = currVersion;
		}

		@Override
		public void run() {
			setName("Update Check");
			HttpClient http = clientProvider.getHttpClient();
			HttpMethodParams params = new HttpMethodParams();
			// Two minute timeout if no data is received
			params.setSoTimeout(120000);
			HttpMethod get = new GetMethod(updateUrl.toString());
			get.setParams(params);
			int statusCode;
			try {
				statusCode = http.executeMethod(get);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Checking for updates failed [1]", e);
				return;
			}
			if (statusCode != HttpStatus.SC_OK) {
				get.releaseConnection();
				logger.log(Level.WARNING,
						"Checking for updates failed [2], code: " + statusCode);
				return;
			}
			try {
				String response = get.getResponseBodyAsString();
				String newVer = checker.isUpdateNeeded(currVersion, response);
				/*
				 * String[] versions = response.split("[ \n]"); if
				 * (versions.length == 0) { get.releaseConnection(); logger
				 * .warning
				 * ("Checking for updates failed [3]: no versions found in response"
				 * ); return; } boolean versionOK = false; for (String v :
				 * versions) { if (v.equals(currVersion)) { versionOK = true;
				 * break; } } if (!versionOK) { notifyListener(versions[0]); }
				 */
				if (newVer != null)
					notifyListener(newVer);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Checking for updates failed [4]", e);
				return;
			} finally {
				get.releaseConnection();
			}
		}

	}

}
