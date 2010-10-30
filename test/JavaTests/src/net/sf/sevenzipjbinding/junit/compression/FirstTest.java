package net.sf.sevenzipjbinding.junit.compression;

import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IArchiveUpdateCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.InputStreamSequentialInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Ignore;
import org.junit.Test;

public class FirstTest extends JUnitNativeTestBase {
    @Test
    @Ignore
    // TODO Remove or bring to work
    public void test() throws Exception {
        IOutArchive outArchive = SevenZip.openOutArchive(ArchiveFormat.SEVEN_ZIP);
        RandomAccessFileOutStream outputStream = new RandomAccessFileOutStream(new RandomAccessFile("test.7z", "rw"));
        try {
            outArchive.updateItems(outputStream, 100, new TestUpdateCallback());
        } finally {
            outputStream.close();
        }
    }

    private class TestUpdateCallback implements IArchiveUpdateCallback {

        /**
         * ${@inheritDoc}
         */
        public int getOldArchiveItemIndex(int index) {
            System.out.println("getOldArchiveItemIndex");
            return -1;
        }

        /**
         * ${@inheritDoc}
         */
        public Object getProperty(int index, PropID propID) {
            System.out.println("Get property " + propID + " for index " + index);
            System.out.flush();
            switch (propID) {
            case PATH:
                return "file" + index;
            case IS_FOLDER:
            case IS_ANTI:
                return Boolean.FALSE;
            case SIZE:
                Long valueOf = Long.valueOf(("Index=" + index).length());
                System.out.println("Returning: " + valueOf);
                System.out.flush();
                return valueOf;
            }
            return null;
        }

        /**
         * ${@inheritDoc}
         */
        public ISequentialInStream getStream(int index) {
            System.out.println("getStream");
            return new InputStreamSequentialInStream(new ByteArrayInputStream(("Index=" + index).getBytes()));
        }

        /**
         * ${@inheritDoc}
         */
        public boolean isNewData(int index) {
            System.out.println("isNewData");
            return false;
        }

        /**
         * ${@inheritDoc}
         */
        public boolean isNewProperties(int index) {
            System.out.println("isNewProperty");
            return false;
        }

        /**
         * ${@inheritDoc}
         */
        public void setOperationResult(boolean operationResultOk) {
            System.out.println("Operation result: " + operationResultOk);
        }

        /**
         * ${@inheritDoc}
         */
        public void setCompleted(long completeValue) throws SevenZipException {
            System.out.println("Completed: " + completeValue);
        }

        /**
         * ${@inheritDoc}
         */
        public void setTotal(long total) throws SevenZipException {
            System.out.println("Total: " + total);
        }

    }
}
