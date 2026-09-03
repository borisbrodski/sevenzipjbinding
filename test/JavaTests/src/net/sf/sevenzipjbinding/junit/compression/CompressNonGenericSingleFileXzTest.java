package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemXz;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Tests compression and extraction of a single file using non-generic callback with Xz.
 *
 * @author Boris Brodski
 * @since 23.01
 */
public class CompressNonGenericSingleFileXzTest extends CompressNonGenericSingleFileAbstractTest<IOutItemXz> {
    private static class SingleFileCreateArchiveCallbackXz extends SingleFileCreateArchiveCallback<IOutItemXz> {
        protected SingleFileCreateArchiveCallbackXz(TestContext testContext) {
            super(testContext);
        }

        public IOutItemXz getItemInformation(int index, OutItemFactory<IOutItemXz> outItemFactory)
                throws SevenZipException {
            IOutItemXz outItem = outItemFactory.createOutItem();

            setBaseProperties(outItem);
            setPropertiesForXz(outItem, testContext);

            return outItem;
        }
    }

    public CompressNonGenericSingleFileXzTest(int size, int entropy) {
        super(size, entropy);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.XZ;
    }

    @Override
    protected SingleFileCreateArchiveCallback<IOutItemXz> getSingleFileCreateArchiveCallback() {
        return new SingleFileCreateArchiveCallbackXz(getTestContext());
    }

    @Override
    protected IOutCreateArchive<IOutItemXz> openOutArchive() throws SevenZipException {
        return SevenZip.openOutArchiveXz();
    }

}
