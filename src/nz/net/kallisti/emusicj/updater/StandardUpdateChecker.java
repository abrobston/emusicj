package nz.net.kallisti.emusicj.updater;

import java.util.logging.Logger;

import nz.net.kallisti.emusicj.misc.LogUtils;

import com.google.inject.Inject;

/**
 * <p>
 * This is the update checker used by most versions of the application. It
 * expects a space-separated string of versions that are considered 'current'
 * </p>
 * 
 * @author robin
 */
public class StandardUpdateChecker implements IUpdateChecker {

	private final Logger logger;

	@Inject
	public StandardUpdateChecker() {
		logger = LogUtils.getLogger(this);
	}

	public String isUpdateNeeded(String currVersion, String versionInfo) {
		String[] versions = versionInfo.split("[ \n]");
		if (versions.length == 0) {
			logger.warning("No version information found: zero-length list");
			return null;
		}
		for (String v : versions) {
			if (v.equals(currVersion))
				return null;
		}
		return versions[0];
	}

}
