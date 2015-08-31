package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemBZip2;

/**
 * Tests compression, update and extraction of a single file using non-generic callback with BZip2.
 *
 * @author Boris Brodski
 * @version 9.20-2.00
 */
public class UpdateSingleFileNonGenericBZip2Test extends UpdateSingleFileNonGenericAbstractTest<IOutItemBZip2> {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.BZIP2;
    }

    @Override
    protected void copyFromDelegate(IOutItemBZip2 outItem, IOutItemAllFormats delegateOutItem) {
    }
}
