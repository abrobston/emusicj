package nz.net.kallisti.emusicj.network.failure;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.network.http.proxy.IHttpClientProvider;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.google.inject.Inject;

/**
 * <p>
 * This is the standard implementation of the network failure detection
 * interface.
 * </p>
 * <p>
 * It works by attempting a connection to the host of the URL (via any proxy
 * that is defined). If that connection fails, then it is considered an error.
 * Failure states are cached for a while (see {@link #FAILURE_CACHE_SECONDS})
 * within which time, another check is not performed.
 * </p>
 * 
 * @author robin
 */
public class NetworkFailure implements INetworkFailure {

	/**
	 * The amount of time that a previous result will be cached for
	 */
	public static int FAILURE_CACHE_SECONDS = 15;
	private long lastCheck = 0; // 0 makes it really old
	private boolean lastResult;
	private final Logger logger;
	private final IHttpClientProvider httpProvider;

	@Inject
	public NetworkFailure(IHttpClientProvider httpProvider) {
		this.httpProvider = httpProvider;
		logger = LogUtils.getLogger(this);
	}

	public synchronized boolean isFailure(URL url) {
		// First, check cache
		long now = new Date().getTime();
		if (lastCheck + FAILURE_CACHE_SECONDS > now)
			return lastResult;
		// Build the URL we test
		URL testUrl;
		try {
			testUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(),
					"");
		} catch (MalformedURLException e) {
			// This hopefully will never happen
			logger.log(Level.WARNING, "Unexpected error creating URL", e);
			return false;
		}
		HttpClient client = httpProvider.getHttpClient();
		HttpMethodParams params = new HttpMethodParams();
		// 30 second timeout
		params.setSoTimeout(30000);
		HttpMethod get = new GetMethod(testUrl.toString());
		get.setParams(params);
		try {
			client.executeMethod(get);
		} catch (HttpException e) {
			// This is probably not a network failure
			return cacheAndReturn(false);
		} catch (IOException e) {
			// This probably is
			return cacheAndReturn(true);
		} finally {
			get.releaseConnection();
		}
		return cacheAndReturn(false);
	}

	private boolean cacheAndReturn(boolean b) {
		lastResult = b;
		lastCheck = new Date().getTime();
		return b;
	}

}
