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

        public boolean isNtfsTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isDir() throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public Date getModificationTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getLastAccessTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getCreationTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Integer getAttributes() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Integer getPosixAttributes() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isAnti() throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public long getSize() throws SevenZipException {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getPath() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public String getUser() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public String getGroup() throws SevenZipException {
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

        public IOutItemCallback getOutItemCallback(int i) throws SevenZipException {
            return iOutItemCallback;
        }
    };

    static IOutItemCallbackZip outItemCallbackZip = new IOutItemCallbackZip() {

        public boolean isNtfsTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isDir() throws SevenZipException {
            // TODO Auto-generated method stub
            return false;
        }

        public long getSize() throws SevenZipException {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getPath() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getModificationTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getLastAccessTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Date getCreationTime() throws SevenZipException {
            // TODO Auto-generated method stub
            return null;
        }

        public Integer getAttributes() throws SevenZipException {
            // TODO Auto-generated method stub
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

        public IOutItemCallbackZip getOutItemCallback(int index) throws SevenZipException {
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

        public IOutItemCallbackZip getOutItemCallback(int index) throws SevenZipException {
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

            public IOutItemCallbackZip getOutItemCallback(int index) throws SevenZipException {
                return iOutItemCallback;
            }
        };
        openOutArchiveZip.createArchive(null, 0, outCreateCallback);
    }
}
