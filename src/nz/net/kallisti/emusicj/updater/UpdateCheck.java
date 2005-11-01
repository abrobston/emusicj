package nz.net.kallisti.emusicj.updater;

import java.io.IOException;

import nz.net.kallisti.emusicj.controller.Preferences;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


/**
 * <p>Checks to see if there is a new version of the program available. Reads
 * the provided URL, which contains a string containing a space-seperated
 * list of versions that are considered current. If the current version isn't
 * on that list, then it notifies the provided {@link IUpdateCheckListener}.</p>
 * <p>Note that if the update request fails, this will fail silently (with
 * errors going to STDERR)</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class UpdateCheck {

	private IUpdateCheckListener listener;
	private String updateUrl;

	/**
	 * Creates an instance of the class
	 * @param listener the object to notify when we find out if there is an
	 * update available
	 * @param updateUrl the URL to check 
	 */
	public UpdateCheck(IUpdateCheckListener listener, String updateUrl) {
		this.listener = listener;
		this.updateUrl = updateUrl;
	}

	/**
	 * Initiates the version check.
	 * @param currVersion the current version of the application to check 
	 * against
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
            Preferences prefs = Preferences.getInstance();
            HttpClient http = new HttpClient();
            if (!prefs.getProxyHost().equals("")) {
                HostConfiguration hostConf = new HostConfiguration();
                hostConf.setProxy(prefs.getProxyHost(), prefs.getProxyPort());
                http.setHostConfiguration(hostConf);
            }
			HttpMethodParams params = new HttpMethodParams();
			// Two minute timeout if no data is received
			params.setSoTimeout(120000);
			HttpMethod get = new GetMethod(updateUrl);
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
