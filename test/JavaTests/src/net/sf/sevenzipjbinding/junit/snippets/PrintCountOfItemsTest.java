package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link SevenZipJBindingInitCheck} snippet
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public class PrintCountOfItemsTest extends SnippetTest {
	@Test
	public void testSevenZipJBindingInitCheck() {
		/* BEGIN_OUTPUT(PrintCountOfItems) */
		String expected = "Count of items in archive: 3" + System.getProperty("line.separator");
		/* END_OUTPUT */

		beginSnippetTest();
		PrintCountOfItems.main(new String[] { "testdata/snippets/simple.zip" });
		String output = endSnippetTest();
		assertEquals(expected, output);
	}
}
