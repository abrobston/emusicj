package nz.net.kallisti.emusicj.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of handy utilities for working with lists
 * 
 * $Id:$
 * 
 * @author robin
 */
public class ListUtils {

	/**
	 * This joins the string value of the items in the list with the provided
	 * string between each entry
	 * 
	 * @param list
	 *            the list of items to join
	 * @param joiner
	 *            the string to place between the items
	 * @return the joined string
	 */
	public static String join(List<?> list, String joiner) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (Object o : list) {
			if (!first) {
				sb.append(joiner);
			} else {
				first = false;
			}
			sb.append(o.toString());
		}
		return sb.toString();
	}

	/**
	 * Creates a list from the provided values
	 * 
	 * @param values
	 *            the values to place in the list
	 * @return the list containing the values
	 */
	public static <T> List<T> list(T... values) {
		List<T> l = new ArrayList<T>(values.length);
		for (T v : values) {
			l.add(v);
		}
		return l;
	}

}
