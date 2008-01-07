package nz.net.kallisti.emusicj.controller;

import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * This contains preferences that are specific to the Reggae Country variant of
 * the program.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class ReggaeCountryPreferences extends Preferences {

	@Inject
	public ReggaeCountryPreferences(IStrings strings) {
		super(strings);
	}

	public boolean allowSaveFileAs() {
		return true;
	}

}
