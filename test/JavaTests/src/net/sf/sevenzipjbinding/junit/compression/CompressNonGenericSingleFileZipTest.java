package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Tests compression and extraction of a single file using non-generic callback with Zip.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressNonGenericSingleFileZipTest extends CompressNonGenericSingleFileAbstractTest<IOutItemZip> {
    private class SingleFileCreateArchiveCallbackZip extends SingleFileCreateArchiveCallback {
        public IOutItemZip getItemInformation(int index, OutItemFactory<IOutItemZip> outItemFactory)
                throws SevenZipException {
            IOutItemZip outItem = outItemFactory.createOutItem();

            setBaseProperties(outItem);
            setPropertiesForZip(outItem, getTestContext());

            return outItem;
        }
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

    @Override
    protected SingleFileCreateArchiveCallback getSingleFileCreateArchiveCallback() {
        return new SingleFileCreateArchiveCallbackZip();
    }

    @Override
    protected IOutCreateArchive<IOutItemZip> openOutArchive() throws SevenZipException {
        return SevenZip.openOutArchiveZip();
    }

}
