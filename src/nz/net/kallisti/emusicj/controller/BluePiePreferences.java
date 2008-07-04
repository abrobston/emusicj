package nz.net.kallisti.emusicj.controller;

import nz.net.kallisti.emusicj.misc.files.IFileNameCleaner;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * This contains the preference overrides specific to the Blue Pie variant of
 * the application
 * </p>
 * 
 * $Id$
 * 
 * @author robin
 */
public class BluePiePreferences extends EmusicjPreferences {

	@Inject
	public BluePiePreferences(IStrings strings, IFileNameCleaner nameCleaner) {
		super(strings, nameCleaner);
	}

}
