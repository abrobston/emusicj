package nz.net.kallisti.emusicj.updater;

/**
 * <p>
 * Implementations of this use whatever methods they think is appropriate to
 * check version information that they are provided.
 * </p>
 * 
 * @author robin
 */
public interface IUpdateChecker {

	/**
	 * This will return <code>null</code> if no update is required, otherwise it
	 * will return the currently most up-to-date version.
	 * 
	 * @param versionInfo
	 *            the version information that needs to be tested. This may have
	 *            been supplied by a remote server or something.
	 * @param currVersion
	 *            the current version of the application
	 * @return <code>null</code> if no update is needed, otherwise the most
	 *         current version.
	 * */
	public String isUpdateNeeded(String currVersion, String versionInfo);

}
