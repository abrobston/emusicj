package nz.net.kallisti.emusicj.controller;

import nz.net.kallisti.emusicj.misc.files.IFileNameCleaner;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * This contains the preferences that are specific to the eMusic/J varient of
 * the program.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class EmusicjPreferences extends Preferences {

	@Inject
	public EmusicjPreferences(IStrings strings, IFileNameCleaner nameCleaner) {
		super(strings, nameCleaner);
	}

	public boolean allowSaveFileAs() {
		return true;
	}

}
