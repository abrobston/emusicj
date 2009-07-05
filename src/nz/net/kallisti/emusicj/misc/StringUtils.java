package nz.net.kallisti.emusicj.misc;

/**
 * <p>
 * Handy string utilities
 * </p>
 * 
 * @author robin
 */
public class StringUtils {

	/**
	 * This repeats the provided string a number of times
	 * 
	 * @param str
	 *            the string to repeat
	 * @param count
	 *            the number of times to repeat it
	 * @return the resulting string
	 */
	public static String repeat(String str, int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++)
			sb.append(str);
		return sb.toString();
	}

}
