package net.sf.sevenzipjbinding.junit.compression;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression, update and extraction of multiple files using generic callback with 7z.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateMultipleFilesGeneric7zPassNullTest extends UpdateMultipleFilesGenericAbstractTest {

    @Before
    public void configureEncryption() {
        setUseEncryption(true);
        setUseEncryptionNoPassword(true);
        setUseHeaderEncryption(false);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

}
