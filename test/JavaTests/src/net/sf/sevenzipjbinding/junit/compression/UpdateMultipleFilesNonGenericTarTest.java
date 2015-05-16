package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression, update and extraction of multiple files using non-generic callback with Tar.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public class UpdateMultipleFilesNonGenericTarTest extends UpdateMultipleFilesNonGenericAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.TAR;
    }

}
