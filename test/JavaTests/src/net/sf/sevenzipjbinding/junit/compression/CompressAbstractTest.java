package net.sf.sevenzipjbinding.junit.compression;

import java.util.Date;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveUpdateCallback;
import net.sf.sevenzipjbinding.ISeekableStream;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.tools.AssertOutputStream;
import net.sf.sevenzipjbinding.junit.tools.RandomContext;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import org.junit.Assert;

public abstract class CompressAbstractTest extends JUnitNativeTestBase {
    public class SingleFileArchiveUpdateCallback implements IArchiveUpdateCallback {
        private RandomContext randomContext;

        protected SingleFileArchiveUpdateCallback(RandomContext randomContext) {
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

        public boolean isNewData(int index) {
            return true;
        }

        public boolean isNewProperties(int index) {
            return true;
        }

        public int getOldArchiveItemIndex(int index) {
            return 0;
        }

    }

    protected abstract ArchiveFormat getArchiveFormat();

    protected void verifyCompressedArchive(RandomContext randomContext, ByteArrayStream outputByteArrayStream)
            throws SevenZipException {
        randomContext.seek(0, ISeekableStream.SEEK_SET);
        outputByteArrayStream.rewind();

        IInArchive inArchive = null;
        boolean successfull = false;
        try {
            inArchive = SevenZip.openInArchive(null, outputByteArrayStream);
            Assert.assertEquals(getArchiveFormat(), inArchive.getArchiveFormat());
            inArchive.extractSlow(0, new AssertOutputStream(randomContext));
            successfull = true;
        } finally {
            try {
                if (inArchive != null) {
                    inArchive.close();
                }
            } catch (Throwable throwable) {
                if (successfull) {
                    throw new RuntimeException("Error closing InArchive", throwable);
                }
            }
        }
    }
}
