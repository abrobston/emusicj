package nz.net.kallisti.emusicj.controller;

import nz.net.kallisti.emusicj.misc.files.IFileNameCleaner;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * This contains preferences that are specific to the naxos/classicsonline
 * variant of the program.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class NaxosPreferences extends Preferences {

	@Inject
	public NaxosPreferences(IStrings strings, IFileNameCleaner nameCleaner) {
		super(strings, nameCleaner);
	}

	public boolean allowSaveFileAs() {
		return false;
	}

	/**
	 * Classicsonline wants more downloads by default
	 */
	@Override
	protected int getDefaultMinDownloads() {
		return 4;
	}

	@Override
	public boolean isAutoloadAllowed() {
		return false;
	}

}
