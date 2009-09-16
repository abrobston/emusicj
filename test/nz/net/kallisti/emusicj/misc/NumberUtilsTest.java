package nz.net.kallisti.emusicj.misc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * <p>
 * Test cases for number utilities
 * </p>
 * 
 * @author robin
 */
public class NumberUtilsTest {

	/**
	 * Test method for
	 * {@link nz.net.kallisti.emusicj.misc.NumberUtils#max(int[])}.
	 */
	@Test
	public void testMax() {
		assertEquals(5, NumberUtils.max(1, 2, 3, 4, 5));
		assertEquals(-5, NumberUtils.max(-10, -9, -8, -7, -6, -5));
	}

	/**
	 * Test method for
	 * {@link nz.net.kallisti.emusicj.misc.NumberUtils#formatSeconds(int)}.
	 */
	@Test
	public void testFormatSeconds() {
		// Seconds only
		assertEquals("0:45", NumberUtils.formatSeconds(45));
		// Mins and seconds
		assertEquals("1:45", NumberUtils.formatSeconds(105));
		// Hours, mins and seconds
		assertEquals("1:01:45", NumberUtils.formatSeconds(3705));
		// Without minute padding being needed
		assertEquals("1:11:45", NumberUtils.formatSeconds(4305));
		// seconds padding
		assertEquals("0:01", NumberUtils.formatSeconds(1));
	}

}
