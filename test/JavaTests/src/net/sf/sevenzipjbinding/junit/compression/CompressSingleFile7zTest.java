package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression and extraction of a single file with 7z.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public class CompressSingleFile7zTest extends CompressSingleFileAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }
}
