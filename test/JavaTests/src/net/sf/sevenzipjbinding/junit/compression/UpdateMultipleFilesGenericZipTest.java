package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression, update and extraction of multiple files using generic callback with Zip.
 * 
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public class UpdateMultipleFilesGenericZipTest extends UpdateMultipleFilesGenericAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

}
