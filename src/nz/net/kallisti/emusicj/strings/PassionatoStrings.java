package nz.net.kallisti.emusicj.strings;

import java.io.File;

import nz.net.kallisti.emusicj.controller.IPreferences;
import nz.net.kallisti.emusicj.urls.IURLFactory;

import com.google.inject.Inject;

/**
 * <p>
 * Contains strings specific to the passionato implementation.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class PassionatoStrings extends AbstractStrings {

	private final IURLFactory urlFactory;

	@Inject
	public PassionatoStrings(IURLFactory urlFactory, IPreferences prefs) {
		super(prefs);
		this.urlFactory = urlFactory;
	}

	public String getAppName() {
		return "Passionato Download Manager";
	}

	@Override
	public String getAppNameArticle() {
		return "the ";
	}

	public String getShortAppName() {
		return "Passionato";
	}

	public String getAppPathname() {
		return "passionato";
	}

	public String getFileNamingDetails() {
		return "%a=album, %b=artist, %n=track number, %t=track name\n"
				+ "Note: '.mp3' will be attached to the end of this";
	}

	public String[] getOpenDialogueFilterExtensions() {
		return new String[] { "*.psn", "*.*" };
	}

	public String[] getOpenDialogueFilterNames() {
		return new String[] { "Passionato Files (*.psn)", "All Files (*.*)" };
	}

	public String getPrefsAutoLoadDescription() {
		return "Automatically load .psn files from:";
	}

	public String getAboutBoxText() {
		return "This program was written by Robin Sheat <robin@kallisti.net.nz> \n\n"
				+ "Thanks to:\n"
				+ "Curtis Cooley (code)\n"
				+ "Michael MacDonald (code)\n"
				+ "Liron Tocker <http://lironbot.com> [eMusic: Liron] (artwork)\n"
				+ "James Elwood [eMusic: jelwood01] (artwork)\n"
				+ "\nCheck "
				+ urlFactory.getAppURL()
				+ " for updates and "
				+ "information.\n"
				+ "\nThe program may be freely distributed under the terms of the GNU GPL.\n"
				+ "\nNote that this program is not affiliated in any way with eMusic.com\n";

	}

	public String getDefaultFilePattern() {
		return "%b" + File.separatorChar + "%a" + File.separatorChar + "%n %t";
	}

	public String getXMLBaseNodeName() {
		return "passionato-state";
	}

	public String getVersion() {
		return "1.0-bzr";
	}

}
