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

	public String getFilename(int track, String song, String album,
			String artist, String format);

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
	public File getPathFor(int track, String song, String album, String artist);

	/**
	 * @param text
	 */
	public void setSavePath(String path);

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

}
