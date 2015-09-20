package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Tests compression, update and extraction of a single file using generic callback with BZip2.
 * 
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateSingleFileGenericBZip2Test extends UpdateSingleFileGenericAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.BZIP2;
    }

}
