package nz.net.kallisti.emusicj.updater;

import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.misc.version.InvalidVersionException;
import nz.net.kallisti.emusicj.misc.version.Version;

/**
 * <p>
 * This update checker is used by ClassicsOnline, it follows a slightly
 * different protocol to the standard one. This works by only considering that
 * an update is needed if the version the server provides is greater than the
 * current version. This does mean understanding of version numbering systems,
 * and is less able to work with more interesting numbering schemes.
 * </p>
 * 
 * @author robin
 */
public class IncreasingVersionUpdateChecker implements IUpdateChecker {

	private final Logger logger;

	public IncreasingVersionUpdateChecker() {
		logger = LogUtils.getLogger(this);
	}

	public String isUpdateNeeded(String currVersion, String versionInfo) {
		String v = versionInfo.trim();
		Version curr, supplied;
		try {
			curr = new Version(currVersion);
			supplied = new Version(v);
		} catch (InvalidVersionException e) {
			logger.log(Level.WARNING,
					"Invalid version information supplied [curr=" + currVersion
							+ ", supplied=" + versionInfo + "]", e);
			return null;
		}
		if (curr.compareTo(supplied) < 0) {
			return v;
		}
		return null;
	}

}
