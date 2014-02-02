package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import org.junit.Test;

public class WrongCRCGetterInSimpleInterface {

    @Test
    public void test() throws Throwable {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;

        Throwable throwable = null;
        try {
            randomAccessFile = new RandomAccessFile("testdata/bug/wrong_crc_getter_in_simple_interface.7z", "r");
            inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(randomAccessFile));
            ISimpleInArchiveItem[] archiveItems = inArchive.getSimpleInterface().getArchiveItems();
            assertEquals(2, archiveItems.length);
            assertTrue(archiveItems[0].getCRC().intValue() != archiveItems[1].getCRC().intValue());
        } catch (Throwable e) {
            throwable = e;
        }
        if (inArchive != null) {
            try {
                inArchive.close();
            } catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                }
            }
        }
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                }
            }
        }
        if (throwable != null) {
            throw throwable;
        }
    }
}
