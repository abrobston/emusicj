package nz.net.kallisti.emusicj.network.http.proxy;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import nz.net.kallisti.emusicj.view.IEmusicjView;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;

import com.google.inject.Inject;

/**
 * <p>
 * This will get the view to ask the user for proxy-related credentials, and
 * save them for the session. It is smart so that it will only ask once, even if
 * it gets multiple requests around the same time. It'll just block them all.
 * Note that it doesn't expect to work with realms and stuff. It's not that
 * smart.
 * </p>
 * 
 * @author robin
 */
public class ProxyCredentialsProvider implements CredentialsProvider {

	private volatile Credentials creds = null;
	/**
	 * This gets set to true if the user cancels the request for proxy
	 * credentials. Note that once it is set, it remains set so they don't get
	 * asked again.
	 */
	private volatile boolean userCancelled = false;
	private final IEmusicjView view;
	private final Semaphore semaphore = new Semaphore(1);
	private final AtomicInteger blockedCounter = new AtomicInteger();

	/**
	 * Default constructor that gets an instance of the view so that it can
	 * request things from the user via it.
	 */
	@Inject
	public ProxyCredentialsProvider(IEmusicjView view) {
		this.view = view;
	}

	public Credentials getCredentials(AuthScheme authScheme, String host,
			int port, boolean proxy) throws CredentialsNotAvailableException {
		if (!proxy) {
			throw new CredentialsNotAvailableException(
					"Unable to handle authentication for things that aren't proxies");
		}
		// if (authScheme == null)
		// return null;
		if (userCancelled)
			throw new CredentialsNotAvailableException(
					"The user cancelled the request for proxy details");
		if (creds != null)
			return creds;
		// This is so we can clear the creds object afterwards. This is so that
		// if they get it wrong, we'll get asked again.
		blockedCounter.incrementAndGet();
		synchronized (this) {
			// In case we were waiting for someone else to get the creds, we
			// just send them on.
			if (userCancelled) {
				blockedCounter.decrementAndGet();
				throw new CredentialsNotAvailableException(
						"The user cancelled the request for proxy details");
			}
			if (creds != null) {
				Credentials lCreds = creds;
				if (blockedCounter.decrementAndGet() == 0)
					creds = null;
				return lCreds;
			}
			// We know there will be one semaphore available, so we grab it now.
			// It'll be released by the callback
			try {
				semaphore.acquire();
			} catch (InterruptedException e1) {
				throw new RuntimeException(
						"A fatal error occurred: multiple threads are inside a synchronise block.",
						e1);
			}
			// Ask for the credentials. Note that due to the design of the
			// provider, this thread has to be blocked while we get them, but
			// we still want to make it easy for the GUI code. So we allow
			// ourselves to wait while the GUI goes and talks to the user.
			view.getProxyCredentials(authScheme, host, port,
					new CredsCallback());
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				if (creds == null)
					throw new CredentialsNotAvailableException(
							"The thread was interrupted while waiting for the proxy information",
							e);
			} finally {
				semaphore.release();
			}
			if (creds == null) {
				// This probably means the user hit cancel
				throw new CredentialsNotAvailableException(
						"The proxy credentials are not available");
			}
			Credentials lCreds = creds;
			if (blockedCounter.decrementAndGet() == 0)
				creds = null;
			return lCreds;
		}
	}

	/**
	 * <p>
	 * When the view has the proxy information from the user, it gives them to
	 * this class, which then lets the download code know about it.
	 * </p>
	 */
	public class CredsCallback {

		/**
		 * This is to be called when the user has provided a username and
		 * password for the proxy.
		 * 
		 * @param username
		 *            the supplied username
		 * @param password
		 *            the supplied password
		 * @param host
		 *            the host that these credentials are for. Required for NTLM
		 *            only, otherwise may be <code>null</code>.
		 * @param domain
		 *            if this is for an NTLM proxy, the domain needs to be
		 *            provided. If it's just a regular one, then set this to
		 *            <code>null</code>.
		 */
		public synchronized void setUsernamePassword(String username,
				String password, String host, String domain) {
			if (domain == null) {
				creds = new UsernamePasswordCredentials(username, password);
			} else {
				creds = new NTCredentials(username, password, host, domain);
			}
			semaphore.release();
		}

		/**
		 * This is to be called if the user cancels the request for proxy
		 * information.
		 */
		public synchronized void userCancelled() {
			userCancelled = true;
			semaphore.release();
		}

	}

}
