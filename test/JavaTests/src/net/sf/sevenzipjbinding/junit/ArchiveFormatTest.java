package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;

import org.junit.Test;

public class ArchiveFormatTest {
    @Test
    public void testForOutArchive() {
        for (ArchiveFormat archiveFormat : ArchiveFormat.values()) {
            try {
                System.out.print("Testing " + archiveFormat + ": ");
                System.out.flush();
                SevenZip.openOutArchive(archiveFormat);
                System.out.println("Ok");
            } catch (Exception e) {
                System.out.println("Exception");
            }
        }
    }
}
