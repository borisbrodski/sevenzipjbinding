package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression and extraction of a single file with GZip.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public class CompressSingleFileBZip2Test extends CompressSingleFileAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.BZIP2;
    }
}
