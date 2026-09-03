package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItemAllFormats;

/**
 * Tests compression, update and extraction of a single file using non-generic callback with Xz.
 *
 * @author Boris Brodski
 * @since 23.01
 */
public class UpdateSingleFileNonGenericXzTest extends UpdateSingleFileNonGenericAbstractTest {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.XZ;
    }

    @Override
    protected void copyFromDelegate(IOutItemAllFormats outItem, IOutItemAllFormats delegateOutItem) {
    }
}
