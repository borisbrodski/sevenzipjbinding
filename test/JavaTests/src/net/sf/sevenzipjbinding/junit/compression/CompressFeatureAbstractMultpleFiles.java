package net.sf.sevenzipjbinding.junit.compression;

import java.io.Closeable;

import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Tests setting compression level.
 *
 * @param <C>
 *            TestContext class
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class CompressFeatureAbstractMultpleFiles<C extends AbstractTestContext>
        extends CompressAbstractTest<C> {

    protected IOutCreateArchive<IOutItemAllFormats> createArchive() throws Exception {
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(getArchiveFormat());
        addCloseable(outArchive);
        return outArchive;
    }

    protected void closeArchive(Closeable outArchive) throws Exception {
        removeCloseable(outArchive);
        outArchive.close();
    }

    protected void verifySingleFileArchive(ByteArrayStream outputByteArrayStream)
            throws Exception {
    }

}
