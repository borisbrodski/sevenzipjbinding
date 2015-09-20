package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemBZip2;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Tests compression and extraction of a single file using non-generic callback with BZip2.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressNonGenericSingleFileBZip2Test extends CompressNonGenericSingleFileAbstractTest<IOutItemBZip2> {
    private class SingleFileCreateArchiveCallbackBZip2 extends SingleFileCreateArchiveCallback {
        public IOutItemBZip2 getItemInformation(int index, OutItemFactory<IOutItemBZip2> outItemFactory)
                throws SevenZipException {
            IOutItemBZip2 outItem = outItemFactory.createOutItem();

            setBaseProperties(outItem);
            setPropertiesForBZip2(outItem, getTestContext());

            return outItem;
        }
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.BZIP2;
    }

    @Override
    protected SingleFileCreateArchiveCallback getSingleFileCreateArchiveCallback() {
        return new SingleFileCreateArchiveCallbackBZip2();
    }

    @Override
    protected IOutCreateArchive<IOutItemBZip2> openOutArchive() throws SevenZipException {
        return SevenZip.openOutArchiveBZip2();
    }

}
