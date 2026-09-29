package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.NFileTimeType;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Java property ids and file time types must mirror the 7-Zip engine, so every property an archive reports reaches
 * Java under its own name.
 */
public class PropertyIdsTest extends JUnitNativeTestBase<VoidContext> {
    private static final String OWNER_AND_DEVICE_TAR = "testdata/misc/properties/owner-and-device.tar";

    @Test
    public void propertyIdsMatchEngineIndexes() {
        // kpidArcFileName ... kpidDevMinor, kpid_NUM_DEFINED (7-Zip 26.03 PropID.h)
        assertEquals(96, PropID.ARC_FILE_NAME.getPropIDIndex());
        assertEquals(97, PropID.IS_HASH.getPropIDIndex());
        assertEquals(98, PropID.CHANGE_TIME.getPropIDIndex());
        assertEquals(99, PropID.USER_ID.getPropIDIndex());
        assertEquals(100, PropID.GROUP_ID.getPropIDIndex());
        assertEquals(101, PropID.DEVICE_MAJOR.getPropIDIndex());
        assertEquals(102, PropID.DEVICE_MINOR.getPropIDIndex());
        assertEquals(103, PropID.DEV_MAJOR.getPropIDIndex());
        assertEquals(104, PropID.DEV_MINOR.getPropIDIndex());
        assertEquals(105, PropID.NUM_DEFINED.getPropIDIndex());
        assertEquals(0x10000, PropID.USER_DEFINED.getPropIDIndex());
    }

    @Test
    public void everyEngineIndexResolvesToItsOwnConstant() {
        for (int index = 0; index < PropID.NUM_DEFINED.getPropIDIndex(); index++) {
            PropID propID = PropID.getPropIDByIndex(index);
            assertEquals("engine property id " + index, index, propID.getPropIDIndex());
            assertNotEquals(PropID.UNKNOWN, propID);
        }
        assertSame(PropID.UNKNOWN, PropID.getPropIDByIndex(PropID.NUM_DEFINED.getPropIDIndex() + 1000));
    }

    @Test
    public void fileTimeTypesMatchEngineOrder() {
        // NFileTimeType::kWindows, kUnix, kDOS, k1ns
        assertEquals(0, NFileTimeType.WINDOWS.ordinal());
        assertEquals(1, NFileTimeType.UNIX.ordinal());
        assertEquals(2, NFileTimeType.DOS.ordinal());
        assertEquals(3, NFileTimeType.NANOSECONDS.ordinal());
    }

    @Test
    public void tarOwnerAndDeviceNumbersAreReported() throws Exception {
        RandomAccessFileInStream stream = new RandomAccessFileInStream(new RandomAccessFile(OWNER_AND_DEVICE_TAR, "r"));
        addCloseable(stream);
        IInArchive archive = SevenZip.openInArchive(ArchiveFormat.TAR, stream);
        addCloseable(archive);

        assertEquals(2, archive.getNumberOfItems());
        assertEquals("file.txt", archive.getProperty(0, PropID.PATH));
        assertEquals(Integer.valueOf(1234), archive.getProperty(0, PropID.USER_ID));
        assertEquals(Integer.valueOf(5678), archive.getProperty(0, PropID.GROUP_ID));
        assertEquals("alice", archive.getProperty(0, PropID.USER));
        assertEquals("staff", archive.getProperty(0, PropID.GROUP));

        assertEquals("null", archive.getProperty(1, PropID.PATH));
        assertEquals(Integer.valueOf(1), archive.getProperty(1, PropID.DEVICE_MAJOR));
        assertEquals(Integer.valueOf(3), archive.getProperty(1, PropID.DEVICE_MINOR));

        Set<PropID> reported = EnumSet.noneOf(PropID.class);
        for (int i = 0; i < archive.getNumberOfProperties(); i++) {
            reported.add(archive.getPropertyInfo(i).propID);
        }
        assertTrue(reported.toString(), reported.containsAll(
                EnumSet.of(PropID.USER_ID, PropID.GROUP_ID, PropID.DEVICE_MAJOR, PropID.DEVICE_MINOR)));
        assertTrue(reported.toString(), !reported.contains(PropID.UNKNOWN));
        assertTrue(reported.toString(), !reported.contains(PropID.NUM_DEFINED));
    }

    @Test
    public void zipTimeTypeIsConvertedToEnum() throws Exception {
        ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(zipBytes)) {
            ZipEntry entry = new ZipEntry("a.txt");
            // An explicit modification time makes Java write the Unix "extended timestamp" extra field.
            entry.setLastModifiedTime(FileTime.fromMillis(1700000000000L));
            zip.putNextEntry(entry);
            zip.write('a');
            zip.closeEntry();
        }
        ByteArrayStream stream = new ByteArrayStream(zipBytes.toByteArray(), false);
        IInArchive archive = SevenZip.openInArchive(ArchiveFormat.ZIP, stream);
        addCloseable(archive);

        assertEquals(NFileTimeType.UNIX, archive.getProperty(0, PropID.TIME_TYPE));
    }
}
