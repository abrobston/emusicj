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
import nz.net.kallisti.emusicj.misc.ListUtils;
import nz.net.kallisti.emusicj.misc.LogUtils;
import nz.net.kallisti.emusicj.misc.StringUtils;
import nz.net.kallisti.emusicj.misc.files.IFileNameCleaner;
import nz.net.kallisti.emusicj.strings.IStrings;

import com.google.inject.Inject;

/**
 * <p>
 * This is a singleton class that tracks the application preferences. It does
 * things like provide a full filename given track information, and provides
 * access to other user-definable options.
 * </p>
 * <p>
 * It's started to become a bit of a monster, being a central point of all sorts
 * of information.
 * </p>
 * 
 * @author robin
 */
public abstract class Preferences implements IPreferences {

	private static final String PROXY_PORT = "proxyPort";
	private static final String PROXY_HOST = "proxyHost";
	private static final String USE_PROXY = "useProxy";
	private static final String LOG_LEVEL = "logLevel";
	private static final String WINDOWS_MAX_PATH_LENGTH = "windowsMaxPathLength";
	private static final String DOWNLOAD_COVER_ART = "downloadCoverArt";
	private static final String MULTIDISK_NAME = "disc";

	public final String statePath;
	private String path;
	private String filePattern;
	private int minDownloads;
	private Properties props;
	private String proxyHost = "";
	private int proxyPort = 0;
	private final List<IPreferenceChangeListener> listeners = Collections
			.synchronizedList(new ArrayList<IPreferenceChangeListener>());
	private final IStrings strings;
	private boolean firstLaunch = false;
	private String coverArtFilename;
	private int windowsMaxPathLength = 250;
	private boolean dlCoverArt = true;
	private final IFileNameCleaner nameCleaner;
	private final Logger logger;

	@Inject
	public Preferences(IStrings strings, IFileNameCleaner nameCleaner) {
		super();
		logger = LogUtils.getLogger(this);
		this.strings = strings;
		this.nameCleaner = nameCleaner;
		this.path = buildDefaultSavePath();
		this.statePath = System.getProperty("user.home") + File.separatorChar
				+ "." + strings.getAppPathname() + File.separatorChar;
		this.filePattern = strings.getDefaultFilePattern();
		this.minDownloads = getDefaultMinDownloads();
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

	/**
	 * This provides the default number of downloads that will happen at once.
	 * 
	 * @return a number. Probably a fairly small one.
	 */
	protected int getDefaultMinDownloads() {
		return 2;
	}

	/**
	 * This creates the default save path for saving files in. It checks to see
	 * if some standard ones exist, and if they do, it uses the first one of
	 * them that it finds. If not, it defaults to 'My Music'.
	 * 
	 * @return a string containing the default path for saving downloaded files
	 *         to
	 */
	private String buildDefaultSavePath() {
		String home = System.getProperty("user.home");
		String[] check = { "My Music", "Music" };
		for (String dir : check) {
			if (new File(home, dir).exists())
				return home + File.separatorChar + dir + File.separatorChar
						+ strings.getAppPathname();
		}
		return home + File.separatorChar + check[check.length - 1]
				+ File.separatorChar + strings.getAppPathname();
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
			dlCoverArt = Boolean.parseBoolean(props.getProperty(
					DOWNLOAD_COVER_ART, "true"));
			proxyHost = props.getProperty(PROXY_HOST, proxyHost);
			proxyPort = Integer.parseInt(props.getProperty(PROXY_PORT,
					proxyPort + ""));
			setDebugLevel(props.getProperty(LOG_LEVEL, "INFO"));
			windowsMaxPathLength = Integer.parseInt(props.getProperty(
					WINDOWS_MAX_PATH_LENGTH, windowsMaxPathLength + ""));
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
			String artist, String format, Integer disk, Integer diskNum) {
		DecimalFormat df = new DecimalFormat("00");
		String[] filePatternParts;
		// Ticket #70 - turn '/' and '\' to whatever is appropriate on this
		// platform to prevent confusion.
		String pattern = filePattern.replace('/', File.separatorChar).replace(
				'\\', File.separatorChar);
		if (File.separator.equals("\\")) {
			// avoid issues with windows and regex split
			filePatternParts = pattern.split("\\\\");
		} else {
			filePatternParts = pattern.split(File.separator);
		}
		// This calculates the multidisk name, which will be inserted after the
		// album name
		String multiDisk = null;
		if (disk != null && diskNum != null && diskNum > 1) {
			String diskForm = formatToMatch(disk, diskNum);
			multiDisk = MULTIDISK_NAME + " " + diskForm;
		}

		List<String> nameParts = new ArrayList<String>();
		for (String part : filePatternParts) {
			StringBuffer convPattern = new StringBuffer(part);
			int pos;
			boolean wasAlbum = false;
			while ((pos = convPattern.indexOf("%a")) != -1) {
				convPattern.replace(pos, pos + 2, album.trim());
				wasAlbum = true;
			}
			while ((pos = convPattern.indexOf("%b")) != -1)
				convPattern.replace(pos, pos + 2, artist.trim());
			while ((pos = convPattern.indexOf("%n")) != -1)
				convPattern.replace(pos, pos + 2, df.format(track));
			while ((pos = convPattern.indexOf("%t")) != -1)
				convPattern.replace(pos, pos + 2, song.trim());
			nameParts.add(convPattern.toString());
			if (wasAlbum && multiDisk != null)
				nameParts.add(multiDisk);
		}
		List<String> finalNameParts = nameCleaner.cleanName(nameParts, "true"
				.equals(props.getProperty("spacesToUnderscore", "false")));

		String convPattern = ListUtils.join(finalNameParts, File.separator);
		String fname = path + File.separatorChar + convPattern;
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows")) {
			if (fname.length() < windowsMaxPathLength) {
				fname = fname
						.substring(
								0,
								fname.length() > windowsMaxPathLength ? windowsMaxPathLength
										: fname.length());
			}
		}
		return fname.toString() + format;
	}

	/**
	 * This formats the first value to ensure that it has the same number of
	 * digits as 'format'. Negatives etc. will cause this to be weird, so don't
	 * do that.
	 * 
	 * @param value
	 *            the value to format
	 * @param format
	 *            the value that contains the number of digits we want to match
	 * @return a string containing 'value' padded to the appropriate number of
	 *         digits.
	 */
	private static String formatToMatch(int value, int format) {
		// most common case is handled quickly
		if (format < 10) {
			return String.valueOf(value);
		}
		String formValue = String.valueOf(format);
		String fmt = StringUtils.repeat("0", formValue.length());
		DecimalFormat df = new DecimalFormat(fmt);
		return df.format(value);
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
	public File getPathFor(int track, String song, String album, String artist,
			Integer disk, Integer diskNum) {
		String filename = getFilename(track, song, album, artist, ".foo", disk,
				diskNum);
		File file = new File(filename);
		File path = new File(file.getParent());
		return path;
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

	public boolean downloadCoverArt() {
		return dlCoverArt;
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
			props.setProperty(USE_PROXY, "FALSE");
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
	 * @param levelStr
	 *            one of the logging levels defined in {@link Level},
	 *            case-insensitive
	 */
	private void setDebugLevel(String levelStr) {
		Logger logger = Logger.getLogger("nz.net.kallisti.emusicj");
		Level level = null;
		if (levelStr.equalsIgnoreCase("severe")) {
			level = Level.SEVERE;
		} else if (levelStr.equalsIgnoreCase("warning")) {
			level = Level.WARNING;
		} else if (levelStr.equalsIgnoreCase("info")) {
			level = Level.INFO;
		} else if (levelStr.equalsIgnoreCase("config")) {
			level = Level.CONFIG;
		} else if (levelStr.equalsIgnoreCase("fine")) {
			level = Level.FINE;
		} else if (levelStr.equalsIgnoreCase("finer")) {
			level = Level.FINER;
		} else if (levelStr.equalsIgnoreCase("finest")) {
			level = Level.FINEST;
		} else if (levelStr.equalsIgnoreCase("off")) {
			level = Level.OFF;
		} else if (levelStr.equalsIgnoreCase("all")) {
			level = Level.ALL;
		} else
			level = Level.INFO;
		logger.setLevel(level);
		// Handler handler = new ConsoleHandler();
		// handler.setLevel(level);
		// logger.addHandler(handler);
	}

	public File getIconCacheDir() {
		return new File(statePath, "iconcache");
	}

	public boolean isAutoloadAllowed() {
		// Default
		return true;
	}

	public int getMaxDownloadFailures() {
		String failures = getProperty("maxDownloadFailures",
				getDefaultMaxDownloadFailures() + "");
		try {
			return Integer.parseInt(failures);
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Unparsable value for maxDownloadFailures: " + failures);
		}
		return getDefaultMaxDownloadFailures();
	}

	/**
	 * The default number of allowed download failures. May be overridden if a
	 * different value is wanted.
	 * 
	 * @return an int specifying the default number of allowed download failures
	 */
	protected int getDefaultMaxDownloadFailures() {
		return 5;
	}

	public boolean showTrackControls() {
		return true;
	}

	/**
	 * By default we show the prefs
	 */
	public boolean showPrefsOnFirstRun() {
		return true;
	}

}
