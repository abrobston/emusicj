/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005-2009 Robin Sheat

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
package nz.net.kallisti.emusicj.network.http.proxy;

import nz.net.kallisti.emusicj.controller.IPreferences;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.auth.CredentialsProvider;

import com.google.inject.Inject;

/**
 * <p>
 * This is the default HttpClient provider for the system. It will create and
 * initialise the state as needed, including all the proxy-related information.
 * </p>
 * 
 * @author robin
 */
public class HttpClientProvider implements IHttpClientProvider {

	// volatile makes double-checked locking OK
	private volatile HttpState globalState;
	private final CredentialsProvider proxyCredsProvider;
	private final IPreferences prefs;

	@Inject
	public HttpClientProvider(CredentialsProvider proxyCredsProvider,
			IPreferences prefs) {
		this.proxyCredsProvider = proxyCredsProvider;
		this.prefs = prefs;
	}

	public HttpClient getHttpClient() {
		HttpState state = getState();
		HttpClient client = new HttpClient();
		client.setState(state);
		if (!prefs.getProxyHost().equals("") && prefs.usingProxy()) {
			HostConfiguration hostConf = new HostConfiguration();
			hostConf.setProxy(prefs.getProxyHost(), prefs.getProxyPort());
			client.setHostConfiguration(hostConf);
			client.getParams().setParameter(CredentialsProvider.PROVIDER,
					proxyCredsProvider);
		}
		return client;
	}

	HttpState getState() {
		if (globalState != null)
			return globalState;
		synchronized (this) {
			if (globalState != null)
				return globalState;
			HttpState state = new HttpState();
			// Any state configuration needed will occur here
			globalState = state;
		}
		return globalState;
	}

}
