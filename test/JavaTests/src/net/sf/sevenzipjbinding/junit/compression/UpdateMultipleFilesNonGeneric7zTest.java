package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItemAllFormats;

/**
 * Tests compression, update and extraction of multiple files using non-generic callback with 7z.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateMultipleFilesNonGeneric7zTest extends UpdateMultipleFilesNonGenericAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

    @Override
    protected void copyFromDelegate(IOutItemAllFormats outItem, IOutItemAllFormats delegateOutItem) {
        copyFromDelegate7z(outItem, delegateOutItem);
    }

}
