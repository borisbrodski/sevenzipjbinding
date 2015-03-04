package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression and extraction of a single file using non-generic callback with Zip.
 * 
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public class CompressNonGenericSingleFileZipTest extends CompressNonGenericSingleFileAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }
}
