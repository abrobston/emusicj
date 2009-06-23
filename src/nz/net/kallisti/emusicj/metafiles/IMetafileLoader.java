package nz.net.kallisti.emusicj.metafiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import nz.net.kallisti.emusicj.controller.IEMusicController;
import nz.net.kallisti.emusicj.download.IDownloader;
import nz.net.kallisti.emusicj.metafiles.exceptions.UnknownFileException;

/**
 * <p>
 * 
 * @author robin
 */
public interface IMetafileLoader {

	/**
	 * Calls the handlers for the metafile formats that it knows about, asking
	 * them if they understand the format. If one returns true, then it creates
	 * a downloaders for the files in the metafile, and notifies the controller
	 * of them.
	 * 
	 * @param controller
	 *            the controller to notify of the download
	 * @param filename
	 *            the metafile to get the download information from
	 * @return a list containing the downloaders representing the tracks/files
	 *         contained in this metafile
	 * @throws FileNotFoundException
	 *             if the file doesn't exist or is unreadable
	 */
	public List<IDownloader> load(IEMusicController controller, File filename)
			throws FileNotFoundException, IOException, UnknownFileException;

}