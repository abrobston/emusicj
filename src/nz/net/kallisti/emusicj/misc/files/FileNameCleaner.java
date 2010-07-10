package nz.net.kallisti.emusicj.misc.files;

import java.util.ArrayList;
import java.util.List;

import nz.net.kallisti.emusicj.misc.PlatformUtils;

/**
 * This cleans the file name up to the point where it will work on fat32
 * filesystems. This is needed in order to download on windows, but is also
 * handy for fat32 based MP3 players.
 * 
 * $Id:$
 * 
 * @author robin
 */
public class FileNameCleaner implements IFileNameCleaner {

	/**
	 * Cleans the name by removing characters that are illegal and replacing
	 * them with underscore. Most of these characters are bad on win32, but
	 * useful for other platforms when people move them to fat32-based players.
	 * If the last character in a file or folder name is a '.' then it is
	 * replaced with underscore also, as that can cause issues too.
	 * 
	 * @param file
	 *            the file that we want to clean up.
	 * @param spacesToo
	 *            if true, spaces will be converted to underscores
	 */
	public List<String> cleanName(List<String> file, boolean spacesToo) {
		// Break the file into parts, rebuild it with clean components
		List<String> paths = new ArrayList<String>(file.size());
		paths = clean(file, spacesToo);
		return paths;
	}

	/**
	 * Cleans the file names in this list, assuming they're file paths.
	 * 
	 * @param paths
	 *            the list of path segments to clean
	 * @param spacesToo
	 *            if true, spaces will be converted to underscores
	 * @return the cleaned names
	 */
	private List<String> clean(List<String> paths, boolean spacesToo) {
		List<String> res = new ArrayList<String>(paths.size());
		for (String p : paths) {
			res.add(clean(p, spacesToo));
		}
		return res;
	}

	/**
	 * Actually cleans a string
	 * 
	 * @param p
	 *            the path to clean
	 * @param spacesToo
	 *            if true, spaces will be converted to underscores
	 * @return the cleaned path
	 */
	private String clean(String p, boolean spacesToo) {
		StringBuffer str = new StringBuffer(p);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < ' ' || c == '/' || c == '\\' || c > '~' || c == ':'
					|| c == '*' || c == '?' || c == '"' || c == '&' || c == '%'
					|| c == '>' || c == '<')
				str.setCharAt(i, '_');
			if (spacesToo && c == ' ')
				str.setCharAt(i, '_');
		}
		if (str.charAt(str.length() - 1) == '.')
			str.setCharAt(str.length() - 1, '_');
		// Windows sucks at filenames, so if we're on it, truncate the name of
		// the part to 50 chars.
		if (PlatformUtils.isWindows() && str.length() > 50) {
			str.delete(50, str.length());
		}
		return str.toString().trim();
	}

}
