package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression, update and extraction of a single file using generic callback with Zip.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateSingleFileGenericZipTest extends UpdateSingleFileGenericAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

}
