package nz.net.kallisti.emusicj.misc;

import java.io.File;
import java.io.FilenameFilter;

/**
 * <p>Used to recognise emusic metafiles by name. Looks for '.emp' and '.EMP'
 * extensions.</p>
 * 
 * <p>$Id:$</p>
 *
 * @author robin
 */
public class EMPFilenameFilter implements FilenameFilter {

	/**
	 * Default constructor 
	 */
	public EMPFilenameFilter() {
		super();
	}

	/**
	 * Returns true if the name matches the scheme of an eMusic metafile.
	 */
	public boolean accept(File dir, String name) {		
		return name.endsWith(".emp") || name.endsWith(".EMP");
	}

}
