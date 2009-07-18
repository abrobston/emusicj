package nz.net.kallisti.emusicj.misc;

/**
 * <p>
 * Handy utils for working with numbers
 * </p>
 * 
 * @author robin
 */
public class NumberUtils {

	/**
	 * Find the maximum in a list of numbers
	 * 
	 * @param n
	 *            the list of numbers
	 * @return the largest one in there
	 */
	public static int max(int... n) {
		int curr = n[0];
		for (int i = 1; i < n.length; i++)
			curr = Math.max(curr, n[i]);
		return curr;
	}

}
