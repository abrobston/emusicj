package nz.net.kallisti.emusicj.network.failure;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.controller.IEmusicjController;
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
 * Any simultaneous queries to this will get the same result as the first one
 * (i.e. this blocks while the test is in progress and returns that result.)
 * </p>
 * <p>
 * It will also signal the controller that a failure has occurred. This will not
 * occur when reporting cached values.
 * </p>
 * 
 * @author robin
 */
public class NetworkFailure implements INetworkFailure {

	/**
	 * The amount of time that a previous result will be cached for
	 */
	private Boolean lastResult = null;
	private final Logger logger;
	private final IHttpClientProvider httpProvider;
	private final IEmusicjController controller;
	private final AtomicInteger blockedCounter = new AtomicInteger();

	@Inject
	public NetworkFailure(IHttpClientProvider httpProvider,
			IEmusicjController controller) {
		this.httpProvider = httpProvider;
		this.controller = controller;
		logger = LogUtils.getLogger(this);
	}

	public synchronized boolean isFailure(URL url) {
		// This allows us to clear the cached value when the last simultaneous
		// request clears
		blockedCounter.incrementAndGet();
		synchronized (this) {
			if (lastResult != null) {
				if (blockedCounter.decrementAndGet() == 0) {
					boolean tempResult = lastResult;
					lastResult = null;
					return tempResult;
				}
				return lastResult;
			}
			try {
				// Build the URL we test
				URL testUrl;
				try {
					testUrl = new URL(url.getProtocol(), url.getHost(), url
							.getPort(), "");
				} catch (MalformedURLException e) {
					// This hopefully will never happen
					logger.log(Level.WARNING, "Unexpected error creating URL",
							e);
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
					controller.networkIssuesDetected();
					logger.log(Level.SEVERE, "Network issues detected", e);
					return cacheAndReturn(true);
				} finally {
					get.releaseConnection();
				}
				return cacheAndReturn(false);

			} finally {
				if (blockedCounter.decrementAndGet() == 0)
					lastResult = null;
			}
		}
	}

	private boolean cacheAndReturn(boolean b) {
		lastResult = b;
		return b;
	}

}
