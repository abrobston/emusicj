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

	/**
	 * Returns the capitalised form of the string. E.g. abc -> Abc
	 * 
	 * @param str
	 *            the input string
	 * @return the input string with a capitalised first letter. If
	 *         <code>str</code> is <code>null</code>, then an empty string is
	 *         returned.
	 */
	public static String capitalise(String str) {
		if (str == null || str.length() == 0)
			return "";
		char c = Character.toUpperCase(str.charAt(0));
		StringBuffer sb = new StringBuffer(c);
		if (str.length() == 1)
			return sb.toString();
		sb.append(str.substring(1));
		return sb.toString();
	}

}
