package nz.net.kallisti.emusicj.misc;

import java.io.File;

/**
 * <p>
 * Opens the specified folder in a system-dependent manner.
 * </p>
 * 
 * $Id:$
 * 
 * @author robin
 */
public class FolderOpener {

	public static void openDir(File dir) throws FolderOpenerException {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Runtime.getRuntime().exec("open " + dir.toString());
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec("explorer.exe /e," + dir.toString());
			} else { // assume Unix or Linux
				Runtime.getRuntime().exec(
						new String[] { "xdg-open", dir.toString() });
			}
		} catch (Exception e) {
			throw new FolderOpenerException(e);
		}
	}

}