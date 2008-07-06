package nz.net.kallisti.emusicj.strings;

import java.io.File;

import nz.net.kallisti.emusicj.controller.IPreferences;

import com.google.inject.Inject;

/**
 * <p>
 * Contains strings specific to the Blue Pie variant of the application
 * </p>
 * 
 * $Id$
 * 
 * @author robin
 */
public class BluePieStrings implements IStrings {

	private final IPreferences prefs;

	@Inject
	public BluePieStrings(IPreferences prefs) {
		this.prefs = prefs;
	}

	public String getAppName() {
		return "BluePie Download Manager";
	}

	public String getShortAppName() {
		return "BluePie";
	}

	public String getAppPathname() {
		return "bluepie";
	}

	public String getFileNamingDetails() {
		return "%a=album, %b=artist, %n=track number, %t=track name\n"
				+ "Note: '.mp3' will be attached to the end of this";
	}

	public String[] getOpenDialogueFilterExtensions() {
		return new String[] { "*.pie", "*.*" };
	}

	public String[] getOpenDialogueFilterNames() {
		return new String[] { "BluePie files (*.pie)", "All Files (*.*)" };
	}

	public String getAutoLoadDescription() {
		return "Automatically load .pie files from:";
	}

	public String getAboutBoxText() {
		return "BluePie Download Manager v1.0, Copyright (C) 2006-2008 Robin Sheat <robin@kallisti.net.nz>\n\n"
				+ "The BluePie Download Manager comes with ABSOLUTELY NO WARRANTY. "
				+ "This is free software, and you are welcome to redistribute it under the "
				+ "terms of the GNU General Public License. A copy of this is contained in "
				+ "the file 'COPYING'.\n\n"
				+ "Artwork Copyright (C) 2008 bluepie.org";
	}

	public String getDefaultFilePattern() {
		return "%b" + File.separatorChar + "%a" + File.separatorChar + "%n %t";
	}

	public String getXMLBaseNodeName() {
		return "bluepie-state";
	}

	public String getVersion() {
		return "1.0";
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
