package nz.net.kallisti.emusicj.misc;

/**
 * <p>
 * This has utilities to provide platform-specific operations.
 * </p>
 * 
 * @author robin
 */
public class PlatformUtils {

	public static String getOsName() {
		return System.getProperty("os.name", "unknown").toLowerCase();
	}

	public static boolean isOSX() {
		String osname = getOsName();
		return (osname.startsWith("mac") || osname.startsWith("darwin"));
	}

	public static boolean isWindows() {
		String osname = getOsName();
		return osname.startsWith("windows");
	}

	public static boolean isLinux() {
		String osname = getOsName();
		return osname.startsWith("linux");
	}

}
