/*
    eMusic/J - a Free software download manager for emusic.com
    http://www.kallisti.net.nz/emusicj
    
    Copyright (C) 2005, 2006 Robin Sheat, Curtis Cooley

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */
package nz.net.kallisti.emusicj.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nz.net.kallisti.emusicj.controller.IPreferenceChangeListener.Pref;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * This is a singleton class that tracks the application preferences. To get an
 * instance, use the getInstance() method. It does things like provide a full
 * filename given track information, and provides access to other user-definable
 * options.
 * </p>
 * 
 * 
 * $Id$
 * 
 * @author robin
 */
public abstract class Preferences implements IPreferences {

	private static final String PROXY_PORT = "proxyPort";
	private static final String PROXY_HOST = "proxyHost";
	private static final String USE_PROXY = "useProxy";
	private static final String DEBUG_MODE = "debugMode";

	public final String statePath;
	private String path;
	private String filePattern;
	private int minDownloads = 2;
	private Properties props;
	private String proxyHost = "";
	private int proxyPort = 0;
	private final List<IPreferenceChangeListener> listeners = Collections
			.synchronizedList(new ArrayList<IPreferenceChangeListener>());
	private final IStrings strings;
	private boolean firstLaunch = false;
	private String coverArtFilename;

	@Inject
	public Preferences(IStrings strings) {
		super();
		this.strings = strings;
		this.path = System.getProperty("user.home") + File.separatorChar
				+ "mp3" + File.separatorChar + strings.getAppPathname();
		this.statePath = System.getProperty("user.home") + File.separatorChar
				+ "." + strings.getAppPathname() + File.separatorChar;
		this.filePattern = strings.getDefaultFilePattern();
		// Make sure the state path exists, as other things may need it
		new File(statePath).mkdirs();
		// Set the proxy variables
		try {
			URL url = new URL(System.getProperty("env.http_proxy"));
			proxyHost = url.getHost();
			proxyPort = url.getPort();
		} catch (MalformedURLException e) {
		}
		loadProps();
	}

	private void loadProps() {
		props = new Properties();
		try {
			InputStream in = new FileInputStream(statePath
					+ strings.getAppPathname() + ".prop");
			props.load(in);
			path = props.getProperty("savePath", path);
			filePattern = props.getProperty("savePattern", filePattern);
			coverArtFilename = props.getProperty("coverArtFilename");
			// Compatibility fix if moving from <0.07 to >=0.07
			// TODO remove this some time in the future (31/10/05)
			if (filePattern.length() > 4
					&& filePattern.substring(filePattern.length() - 4)
							.equalsIgnoreCase(".mp3"))
				filePattern = filePattern
						.substring(0, filePattern.length() - 4);
			minDownloads = Integer.parseInt(props.getProperty("minDownloads",
					minDownloads + ""));
			proxyHost = props.getProperty(PROXY_HOST, proxyHost);
			proxyPort = Integer.parseInt(props.getProperty(PROXY_PORT,
					proxyPort + ""));
			setDebugLevel(props.getProperty(DEBUG_MODE, "INFO"));
		} catch (IOException e) {
			// We don't care, it'll just use the defaults
			// but do remember that this is the first execution, other things
			// may be interested in that.
			firstLaunch = true;
		}
	}

	/**
	 * Save the preferences to a file
	 */
	public synchronized void save() {
		try {
			File outFile = new File(statePath + strings.getAppPathname()
					+ ".prop");
			File dir = outFile.getParentFile();
			dir.mkdirs();
			OutputStream out = new FileOutputStream(outFile);
			props.store(out, strings.getShortAppName());
			out.close();
		} catch (IOException e) {
			System.err.println("There was an error saving the preferences:");
			e.printStackTrace();
		}
	}

	public String getFilename(int track, String song, String album,
			String artist, String format) {
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
		while ((pos = convPattern.indexOf("%a")) != -1)
			convPattern.replace(pos, pos + 2, albumB.toString());
		while ((pos = convPattern.indexOf("%b")) != -1)
			convPattern.replace(pos, pos + 2, artistB.toString());
		while ((pos = convPattern.indexOf("%n")) != -1)
			convPattern.replace(pos, pos + 2, df.format(track));
		while ((pos = convPattern.indexOf("%t")) != -1)
			convPattern.replace(pos, pos + 2, songB.toString());
		String fname = path + File.separatorChar + convPattern + format;
		return fname;
	}

	/**
	 * Works out the directory where a track with the provided parameters will
	 * be saved to
	 * 
	 * @param track
	 * @param song
	 * @param album
	 * @param artist
	 * @return
	 */
	public File getPathFor(int track, String song, String album, String artist) {
		String filename = getFilename(track, song, album, artist, ".foo");
		File file = new File(filename);
		File path = new File(file.getParent());
		return path;
	}

	/**
	 * @param str
	 */
	void cleanName(StringBuffer str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < ' ' || c == '/' || c == '\\' || c > '~' || c == ':'
					|| c == '*' || c == '?' || c == '"' || c == '&')
				str.setCharAt(i, '_');
		}
	}

	/**
	 * @param text
	 */
	public synchronized void setSavePath(String path) {
		props.setProperty("savePath", path);
		this.path = path;
		notify(Pref.SAVE_PATH);
	}

	public synchronized String getSavePath() {
		return path;
	}

	public String getStatePath() {
		return statePath;
	}

	/**
	 * @param filePattern
	 */
	public synchronized void setFilePattern(String filePattern) {
		props.setProperty("savePattern", filePattern);
		this.filePattern = filePattern;
		notify(Pref.FILE_PATTERN);
	}

	public synchronized String getFilePattern() {
		return filePattern;
	}

	public synchronized int getMinDownloads() {
		return minDownloads;
	}

	public synchronized void setMinDownloads(int minDownloads) {
		props.setProperty("minDownloads", minDownloads + "");
		this.minDownloads = minDownloads;
		notify(Pref.MIN_DOWNLOADS);
	}

	/**
	 * A way of storing property information with arbitrary tags. Don't stomp on
	 * the values already used in this class.
	 * 
	 * @param key
	 *            the key to store the value with
	 * @param value
	 *            the value to store
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

	/**
	 * Says whether the user wants to the program to check for updates
	 * 
	 * @return true if the user specified they want to check (default), false
	 *         otherwise.
	 */
	public synchronized boolean checkForUpdates() {
		String updateCheck = props.getProperty("checkForUpdates");
		if (updateCheck == null)
			return true;
		return updateCheck.equalsIgnoreCase("true");
	}

	public synchronized void setCheckForUpdates(boolean check) {
		props.setProperty("checkForUpdates", check ? "true" : "false");
		notify(Pref.CHECK_FOR_UPDATES);
	}

	public synchronized boolean removeCompletedDownloads() {
		String removeCompleted = props.getProperty("removeCompletedDownloads",
				"true");
		return removeCompleted.equalsIgnoreCase("true");
	}

	public synchronized void setRemoveCompletedDownloads(boolean remove) {
		props
				.setProperty("removeCompletedDownloads", remove ? "true"
						: "false");
		notify(Pref.REMOVE_COMPLETED_DOWNLOADS);
	}

	public synchronized String getProxyHost() {
		return proxyHost;
	}

	public synchronized int getProxyPort() {
		return proxyPort;
	}

	public boolean usingProxy() {
		return "TRUE".equalsIgnoreCase(props.getProperty(USE_PROXY));
	}

	synchronized void setProxyHost(String host) {
		proxyHost = host;
		props.setProperty(PROXY_HOST, host);
		notify(Pref.PROXY_HOST);
	}

	synchronized void setProxyPort(int port) {
		proxyPort = port;
		props.setProperty(PROXY_PORT, port + "");
		notify(Pref.PROXY_PORT);
	}

	public synchronized String getDropDir() {
		return props.getProperty("dropDir");
	}

	public synchronized void setDropDir(String dd) {
		props.setProperty("dropDir", dd);
		notify(Pref.DROP_DIR);
	}

	public void addListener(IPreferenceChangeListener l) {
		listeners.add(l);
	}

	protected void notify(Pref p) {
		for (IPreferenceChangeListener l : listeners)
			l.preferenceChanged(p);
	}

	public synchronized void setProxy(boolean noProxy, String host, String port) {
		if (noProxy) {
			// props.remove(PROXY_HOST);
			// props.remove(PROXY_PORT);
			props.setProperty(USE_PROXY, "FALSE");
			// proxyHost = "";
			// proxyPort = 0;
		} else {
			setProxyHost(host);
			props.setProperty(USE_PROXY, "TRUE");
			try {
				setProxyPort(Integer.parseInt(port));
			} catch (NumberFormatException ignoreForNow) {
			}
		}
	}

	public boolean isFirstLaunch() {
		return firstLaunch;
	}

	public String getCoverArtFilename() {
		return coverArtFilename;
	}

	/**
	 * This sets the debug level for the heirarchy nz.net.kallisti.emusicj to be
	 * what is specified by 'level'. If level is an unknown value, it defaults
	 * to 'INFO'.
	 * 
	 * @param level
	 *            one of the logging levels defined in {@link Level},
	 *            case-insensitive
	 */
	private void setDebugLevel(String level) {
		Logger logger = Logger.getLogger("nz.net.kallisti.emusicj");
		if (level.equalsIgnoreCase("severe")) {
			logger.setLevel(Level.SEVERE);
		} else if (level.equalsIgnoreCase("warning")) {
			logger.setLevel(Level.WARNING);
		} else if (level.equalsIgnoreCase("info")) {
			logger.setLevel(Level.INFO);
		} else if (level.equalsIgnoreCase("config")) {
			logger.setLevel(Level.CONFIG);
		} else if (level.equalsIgnoreCase("fine")) {
			logger.setLevel(Level.FINE);
		} else if (level.equalsIgnoreCase("finer")) {
			logger.setLevel(Level.FINER);
		} else if (level.equalsIgnoreCase("finest")) {
			logger.setLevel(Level.FINEST);
		} else if (level.equalsIgnoreCase("off")) {
			logger.setLevel(Level.OFF);
		} else
			logger.setLevel(Level.INFO);
	}

}
