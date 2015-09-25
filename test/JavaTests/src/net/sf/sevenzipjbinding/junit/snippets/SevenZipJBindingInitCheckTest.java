package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link SevenZipJBindingInitCheck} snippet
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public class SevenZipJBindingInitCheckTest extends SnippetTest {
    @Test
    public void testSevenZipJBindingInitCheck() {
        /* BEGIN_OUTPUT(SevenZipJBindingInitCheck) */
        String expected = "7-Zip-JBinding library was initialized\n";
        /* END_OUTPUT */

        expected = expected.replace("\n", NEW_LINE);

        beginSnippetTest();
        SevenZipJBindingInitCheck.main(null);
        String output = endSnippetTest();
        assertEquals(expected, output);
    }
}
