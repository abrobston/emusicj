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

	/**
	 * Given a duration in seconds, this turns it into [hour:]minute:seconds
	 * 
	 * @param seconds
	 *            the duration in seconds
	 * @return a nice string form of the duration
	 */
	public static String formatSeconds(int duration) {
		int secs = duration % 60;
		int mins = (duration / 60) % 60;
		int hours = (duration / 3600);
		boolean padMinutes = (hours != 0 && mins < 10);
		return (hours != 0 ? hours + ":" : "") + (padMinutes ? "0" : "") + mins
				+ ":" + (secs < 10 ? "0" : "") + secs;
	}

}
