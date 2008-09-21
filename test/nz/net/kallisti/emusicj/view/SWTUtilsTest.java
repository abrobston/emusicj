package nz.net.kallisti.emusicj.view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test case for {@link SWTUtils}
 * 
 * $Id:$
 * 
 * @author robin
 */
public class SWTUtilsTest {

	/**
	 * Test method for
	 * {@link nz.net.kallisti.emusicj.view.SWTUtils#deMonic(java.lang.String)}.
	 */
	@Test
	public void testDeMonic() {
		assertEquals("abcdef", SWTUtils.deMonic("abcdef"));
		assertEquals("abc&&def", SWTUtils.deMonic("abc&def"));
		assertEquals("&&abcdef", SWTUtils.deMonic("&abcdef"));
		assertEquals("abcdef&&", SWTUtils.deMonic("abcdef&"));
		assertEquals("abc&&&&def", SWTUtils.deMonic("abc&&def"));
	}

}
