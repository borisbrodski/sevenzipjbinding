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
    private static class SingleFileCreateArchiveCallbackBZip2 extends SingleFileCreateArchiveCallback<IOutItemBZip2> {
        protected SingleFileCreateArchiveCallbackBZip2(TestContext testContext) {
            super(testContext);
        }

        public IOutItemBZip2 getItemInformation(int index, OutItemFactory<IOutItemBZip2> outItemFactory)
                throws SevenZipException {
            IOutItemBZip2 outItem = outItemFactory.createOutItem();

            setBaseProperties(outItem);
            setPropertiesForBZip2(outItem, testContext);

            return outItem;
        }
    }

    public CompressNonGenericSingleFileBZip2Test(int size, int entropy) {
        super(size, entropy);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.BZIP2;
    }

    @Override
    protected SingleFileCreateArchiveCallback<IOutItemBZip2> getSingleFileCreateArchiveCallback() {
        return new SingleFileCreateArchiveCallbackBZip2(getTestContext());
    }

    @Override
    protected IOutCreateArchive<IOutItemBZip2> openOutArchive() throws SevenZipException {
        return SevenZip.openOutArchiveBZip2();
    }

}
