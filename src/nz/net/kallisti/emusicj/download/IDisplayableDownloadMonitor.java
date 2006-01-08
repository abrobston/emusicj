package nz.net.kallisti.emusicj.download;

import java.io.File;

/**
 * An instance of this interface will provide information that allows it to be
 * displayed.
 * 
 * $Id:$
 *
 * @author robin
 */
public interface IDisplayableDownloadMonitor {
	
	/**
	 * Returns a file corresponding to this download, e.g. cover art. 
	 */
	public File getImageFile();

	/**
	 * Returns text to be displayed corresponding to this download. This text 
	 * is in the form of pairs of strings to allow nice layout. The first of the
	 * pair is the name of the value, e.g. "Artist", and the second one is the 
	 * actual value, e.g. "Nirvana".
	 */
	public String[][] getText();
	
}
