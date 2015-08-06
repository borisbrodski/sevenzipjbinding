package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutItemAllFormats;

/**
 * Tests compression, update and extraction of a single file using non-generic callback with 7z.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public class UpdateSingleFileNonGeneric7zTest extends UpdateSingleFileNonGenericAbstractTest<IOutItem7z> {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

    @Override
    protected void copyFromDelegate(IOutItem7z outItem, IOutItemAllFormats delegateOutItem) {
        copyFromDelegate7z(outItem, delegateOutItem);
    }

}
