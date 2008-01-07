package nz.net.kallisti.emusicj.strings;

import java.io.File;

import nz.net.kallisti.emusicj.controller.IPreferences;

import com.google.inject.Inject;

/**
 * <p>
 * Contains strings specific to the eMusic/J implementation.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class ReggaeCountryStrings implements IStrings {

	private final IPreferences prefs;

	@Inject
	public ReggaeCountryStrings(IPreferences prefs) {
		this.prefs = prefs;
	}

	public String getAppName() {
		return "ReggaeCountry Manager";
	}

	public String getShortAppName() {
		return "ReggaeCountry";
	}

	public String getAppPathname() {
		return "reggaecountry";
	}

	public String getFileNamingDetails() {
		return "%a=album, %b=artist, %n=track number, %t=track name\n"
				+ "Note: '.mp3' will be attached to the end of this";
	}

	public String[] getOpenDialogueFilterExtensions() {
		return new String[] { "*.rcm", "*.*" };
	}

	public String[] getOpenDialogueFilterNames() {
		return new String[] { "ReggaeCountry files (*.rcm)", "All Files (*.*)" };
	}

	public String getAutoLoadDescription() {
		return "Automatically load .rcm files from:";
	}

	public String getAboutBoxText() {
		return "Reggaecountry Downloader v1.0, Copyright (C) 2006, 2007 Robin Sheat <robin@kallisti.net.nz>\n\n"
				+ "The Reggaecountry Downloader comes with ABSOLUTELY NO WARRANTY. "
				+ "This is free software, and you are welcome to redistribute it under the "
				+ "terms of the GNU General Public License. A copy of this is contained in "
				+ "the file 'COPYING'.\n\n"
				+ "Artwork Copyright (C) 2007 Reggaecountry.com";
	}

	public String getDefaultFilePattern() {
		return "%b" + File.separatorChar + "%a" + File.separatorChar + "%n %t";
	}

	public String getXMLBaseNodeName() {
		return "reggaecountry-state";
	}

	public String getVersion() {
		return "0.23-svn";
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

}
