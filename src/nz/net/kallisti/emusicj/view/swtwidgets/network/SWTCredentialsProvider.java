package nz.net.kallisti.emusicj.view.swtwidgets.network;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;

/**
 * <p>
 * This will ask the user for proxy-related credentials, and save them for the
 * session. It is smart so that it will only ask once, even if it gets multiple
 * requests around the same time. It'll just block them all. Note that it
 * doesn't expect to work with realms and stuff. It's not that smart.
 * </p>
 * 
 * @author robin
 */
public class SWTCredentialsProvider implements CredentialsProvider {

	private final Credentials creds = null;
	private final Object credsLock = new Object();

	public Credentials getCredentials(AuthScheme authScheme, String host,
			int port, boolean proxy) throws CredentialsNotAvailableException {
		if (!proxy) {
			throw new CredentialsNotAvailableException(
					"Unable to handle authentication for things that aren't proxies");
		}
		if (authScheme == null)
			return null;
		if (creds != null)
			return creds;
		synchronized (credsLock) {
			// In case we were waiting for someone else to get the creds work
			// out
			if (creds != null)
				return creds;
			// TODO ask for credentials using magic GUI stuff.
			// (remember that we may have to wait until the GUI starts up)
			return creds;
		}
	}

}
