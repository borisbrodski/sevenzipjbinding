package net.sf.sevenzipjbinding.junit.compression;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression, update and extraction of multiple files using generic callback with Zip.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateMultipleFilesGenericZipPassTest extends UpdateMultipleFilesGenericAbstractTest {

    @Before
    public void configureEncryption() {
        setUseEncryption(true);
        setUseEncryptionNoPassword(false);
        setUseHeaderEncryption(false);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

}
