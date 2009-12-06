package nz.net.kallisti.emusicj.controller;

import nz.net.kallisti.emusicj.misc.files.IFileNameCleaner;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * This contains the preferences that are specific to the Passionato variant of
 * the program.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class PassionatoPreferences extends Preferences {

	@Inject
	public PassionatoPreferences(IStrings strings, IFileNameCleaner nameCleaner) {
		super(strings, nameCleaner);
	}

	public boolean allowSaveFileAs() {
		return true;
	}

	@Override
	public boolean showPrefsOnFirstRun() {
		return false;
	}

}
