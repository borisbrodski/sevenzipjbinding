package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Tests compression and extraction of a single file using non-generic callback with 7z.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressNonGenericSingleFile7zTest extends CompressNonGenericSingleFileAbstractTest<IOutItem7z> {

    private class SingleFileCreateArchiveCallback7z extends SingleFileCreateArchiveCallback {
        public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
                throws SevenZipException {
            IOutItem7z outItem = outItemFactory.createOutItem();

            setBaseProperties(outItem);
            setPropertiesFor7z(outItem, getTestContext());

            return outItem;
        }
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

    @Override
    protected SingleFileCreateArchiveCallback getSingleFileCreateArchiveCallback() {
        return new SingleFileCreateArchiveCallback7z();
    }

    @Override
    protected IOutCreateArchive<IOutItem7z> openOutArchive() throws SevenZipException {
        return SevenZip.openOutArchive7z();
    }
}
