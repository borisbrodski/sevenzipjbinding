package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemTar;

/**
 * Tests compression, update and extraction of multiple files using non-generic callback with Tar.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public class UpdateMultipleFilesNonGenericTarTest extends UpdateMultipleFilesNonGenericAbstractTest<IOutItemTar> {

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.TAR;
    }

    @Override
    protected void copyFromDelegate(IOutItemTar outItem, IOutItemAllFormats delegateOutItem) {
        copyFromDelegateTar(outItem, delegateOutItem);
    }

}
