package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Tests compression and extraction of a single file using non-generic callback with GZip.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class CompressNonGenericSingleFileGZipTest extends CompressNonGenericSingleFileAbstractTest<IOutItemGZip> {
    private static class SingleFileCreateArchiveCallbackGZip extends SingleFileCreateArchiveCallback<IOutItemGZip> {
        protected SingleFileCreateArchiveCallbackGZip(TestContext testContext) {
            super(testContext);
        }

        public IOutItemGZip getItemInformation(int index, OutItemFactory<IOutItemGZip> outItemFactory)
                throws SevenZipException {
            IOutItemGZip outItem = outItemFactory.createOutItem();

            setBaseProperties(outItem);
            setPropertiesForGZip(outItem, testContext);

            return outItem;
        }
    }

    public CompressNonGenericSingleFileGZipTest(int size, int entropy) {
        super(size, entropy);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.GZIP;
    }

    @Override
    protected SingleFileCreateArchiveCallback<IOutItemGZip> getSingleFileCreateArchiveCallback() {
        return new SingleFileCreateArchiveCallbackGZip(getTestContext());
    }

    @Override
    protected IOutCreateArchive<IOutItemGZip> openOutArchive() throws SevenZipException {
        return SevenZip.openOutArchiveGZip();
    }

}
