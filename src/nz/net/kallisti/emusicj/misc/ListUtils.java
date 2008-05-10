package nz.net.kallisti.emusicj.misc;

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

}
