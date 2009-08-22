package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link SevenZipJBindingInitCheck} snippet
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public class ExtractItemsTest extends SnippetTest {
	private String getExpectedOutput() {
		/* BEGIN_OUTPUT(ExtractItems) */
		String expected = "   Hash   | Filename\n";
		expected += "----------+---------\n";
		expected += " C1FD1029 | file1.txt\n";
		expected += " 8CB12E6A | file2.txt\n";
		expected += " E8EEC7F4 | folder/file in folder.txt\n";
		/* END_OUTPUT */

		expected = expected.replace("\n", System.getProperty("line.separator"));
		return expected;
	}

	@Test
	public void testExtractItemsSimple() {
		String expected = getExpectedOutput();

		beginSnippetTest();
		ExtractItemsSimple.main(new String[] { "testdata/snippets/simple.zip" });
		String output = endSnippetTest();
		assertEquals(expected, output);
	}

	@Test
	public void testExtractItemsStandard() {
		String expected = getExpectedOutput();

		beginSnippetTest();
		ExtractItemsStandard.main(new String[] { "testdata/snippets/simple.zip" });
		String output = endSnippetTest();
		System.out.println(output);
		assertEquals(expected, output);
	}

	//	@Test
	//	public void testListItemsInArchiveSimple() {
	//		String expected = getExpectedOutput();
	//
	//		beginSnippetTest();
	//		ListItemsStandard.main(new String[] { "testdata/snippets/simple.zip" });
	//		String output = endSnippetTest();
	//		System.out.println(output);
	//		assertEquals(expected, output);
	//	}
}
