package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemZip;

/**
 * Tests compression, update and extraction of multiple files using non-generic callback with Zip.
 *
 * @author Boris Brodski
 * @version 9.20-2.00
 */
public class UpdateMultipleFilesNonGenericZipTest extends UpdateMultipleFilesNonGenericAbstractTest<IOutItemZip> {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

    @Override
    protected void copyFromDelegate(IOutItemZip outItem, IOutItemAllFormats delegateOutItem) {
        copyFromDelegateZip(outItem, delegateOutItem);
    }

}
