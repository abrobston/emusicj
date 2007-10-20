package nz.net.kallisti.emusicj.strings;

/**
 * <p>
 * Handles the strings to allow customisation of them between versions of the
 * application
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public interface IStrings {

	/**
	 * The name of the application, as it appears on the title of the window
	 * 
	 * @return the name of the application
	 */
	public String getAppName();

	/**
	 * The short form of the application name, for system tray stuff
	 * 
	 * @return the short form of the application name
	 */
	public String getShortAppName();

	/**
	 * The name of the application in a form suitable for putting into paths
	 * 
	 * @return the name of the application in a form for putting into paths
	 */
	public String getAppPathname();

	/**
	 * The explanation on how files to be saved should be named
	 * 
	 * @return the explanation on how files to be saved should be named
	 */
	public String getFileNamingDetails();

	/**
	 * The list of names that appear in the open file dialogue describing the
	 * file types to open
	 * 
	 * @return an array containing the descriptions of the file types the user
	 *         can select
	 */
	public String[] getOpenDialogueFilterNames();

	/**
	 * The file extensions that the file dialogue will match. Must correspond to
	 * the descriptions returned by {@link #getOpenDialogueFilterNames()}. e.g.
	 * '*.emp'
	 * 
	 * @return the file extensions that the file dialogue will match
	 */
	public String[] getOpenDialogueFilterExtensions();

	/**
	 * The description presented to the user regarding automatically opening
	 * files.
	 * 
	 * @return the description presented to the user regarding automatically
	 *         opening files
	 */
	public String getAutoLoadDescription();

	/**
	 * The text to be shown in the 'about' dialogue
	 * 
	 * @return the text to be shown in the 'about' dialogue
	 */
	public String getAboutBoxText();

	/**
	 * The default naming pattern for saving files
	 * 
	 * @return the default naming pattern for saving files
	 */
	public String getDefaultFilePattern();

	/**
	 * This provides the base name for the XML node that stores the download
	 * state of the application.
	 * 
	 * @return the XML node name for the download state
	 */
	public String getXMLBaseNodeName();

	/**
	 * The version of the application.
	 * 
	 * @return the version of the application
	 */
	public String getVersion();
	
	/**
	 * This provides the base name (without extension) for cover art files
	 * @return the base name for cover art files
	 */
	public String getCoverArtName();
}
