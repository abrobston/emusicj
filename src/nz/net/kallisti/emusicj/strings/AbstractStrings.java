package nz.net.kallisti.emusicj.strings;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.misc.StringUtils;

/**
 * <p>
 * This provides standard implementations of things that can be shared between
 * different implementations.
 * </p>
 * 
 * @author robin
 */
public abstract class AbstractStrings implements IStrings {

	private final IPreferences prefs;

	public AbstractStrings(IPreferences prefs) {
		this.prefs = prefs;
	}

	public String getCoverArtName() {
		String userDefined = prefs.getCoverArtFilename();
		if (userDefined != null)
			return userDefined;
		// If we're on windows or mac, we return 'folder.jpg'
		String os = System.getProperty("os.name");
		if (os != null
				&& (os.toLowerCase().contains("windows") || os.toLowerCase()
						.contains("mac os x"))) {
			return "folder";
		}
		return "cover";
	}

	public String prefsAutomaticallyCheck() {
		return "Automatically check for updates to the program";
	}

	public String prefsDownloadsGroupTitle() {
		return "Downloads";
	}

	public String prefsFilesGroupTitle() {
		return "Files";
	}

	public String dlMaxFailures() {
		return "Download Failed";
	}

	public String getAppNameArticle() {
		// In many cases we want 'the'
		return "the ";
	}

	public String networkFailureMessage() {
		return StringUtils.capitalise(getAppNameArticle())
				+ getAppName()
				+ " could not establish a connection to the download server.  "
				+ "This may be due to problems with your internet connection.\n"
				+ "\n"
				+ "Your downloads have been paused. To resume them, press "
				+ "the 'unpause' button on the toolbar.";
	}
}
