package net.sf.sevenzipjbinding.junit.compression;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression and extraction of a single file using generic callback.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressGenericSingleFile7zPassTest extends CompressGenericSingleFileAbstractTest {

    public CompressGenericSingleFile7zPassTest(int size, int entropy) {
        super(size, entropy);
    }

    @Before
    public void configureEncryption() {
        setUseEncryption(true);
        setUseEncryptionNoPassword(false);
        setUseHeaderEncryption(false);
    }
    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

}
