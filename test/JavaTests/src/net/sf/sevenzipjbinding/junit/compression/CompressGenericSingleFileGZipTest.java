package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression and extraction of a single file using generic callback.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressGenericSingleFileGZipTest extends CompressGenericSingleFileAbstractTest {

    public CompressGenericSingleFileGZipTest(int size, int entropy) {
        super(size, entropy);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.GZIP;
    }

}
