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

import nz.net.kallisti.emusicj.controller.IPreferences;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.google.inject.Inject;


/**
 * <p>Checks to see if there is a new version of the program available. Reads
 * the provided URL, which contains a string containing a space-seperated
 * list of versions that are considered current. If the current version isn't
 * on that list, then it notifies the provided {@link IUpdateCheckListener}.</p>
 * <p>Note that if the update request fails, this will fail silently (with
 * errors going to STDERR)</p>
 * 
 * $Id$
 *
 * @author robin
 */
public class UpdateCheck implements IUpdateCheck {

	private IUpdateCheckListener listener;
	private URL updateUrl;
	private final IPreferences prefs;

	@Inject
	public UpdateCheck(IPreferences prefs) {
		this.prefs = prefs;
	}

	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.updater.IUpdateCheck#setListener(nz.net.kallisti.emusicj.updater.IUpdateCheckListener)
	 */
	public void setListener(IUpdateCheckListener listener) {
		this.listener = listener;
	}
	
	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.updater.IUpdateCheck#setUpdateUrl(java.lang.String)
	 */
	public void setUpdateUrl(URL updateUrl) {
		this.updateUrl = updateUrl;
	}
	
	/* (non-Javadoc)
	 * @see nz.net.kallisti.emusicj.updater.IUpdateCheck#check(java.lang.String)
	 */
	public void check(String currVersion) {
		UpdateCheckThread updateThread = new UpdateCheckThread(currVersion);
		updateThread.start();
	}

	private void notifyListener(String version) {
		listener.updateAvailable(version);
	}

	
	/**
	 * <p>Performs the actual update checking, as a thread.
	 */
	public class UpdateCheckThread extends Thread {

		private String currVersion = null;
		
		public UpdateCheckThread(String currVersion) {
			this.currVersion = currVersion;
		}
		
		public void run() {
			setName("Update Check");
            HttpClient http = new HttpClient();
            if (!prefs.getProxyHost().equals("")) {
                HostConfiguration hostConf = new HostConfiguration();
                hostConf.setProxy(prefs.getProxyHost(), prefs.getProxyPort());
                http.setHostConfiguration(hostConf);
            }
			HttpMethodParams params = new HttpMethodParams();
			// Two minute timeout if no data is received
			params.setSoTimeout(120000);
			HttpMethod get = new GetMethod(updateUrl.toString());
			get.setParams(params);
			int statusCode;
			try {
				statusCode = http.executeMethod(get);
			} catch (IOException e) {
				System.err.println("Checking for updates failed [1]");				
				e.printStackTrace();
				return;
			}
			if (statusCode != HttpStatus.SC_OK) {
				get.releaseConnection();
				System.err.println("Checking for updates failed [2], code: "+statusCode);
				return;
			}
			try {
				String response = get.getResponseBodyAsString();
				String[] versions = response.split("[ \n]");
				if (versions.length == 0) {
					get.releaseConnection();
					System.err.println("Checking for updates failed [3]: no versions found in response");
					return;					
				}
				boolean versionOK = false;
				for (String v : versions) {
					if (v.equals(currVersion)) {
						versionOK = true;
						break;
					}
				}
				if (!versionOK) {
					notifyListener(versions[0]);
				}
			} catch (IOException e) {
				get.releaseConnection();
				System.err.println("Checking for updates failed [4]");				
				e.printStackTrace();
				return;
			}
			get.releaseConnection();			
		}

	}
	
}
