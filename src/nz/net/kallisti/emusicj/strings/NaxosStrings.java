package nz.net.kallisti.emusicj.strings;

import java.io.File;

import nz.net.kallisti.emusicj.controller.IPreferences;

import com.google.inject.Inject;

/**
 * <p>
 * Contains strings specific to the Naxos implementation
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class NaxosStrings extends AbstractStrings {

	private final IPreferences prefs;

	@Inject
	public NaxosStrings(IPreferences prefs) {
		super(prefs);
		this.prefs = prefs;
	}

	public String getAppName() {
		return "ClassicsOnline Download Manager";
	}

	public String getShortAppName() {
		return "ClassicsOnline";
	}

	public String getAppPathname() {
		return "classicsonline";
	}

	public String getFileNamingDetails() {
		return "%a=album, %n=track number, %t=track name";
	}

	public String[] getOpenDialogueFilterExtensions() {
		return new String[] { "*.col", "*.*" };
	}

	public String[] getOpenDialogueFilterNames() {
		return new String[] { "col files (*.col)", "All Files (*.*)" };
	}

	public String getPrefsAutoLoadDescription() {
		return "Automatically load .col files from:";
	}

	public String getAboutBoxText() {
		return "ClassicsOnline Download Manager "
				+ getVersion()
				+ ", Copyright (C) 2006-2009 Robin Sheat <robin@kallisti.net.nz>\n\n"
				+

				"The Classicsonline Download Manager comes with ABSOLUTELY NO WARRANTY. "
				+ "This is free software, and you are welcome to redistribute it under the "
				+ "terms of the GNU General Public License. A copy of this is contained in "
				+ "the file 'COPYING'.\n\n" +

				"Artwork Copyright (C) 2007 ClassicsOnline.com and Naxos Digital Rights Ltd";
	}

	public String getDefaultFilePattern() {
		return "%a" + File.separatorChar + "%t";
	}

	public String getXMLBaseNodeName() {
		return "classicsonline-state";
	}

	public String getVersion() {
		return "2.0-alpha";
	}

	@Override
	public String getCoverArtName() {
		String userDefined = prefs.getCoverArtFilename();
		if (userDefined != null)
			return userDefined;
		// Naxos wants 'album' as the name
		return "album";
	}

	@Override
	public String prefsFilesGroupTitle() {
		return "Downloads Folder Location";
	}

	@Override
	public String prefsDownloadsGroupTitle() {
		return "Downloads Control";
	}

	@Override
	public String prefsAutomaticallyCheck() {
		return "Automatically check for updates when program started";
	}

	@Override
	public String dlMaxFailures() {
		return "Track Download Error - Contact CS";
	}

}
