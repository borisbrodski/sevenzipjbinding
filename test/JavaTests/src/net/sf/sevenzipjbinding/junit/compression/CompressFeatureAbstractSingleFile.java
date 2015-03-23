package net.sf.sevenzipjbinding.junit.compression;

import java.io.Closeable;
import java.util.Date;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallbackGeneric;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.tools.AssertOutputStream;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Assert;

/**
 * Tests setting compression level.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class CompressFeatureAbstractSingleFile extends CompressAbstractTest {

    public class FeatureSingleFileCreateArchiveCallback implements IOutCreateCallbackGeneric {

        private RandomContext randomContext;

        public FeatureSingleFileCreateArchiveCallback(RandomContext randomContext) {
            this.randomContext = randomContext;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return randomContext;
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {

        }

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public Object getProperty(int index, PropID propID) {
            switch (propID) {
            case PATH:
                return "content";

            case SIZE:
                return randomContext.getSize();

            case IS_FOLDER:
                return false;

            case LAST_MODIFICATION_TIME:
                return new Date();
            }
            return null;
        }
    }

    protected IOutCreateArchive<IOutItemCallback> createArchive() throws Exception {
        IOutCreateArchive<IOutItemCallback> outArchive = SevenZip.openOutArchive(getArchiveFormat());
        addCloseable(outArchive);
        return outArchive;
    }

    protected void closeArchive(Closeable outArchive) throws Exception {
        removeCloseable(outArchive);
        outArchive.close();
    }

    protected void verifySingleFileArchive(RandomContext randomContext, ByteArrayStream outputByteArrayStream)
            throws Exception {
        outputByteArrayStream.rewind();
        randomContext.rewind();
        IInArchive inArchive = SevenZip.openInArchive(getArchiveFormat(), outputByteArrayStream);
        addCloseable(inArchive);

        Assert.assertEquals(getArchiveFormat(), inArchive.getArchiveFormat());
        inArchive.extractSlow(0, new AssertOutputStream(randomContext));
        closeArchive(inArchive);
    }

    protected void testSingleOrMultithreaded(boolean multithreaded, final RunnableThrowsException test) throws Exception {
        if (multithreaded) {
            runMultithreaded(new RunnableThrowsException() {
                public void run() throws Exception {
                    testSingleOrMultithreaded(false, test);
                }
            }, null);
        } else {
    
            for (int i = 0; i < SINGLE_TEST_REPEAT_COUNT; i++) {
                test.run();
            }
        }
    }
}
