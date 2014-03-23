package atest;

import java.util.Date;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackZip;
import net.sf.sevenzipjbinding.IOutUpdateCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;

public class MyTest {
    static IOutItemCallback iOutItemCallback = new IOutItemCallback() {

        public boolean isNtfsTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isDir(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public Date getModificationTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getLastAccessTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getCreationTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Integer getAttributes(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Integer getPosixAttributes(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isAnti(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public long getSize(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return 0;
        }

        // TODO REMOVE ME
        public String getPath1(int index) throws SevenZipException {
            return null;
        }

        public String getPath(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }
    };

    private IOutCreateCallback<IOutItemCallback> outCreateCallback = new IOutCreateCallback<IOutItemCallback>() {

        public void setTotal(long total) throws SevenZipException {
            // TODO Auto-generated method stub

        }

        public void setCompleted(long complete) throws SevenZipException {
            // TODO Auto-generated method stub

        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
            // TODO Auto-generated method stub

        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public IOutItemCallback getOutItemCallback() throws SevenZipException {
            return iOutItemCallback;
        }
    };

    static IOutItemCallbackZip outItemCallbackZip = new IOutItemCallbackZip() {

        public boolean isNtfsTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isDir(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public long getSize(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getPath(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getModificationTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getLastAccessTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getCreationTime(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Integer getAttributes(int index) throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        // TODO REMOVE ME
        public String getPath1(int index) throws SevenZipException {
            return null;
        }
    };
    static IOutCreateCallback<IOutItemCallbackZip> outCreateCallbackZip = new IOutCreateCallback<IOutItemCallbackZip>() {

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {

        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return null;
        }

        public IOutItemCallbackZip getOutItemCallback() throws SevenZipException {
            return outItemCallbackZip;
        }
    };
    static IOutCreateCallback<IOutItemCallbackZip> outCreateCallbackZipUniversalItemCallback = new IOutCreateCallback<IOutItemCallbackZip>() {

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {

        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return null;
        }

        public IOutItemCallbackZip getOutItemCallback() throws SevenZipException {
            return iOutItemCallback;
        }
    };

    private void test2() throws SevenZipException {

        IOutCreateArchive<IOutItemCallback> openOutArchive = SevenZip.openOutArchive(ArchiveFormat.ZIP);
        openOutArchive.createArchive(null, 0, outCreateCallback);

        IOutCreateArchive<IOutItemCallbackZip> openOutArchive2 = SevenZip.openOutArchiveZip();
        openOutArchive2.createArchive(null, 0, outCreateCallback);
        openOutArchive2.createArchive(null, 0, outCreateCallbackZip);

        IOutCreateArchive<IOutItemCallback> a = null;
        IOutCreateArchive<IOutItemCallbackZip> b = null;
        IOutCreateArchive<? super IOutItemCallback> concrete = b;

        IOutUpdateCallback<IOutItemCallback> universalCallback = null;
        concrete.createArchive(null, 0, universalCallback);

    }

    public static void test() throws Exception {
        IOutCreateArchive<IOutItemCallbackZip> openOutArchiveZip = SevenZip.openOutArchiveZip();

        IOutCreateCallback<IOutItemCallbackZip> outCreateCallback = new IOutCreateCallback<IOutItemCallbackZip>() {

            public void setTotal(long total) throws SevenZipException {
                // TODO Auto-generated method stub

            }

            public void setCompleted(long complete) throws SevenZipException {
                // TODO Auto-generated method stub

            }

            public void setOperationResult(boolean operationResultOk) throws SevenZipException {
                // TODO Auto-generated method stub

            }

            public ISequentialInStream getStream(int index) throws SevenZipException {
                // TODO Auto-generated method stub
                return null;
            }

            public IOutItemCallbackZip getOutItemCallback() throws SevenZipException {
                return iOutItemCallback;
            }
        };
        openOutArchiveZip.createArchive(null, 0, outCreateCallback);
    }
}
