package nz.net.kallisti.emusicj.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Properties;

import nz.net.kallisti.emusicj.Constants;

/**
 * <p>This is a singleton class that tracks the application preferences.
 * To get an instance, use the getInstance() method. It does things like
 * provide a full filename given track information, and provides access to
 * other user-definable options.</p>
 * 
 * 
 * $Id$
 *
 * @author robin
 */
public class Preferences {

	private static Preferences instance;
	
	String statePath = System.getProperty("user.home")+
		File.separatorChar+Constants.STATE_DIR+File.separatorChar;
	private String path = System.getProperty("user.home")+File.separatorChar+
		"mp3"+File.separatorChar+"emusic";
	private String filePattern = "%b"+File.separatorChar+"%a"+
		File.separatorChar+"%n %t.mp3";
	private int minDownloads = 2;
	private Properties props;
	
	private Preferences() {
		super();
	}
	
	public static Preferences getInstance() {
		if (instance == null)
			instance = new Preferences();
		instance.loadProps();
		return instance;
	}

	private void loadProps() {
		props = new Properties();
		try {
			InputStream in = new FileInputStream(System.getProperty("user.home")+
					File.separatorChar+Constants.STATE_DIR+File.separatorChar+
					"emusicj.prop");
			props.load(in);
			path = props.getProperty("savePath", path);
			filePattern = props.getProperty("savePattern", filePattern);
			minDownloads = Integer.parseInt(props.getProperty("minDownloads", minDownloads+""));
		} catch (IOException e) {
			// We don't care, it'll just use the defaults
		}
	}
	
	/**
	 * Save the preferences to a file
	 */
	public void save() {
		try {
			File outFile = new File(System.getProperty("user.home")+
					File.separatorChar+Constants.STATE_DIR+File.separatorChar+
					"emusicj.prop");
			File dir = outFile.getParentFile();
			dir.mkdirs();
			OutputStream out = new FileOutputStream(outFile);
			props.store(out,Constants.APPNAME);
			out.close();
		} catch (IOException e) {
			System.err.println("There was an error saving the preferences:");
			e.printStackTrace();
		}
	}
	
	public String getFilename(int track, String song, String album, String artist) {
		DecimalFormat df = new DecimalFormat("00");
		StringBuffer songB = new StringBuffer(song);
		StringBuffer albumB = new StringBuffer(album);
		StringBuffer artistB = new StringBuffer(artist);
		// Remove any bad characters from the names
		cleanName(songB);
		cleanName(albumB);
		cleanName(artistB);
		StringBuffer convPattern = new StringBuffer(filePattern);
		int pos;
		while ((pos = convPattern.indexOf("%a"))!= -1)
			convPattern.replace(pos,pos+2,album);
		while ((pos = convPattern.indexOf("%b"))!= -1)
			convPattern.replace(pos,pos+2,artist);
		while ((pos = convPattern.indexOf("%n"))!= -1) 
			convPattern.replace(pos,pos+2,df.format(track));
		while ((pos = convPattern.indexOf("%t"))!= -1)
			convPattern.replace(pos,pos+2,song);
		String fname = path+File.separatorChar+convPattern;
		return fname;
	}

	/**
	 * @param str
	 */
	private void cleanName(StringBuffer str) {
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			if (c < ' ' || c == '/' || c == '\\' || c > '~')
				str.setCharAt(i,'_');
		}
	}
	
	/**
	 * @param text
	 */
	public void setPath(String path) {
		props.setProperty("savePath",path);
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	/**
	 * @param filePattern
	 */
	public void setFilePattern(String filePattern) {
		props.setProperty("savePattern",filePattern);
		this.filePattern=filePattern;		
	}
	
	public String getFilePattern() {
		return filePattern;
	}
	
	public int getMinDownloads() {
		return minDownloads;
	}

	public void setMinDownloads(int minDownloads) {
		props.setProperty("minDownloads",minDownloads+"");
		this.minDownloads = minDownloads;
	}
	
	/**
	 * A way of storing property information with arbitrary tags. Don't stomp
	 * on the values already used in this class.
	 * @param key the key to store the value with
	 * @param value the value to store
	 */
	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public String getProperty(String key, String def) {
		return props.getProperty(key, def);
	}


}
