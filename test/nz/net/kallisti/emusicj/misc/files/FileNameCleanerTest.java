package nz.net.kallisti.emusicj.misc.files;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A test case for the file name cleaner.
 * 
 * $Id:$
 * 
 * @author robin
 */
public class FileNameCleanerTest {

	private FileNameCleaner cleaner;

	@Before
	public void setUp() {
		cleaner = new FileNameCleaner();
	}

	@Test
	public void testFileNameCleaner() {
		List<String> input = new ArrayList<String>();
		List<String> expected = new ArrayList<String>();
		input.add("<>");
		input.add("\"?");
		expected.add("__");
		expected.add("__");
		Assert.assertEquals(expected, cleaner.cleanName(input, false));

		input.clear();
		expected.clear();
		input.add("hello...");
		input.add(".foo.");
		input.add("bar baz");
		expected.add("hello.._");
		expected.add(".foo_");
		expected.add("bar_baz");

		Assert.assertEquals(expected, cleaner.cleanName(input, true));
	}

}
