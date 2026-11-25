package net.sf.sevenzipjbinding.junit.encoding;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;

/**
 * {@link PropID#PATH_BYTES}: the stored bytes of a Zip item name, so names in a legacy code page can be decoded by the
 * caller (issue #56, pull request #36 by Ethan Roseman). Fixtures: <code>gbk-names.zip</code> (a GBK name without the
 * UTF-8 flag, made by a Windows tool) and <code>jpn_file_names.zip</code> (UTF-8 bytes without the UTF-8 flag, from
 * pull request #36). The bundle runs with three different <code>file.encoding</code> settings; the bytes must not
 * depend on it.
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public class PathBytesTest extends JUnitNativeTestBase<VoidContext> {
    private static final String TEST_DATA_PATH = "testdata/encoding/";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    /** <code>中文文件.txt</code> in GBK, as stored in gbk-names.zip. */
    private static final byte[] GBK_NAME = { (byte) 0xD6, (byte) 0xD0, (byte) 0xCE, (byte) 0xC4, (byte) 0xCE,
            (byte) 0xC4, (byte) 0xBC, (byte) 0xFE, '.', 't', 'x', 't' };

    @Test
    public void gbkNameDecodesWithGbk() throws Exception {
        Map<String, byte[]> pathBytes = pathBytesByDecodedName(open("gbk-names.zip", ArchiveFormat.ZIP), "GBK");
        assertEquals(2, pathBytes.size());
        assertArrayEquals(GBK_NAME, pathBytes.get("中文文件.txt"));
        assertArrayEquals("plain.txt".getBytes(UTF_8), pathBytes.get("plain.txt"));
    }

    @Test
    public void utf8NameWithoutFlagDecodesWithUtf8() throws Exception {
        Map<String, byte[]> pathBytes = pathBytesByDecodedName(open("jpn_file_names.zip", ArchiveFormat.ZIP),
                "UTF-8");
        assertEquals(2, pathBytes.size());
        for (String name : new String[] { "123アアパート.txt",
                "テスト日本語.txt" }) {
            assertArrayEquals(name, name.getBytes(UTF_8), pathBytes.get(name));
        }
    }

    /**
     * The recipe from the javadoc: strict UTF-8 first, the legacy charset if that fails.
     */
    @Test
    public void strictUtf8ThenLegacyCharset() throws Exception {
        IInArchive inArchive = open("gbk-names.zip", ArchiveFormat.ZIP);
        Map<String, byte[]> decoded = new HashMap<String, byte[]>();
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            byte[] bytes = (byte[]) inArchive.getProperty(i, PropID.PATH_BYTES);
            decoded.put(decodeStrictUtf8OrElse(bytes, "GBK"), bytes);
        }
        assertTrue(decoded.containsKey("plain.txt"));
        assertTrue(decoded.containsKey("中文文件.txt"));
    }

    @Test
    public void stringPropertyIsOneCharPerByte() throws Exception {
        IInArchive inArchive = open("gbk-names.zip", ArchiveFormat.ZIP);
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            byte[] bytes = (byte[]) inArchive.getProperty(i, PropID.PATH_BYTES);
            assertEquals(new String(bytes, ISO_8859_1), inArchive.getStringProperty(i, PropID.PATH_BYTES));
        }
    }

    @Test
    public void formatWithoutRawNamesReturnsNull() throws Exception {
        IInArchive inArchive = open("unicode_file_names.7z", ArchiveFormat.SEVEN_ZIP);
        assertTrue(inArchive.getNumberOfItems() > 0);
        assertNull(inArchive.getProperty(0, PropID.PATH_BYTES));
        assertEquals("", inArchive.getStringProperty(0, PropID.PATH_BYTES)); // As for every absent property
    }

    private IInArchive open(String name, ArchiveFormat format) throws Exception {
        RandomAccessFileInStream inStream = new RandomAccessFileInStream(
                new RandomAccessFile(TEST_DATA_PATH + name, "r"));
        addCloseable(inStream);
        IInArchive inArchive = SevenZip.openInArchive(format, inStream);
        addCloseable(inArchive);
        return inArchive;
    }

    private static Map<String, byte[]> pathBytesByDecodedName(IInArchive inArchive, String charset)
            throws Exception {
        Map<String, byte[]> result = new HashMap<String, byte[]>();
        for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
            Object value = inArchive.getProperty(i, PropID.PATH_BYTES);
            assertNotNull("PATH_BYTES of item " + i, value);
            assertTrue(value.getClass().getName(), value instanceof byte[]);
            byte[] bytes = (byte[]) value;
            result.put(new String(bytes, charset), bytes);
        }
        return result;
    }

    private static String decodeStrictUtf8OrElse(byte[] bytes, String legacyCharset) throws Exception {
        CharsetDecoder decoder = UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (java.nio.charset.CharacterCodingException e) {
            return new String(bytes, legacyCharset);
        }
    }
}
