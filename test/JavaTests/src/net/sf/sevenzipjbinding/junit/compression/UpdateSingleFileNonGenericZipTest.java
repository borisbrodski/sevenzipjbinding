package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItemAllFormats;

/**
 * Tests compression, update and extraction of a single file using non-generic callback with Zip.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateSingleFileNonGenericZipTest extends UpdateSingleFileNonGenericAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

    @Override
    protected void copyFromDelegate(IOutItemAllFormats outItem, IOutItemAllFormats delegateOutItem) {
        copyFromDelegateZip(outItem, delegateOutItem);
    }
}
