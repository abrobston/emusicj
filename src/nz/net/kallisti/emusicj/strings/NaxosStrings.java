package nz.net.kallisti.emusicj.strings;

import java.io.File;

import com.google.inject.Inject;

/**
 * <p>Contains strings specific to the Naxos implementation</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class NaxosStrings implements IStrings {
	
	@Inject
	public NaxosStrings() {
	}

	public String getAppName() {
		return "Classicsonline Download Manager";
	}

	public String getShortAppName() {
		return "Classicsonline";
	}
	
	public String getAppPathname() {
		return "classicsonline";
	}
	
	public String getFileNamingDetails() {
		return "%a=album, %n=track number, %t=track name";
	}

	public String[] getOpenDialogueFilterExtensions() {
		return new String [] {"*.col", "*.*"};
	}

	public String[] getOpenDialogueFilterNames() {
		return new String [] {"col files (*.col)","All Files (*.*)"};
	}
	
	public String getAutoLoadDescription() {
		return "Automatically load .col files from:";
	}

	public String getAboutBoxText() {
		return "Classicsonline Download Manager v0.20, Copyright (C) 2006, 2007 Robin Sheat <robin@kallisti.net.nz>\n\n"+ 

		"The Classicsonline Download Manager comes with ABSOLUTELY NO WARRANTY. "+
		"This is free software, and you are welcome to redistribute it under the "+
		"terms of the GNU General Public License. A copy of this is contained in "+
		"the file 'COPYING'.\n\n"+

		"Artwork Copyright (C) 2007 Classicsonline.com and Naxos Digital Rights Ltd";
	}

	public String getDefaultFilePattern() {
		return "%a"+ File.separatorChar+"%t";
	}

	public String getXMLBaseNodeName() {
		return "classicsonline-state";
	}
	
	public String getVersion() {
		return "1.1";
	}

}
