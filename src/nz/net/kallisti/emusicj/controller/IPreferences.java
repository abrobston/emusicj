package nz.net.kallisti.emusicj.controller;

import java.io.File;

/**
 * <p>
 * Interface for the Preferences implementation. This is pretty much a utility
 * class for providing information about the system and its (usually saved)
 * state.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public interface IPreferences {

	/**
	 * Save the preferences to a file
	 */
	public void save();

	/**
	 * Provides the path and filename that the described track should be saved
	 * as.
	 * 
	 * @param track
	 *            the track number
	 * @param song
	 *            the track name
	 * @param album
	 *            the album the track belongs to
	 * @param artist
	 *            the artist of the track
	 * @param format
	 *            the format, including the leading '.' (e.g. ".mp3")
	 * @param disk
	 *            the disk number that this belongs to. May be <code>null</code>
	 *            .
	 * @param diskNum
	 *            the total number of disks that are in this album. May be
	 *            <code>null</code>.
	 * @return a string that is the full path and filename for the file.
	 */
	public String getFilename(int track, String song, String album,
			String artist, String format, Integer disk, Integer diskNum);

	/**
	 * Works out the directory where a track with the provided parameters will
	 * be saved to
	 * 
	 * @param track
	 * @param song
	 * @param album
	 * @param artist
	 * @param diskCount
	 *            the total number of disks in this album, may be
	 *            <code>null</code>.
	 * @param disk
	 *            the disk that this track belongs to, may be <code>null</code>
	 * @return
	 */
	public File getPathFor(int track, String song, String album, String artist,
			Integer disk, Integer diskCount);

	/**
	 * @param text
	 */
	public void setSavePath(String path);

	/**
	 * The path where downloaded files are saved to
	 * 
	 * @return the path where downloads go to
	 */
	public String getSavePath();

	public String getStatePath();

	/**
	 * @param filePattern
	 */
	public void setFilePattern(String filePattern);

	public String getFilePattern();

	public int getMinDownloads();

	public void setMinDownloads(int minDownloads);

	/**
	 * A way of storing property information with arbitrary tags. Don't stomp on
	 * the values already used in this class.
	 * 
	 * @param key
	 *            the key to store the value with
	 * @param value
	 *            the value to store
	 */
	public void setProperty(String key, String value);

	public String getProperty(String key);

	public String getProperty(String key, String def);

	/**
	 * Says whether the user wants to the program to check for updates
	 * 
	 * @return true if the user specified they want to check (default), false
	 *         otherwise.
	 */
	public boolean checkForUpdates();

	public void setCheckForUpdates(boolean check);

	public boolean removeCompletedDownloads();

	public void setRemoveCompletedDownloads(boolean remove);

	public String getProxyHost();

	public int getProxyPort();

	public boolean usingProxy();

	public String getDropDir();

	public void setDropDir(String dd);

	public void addListener(IPreferenceChangeListener l);

	public void setProxy(boolean noProxy, String host, String port);

	/**
	 * Is this the first launch of the application, based on the existence of a
	 * preferences file.
	 * 
	 * @return true if this is the first time the application has been launched,
	 *         false otherwise.
	 */
	public boolean isFirstLaunch();

	/**
	 * Some variants don't want the 'save file as' box to show up, this
	 * determines whether it should be there or not.
	 * 
	 * @return true if the 'save file as' preference should be show, false
	 *         otherwise
	 */
	public boolean allowSaveFileAs();

	/**
	 * Gets the user-defined cover art filename.
	 * 
	 * @return the user-defined cover art filename, or <code>null</code> if none
	 *         has been defined.
	 */
	public String getCoverArtFilename();

	/**
	 * This will return true if cover art should be downloaded
	 * 
	 * @return true if the cover art should be downloaded, false otherwise
	 */
	public boolean downloadCoverArt();

	/**
	 * Provides the directory that should be used for saving icons into.
	 * 
	 * @return a directory for caching icons in
	 */
	public File getIconCacheDir();

	/**
	 * If true, then autoloading of files from a directory should be allowed. If
	 * false, it shouldn't. This is mostly used in relation to branding
	 * differences.
	 * 
	 * @return true to allow autoloading of files, false to not allow it.
	 */
	public boolean isAutoloadAllowed();

	/**
	 * The maximum number of failures that will be allowed before downloading
	 * gives up on the file
	 * 
	 * @return the maximum number of downloads
	 */
	public int getMaxDownloadFailures();

}
