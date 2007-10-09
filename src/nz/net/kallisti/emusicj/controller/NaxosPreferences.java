package nz.net.kallisti.emusicj.controller;

import com.google.inject.Inject;

import nz.net.kallisti.emusicj.strings.IStrings;

/**
 * <p>This contains preferences that are specific to the naxos/classicsonline
 * variant of the program.</p>
 * 
 * $Id:$
 *
 * @author robin
 */
public class NaxosPreferences extends Preferences {

	@Inject
	public NaxosPreferences(IStrings strings) {
		super(strings);
	}

	public boolean allowSaveFileAs() {
		return false;
	}

}
