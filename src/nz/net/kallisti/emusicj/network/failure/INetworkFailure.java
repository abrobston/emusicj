package nz.net.kallisti.emusicj.network.failure;

import java.net.URL;

/**
 * <p>
 * Implementations of this will detect network failures. If a network failure is
 * suspected, then {@link #isFailure(URL)} should be called.
 * </p>
 * 
 * @author robin
 */
public interface INetworkFailure {

	/**
	 * Call this when a network failure is suspected. Multiple calls to this are
	 * acceptable, they will block until the first one is completed (which may
	 * take time as it could involve waiting for networks to time out.)
	 * 
	 * @param url
	 *            the URL that failed. The host of this will be used to check
	 *            connectivity.
	 * @return <code>true</code> if a network failure is detected.
	 *         <code>false</code> if not. Note that if <code>true</code> is
	 *         returned it is possible that callbacks will have been called that
	 *         may change the state of the program, so allow for that.
	 */
	public boolean isFailure(URL url);

}
