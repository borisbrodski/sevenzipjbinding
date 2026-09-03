package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression and extraction of a single file using generic callback.
 *
 * @author Boris Brodski
 * @since 23.01
 */
public class CompressGenericSingleFileXzTest extends CompressGenericSingleFileAbstractTest {

    public CompressGenericSingleFileXzTest(int size, int entropy) {
        super(size, entropy);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.XZ;
    }

}
