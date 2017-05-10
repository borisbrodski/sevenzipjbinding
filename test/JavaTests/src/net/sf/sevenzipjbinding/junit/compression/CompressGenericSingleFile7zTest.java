package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression and extraction of a single file using generic callback.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressGenericSingleFile7zTest extends CompressGenericSingleFileAbstractTest {

    public CompressGenericSingleFile7zTest(int size, int entropy) {
        super(size, entropy);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

}
