package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link SevenZipJBindingInitCheck} snippet
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public class SevenZipJBindingInitCheckTest extends SnippetTest {
	@Test
	public void testSevenZipJBindingInitCheck() {
		/* BEGIN_OUTPUT(SevenZipJBindingInitCheck) */
		String expected = "7-Zip-JBinding library was initialized\n";
		/* END_OUTPUT */

		expected = expected.replace("\n", System.getProperty("line.separator"));

		beginSnippetTest();
		SevenZipJBindingInitCheck.main(null);
		String output = endSnippetTest();
		assertEquals(expected, output);
	}
}
