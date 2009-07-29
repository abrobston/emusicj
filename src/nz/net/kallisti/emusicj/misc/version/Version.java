package nz.net.kallisti.emusicj.misc.version;

/**
 * <p>
 * Stores and compares version numbers. It understands that 2.13 &gt; 2.5.
 * Version numbers can contain any number of decimal places.
 * </p>
 * 
 * @author robin
 */
public class Version implements Comparable<Version> {

	private final int[] version;

	/**
	 * Create an instance of the <code>Version</code> from a string. This will
	 * throw an {@link InvalidVersionException} if it isn't understood.
	 */
	public Version(String version) throws InvalidVersionException {
		this.version = parse(version);
	}

	/**
	 * Parses a version numbers by splitting at the '.'.
	 * 
	 * @param version
	 *            the version number as a string
	 * @return the split up and arrayed version number
	 */
	private int[] parse(String version) throws InvalidVersionException {
		String[] split = version.split("\\.");
		int[] v = new int[split.length];
		try {
			for (int i = 0; i < split.length; i++) {
				v[i] = Integer.parseInt(split[i]);
			}
		} catch (NumberFormatException e) {
			throw new InvalidVersionException(
					"Error parsing version number: non-numeric value", e);
		}
		return v;
	}

	public int compareTo(Version o) {
		int smallest = Math.min(o.version.length, this.version.length);
		for (int i = 0; i < smallest; i++) {
			if (this.version[i] == o.version[i])
				continue;
			if (this.version[i] < o.version[i])
				return -1;
			return 1;
		}
		// If we get here, they're equal for the points checked. This means that
		// the longer one is the higher value
		if (this.version.length == o.version.length)
			return 0;
		if (this.version.length > o.version.length)
			return 1;
		return -1;
	}

}
