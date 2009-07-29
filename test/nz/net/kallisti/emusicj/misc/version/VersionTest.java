package nz.net.kallisti.emusicj.misc.version;

import junit.framework.Assert;

import org.junit.Test;

public class VersionTest {

	@Test
	public void versionTest() throws InvalidVersionException {
		Version v1;
		Version v2;

		v1 = new Version("1.0");
		v2 = new Version("1.0");

		Assert.assertEquals(0, v1.compareTo(v2));

		v2 = new Version("1.1");

		Assert.assertEquals(-1, v1.compareTo(v2));

		v2 = new Version("1.0.1");

		Assert.assertEquals(-1, v1.compareTo(v2));
	}

}
