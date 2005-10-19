package nz.net.kallisti.emusicj.controller;

import java.text.DecimalFormat;

/**
 * <p>This is a singleton class that tracks the application preferences.
 * To get an instance, use the getInstance() method. It does things like
 * provide a full filename given track information, and provides access to
 * other user-definable options.</p>
 * 
 * 
 * $Id:$
 *
 * @author robin
 */
public class Preferences {

	private static Preferences instance;
	
	private Preferences() {
		super();
	}
	
	public static Preferences getInstance() {
		if (instance == null)
			instance = new Preferences();
		// TODO load user prefs
		return instance;
	}
	
	public String getFilename(int track, String song, String album, String artist) {
		// TODO this is for testing only - needs to be configurable
		String prefix = System.getProperty("user.home")+"/mp3/emusic/";
		DecimalFormat df = new DecimalFormat("00");
		prefix += artist+"/"+album+"/"+df.format(track)+" "+song+".mp3";
		return prefix;
	}
	
}
