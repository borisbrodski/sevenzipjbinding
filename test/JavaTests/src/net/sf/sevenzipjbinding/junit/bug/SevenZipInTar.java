package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.assertEquals;

import java.io.RandomAccessFile;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class SevenZipInTar {

    @Test
    public void test() throws Throwable {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;

        Throwable throwable = null;
        try {
            randomAccessFile = new RandomAccessFile("testdata/bug/TarArchiveWith7zInside.tar", "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            assertEquals(ArchiveFormat.TAR, inArchive.getArchiveFormat());
            //            ISimpleInArchiveItem[] archiveItems = inArchive.getSimpleInterface().getArchiveItems();
            //            assertEquals(2, archiveItems.length);
            //            assertTrue(archiveItems[0].getCRC().intValue() != archiveItems[1].getCRC().intValue());
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
