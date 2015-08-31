package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemGZip;

/**
 * Tests compression, update and extraction of a single file using non-generic callback with GZip.
 *
 * @author Boris Brodski
 * @version 9.20-2.00
 */
public class UpdateSingleFileNonGenericGZipTest extends UpdateSingleFileNonGenericAbstractTest<IOutItemGZip> {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.GZIP;
    }

    @Override
    protected void copyFromDelegate(IOutItemGZip outItem, IOutItemAllFormats delegateOutItem) {
        outItem.setPropertyLastModificationTime(delegateOutItem.getPropertyLastModificationTime());
        outItem.setPropertyPath(delegateOutItem.getPropertyPath());
    }

}
