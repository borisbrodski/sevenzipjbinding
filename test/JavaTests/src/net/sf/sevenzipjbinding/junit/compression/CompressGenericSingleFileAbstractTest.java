package net.sf.sevenzipjbinding.junit.compression;

import java.util.Date;

import net.sf.sevenzipjbinding.IOutCreateCallbackGeneric;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;

/**
 * Tests compression and extraction of a single file using generic interface.
 *
 * @author Boris Brodski
 * @version 9.13-2.0
 */
public abstract class CompressGenericSingleFileAbstractTest extends CompressSingleFileAbstractTest {
    public class GenericSingleFileCreateArchiveCallback implements IOutCreateCallbackGeneric {
        private RandomContext randomContext;

        protected GenericSingleFileCreateArchiveCallback(RandomContext randomContext) {
            this.randomContext = randomContext;
        }

        public Object getProperty(int index, PropID propID) {
            switch (propID) {
            case PATH:
                return "content";

            case IS_FOLDER:
            case IS_ANTI:
                return Boolean.FALSE;

            case SIZE:
                return Long.valueOf(randomContext.getSize());
            case LAST_MODIFICATION_TIME:
                return new Date();
            }
            return null;
        }

        public ISequentialInStream getStream(int index) {
            return randomContext;
        }

        public void setOperationResult(boolean operationResultOk) {

        }

        public void setTotal(long total) throws SevenZipException {
        }

        public void setCompleted(long completeValue) throws SevenZipException {

        }
    }

    //    private static final int MINIMUM_STREAM_LENGTH = 32769;

    //    private long doTest(final int dataSize, final int entropy, boolean multithreaded) throws Exception {
    //        long result = 0;
    //        if (multithreaded) {
    //            runMultithreaded(new RunnableThrowsException() {
    //                public void run() throws Exception {
    //                    doTest(dataSize, entropy, false);
    //                }
    //            }, null);
    //        } else {
    //            int repeatCount = dataSize == 0 ? SINGLE_TEST_REPEAT_COUNT * 100
    //                    : 1 + (SINGLE_TEST_REPEAT_COUNT * 100 / dataSize);
    //
    //            for (int i = 0; i < repeatCount; i++) {
    //                result = doTest(dataSize, entropy);
    //            }
    //        }
    //        return result;
    //    }
    //
    //    private long doTest(int dataSize, int entropy) throws Exception {
    //        System.out.println(getArchiveFormat());
    //        RandomContext randomContext = new RandomContext(dataSize, entropy);
    //        int maxStreamSize = dataSize + MINIMUM_STREAM_LENGTH;
    //        ByteArrayStream outputByteArrayStream = new ByteArrayStream(maxStreamSize);
    //
    //        IOutCreateArchive<IOutItemCallback> outArchive = SevenZip.openOutArchive(getArchiveFormat());
    //
    //        try {
    //            // TODO Add callbackTesterCreateArchive
    //            outArchive.createArchive(outputByteArrayStream, 1,
    //                    new GenericSingleFileCreateArchiveCallback(randomContext));
    //        } finally {
    //            outArchive.close();
    //        }
    //        System.out.println("Length: " + dataSize + ", entropy: " + entropy + ": compressed size: "
    //                + outputByteArrayStream.getSize());
    //
    //        verifyCompressedArchive(randomContext, outputByteArrayStream, );
    //
    //        return outputByteArrayStream.getSize();
    //    }
}
