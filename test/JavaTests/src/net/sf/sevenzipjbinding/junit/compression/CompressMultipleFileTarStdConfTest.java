package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ISevenZipOutArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.Assert;
import org.junit.Test;

public class CompressMultipleFileTarStdConfTest extends CompressMultipleFileAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.TAR;
    }

    @Test
    public void setPropertyTest() throws Exception {
        ISevenZipOutArchive outArchive = SevenZip.openOutArchive(ArchiveFormat.TAR);
        try {
            outArchive.setLevel(0);
            Assert.fail("SevenZipException expected. TAR archive doesn't support properties.");
        } catch (SevenZipException exception) {
            // Everything all right
        }
    }

    @Test
    public void setPropertyMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                setPropertyTest();
            }
        }, null);
    }
}
