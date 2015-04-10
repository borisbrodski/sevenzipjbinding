package net.sf.sevenzipjbinding.junit.compression;

import java.util.Date;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.IOutUpdateCallback;
import net.sf.sevenzipjbinding.IOutUpdateCallbackBase;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * Base class for all update single file tests using non-generic update classback.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class UpdateSingleFileNonGenericAbstractTest extends UpdateSingleFileAbstractTest {
    public class ArchiveUpdateCallback implements IOutUpdateCallback<IOutItemCallback> {
        ArchiveUpdateCallbackGeneric delegate;

        private ArchiveUpdateCallback(ArchiveUpdateCallbackGeneric delegate) {
            this.delegate = delegate;
        }

        public boolean isNewData(int index) throws SevenZipException {
            return delegate.isNewData(index);
        }

        public boolean isNewProperties(int index) throws SevenZipException {
            return delegate.isNewProperties(index);
        }

        public int getOldArchiveItemIndex(int index) throws SevenZipException {
            return delegate.getOldArchiveItemIndex(index);
        }

        public IOutItemCallback getOutItemCallback(final int index) throws SevenZipException {
            return new IOutItemCallback() {

                public Date getLastAccessTime() throws SevenZipException {
                    return (Date) delegate.getProperty(index, PropID.LAST_ACCESS_TIME);
                }

                public Date getCreationTime() throws SevenZipException {
                    return (Date) delegate.getProperty(index, PropID.CREATION_TIME);
                }

                public String getUser() throws SevenZipException {
                    return (String) delegate.getProperty(index, PropID.USER);
                }

                public Integer getPosixAttributes() throws SevenZipException {
                    return (Integer) delegate.getProperty(index, PropID.POSIX_ATTRIB);
                }

                public String getGroup() throws SevenZipException {
                    return (String) delegate.getProperty(index, PropID.GROUP);
                }

                public boolean isDir() throws SevenZipException {
                    return (Boolean) delegate.getProperty(index, PropID.IS_FOLDER);
                }

                public boolean isAnti() throws SevenZipException {
                    return (Boolean) delegate.getProperty(index, PropID.IS_ANTI);
                }

                public long getSize() throws SevenZipException {
                    return (Long) delegate.getProperty(index, PropID.SIZE);
                }

                public String getPath() throws SevenZipException {
                    return (String) delegate.getProperty(index, PropID.PATH);
                }

                public Date getModificationTime() throws SevenZipException {
                    return (Date) delegate.getProperty(index, PropID.LAST_MODIFICATION_TIME);
                }

                public Integer getAttributes() throws SevenZipException {
                    return (Integer) delegate.getProperty(index, PropID.ATTRIBUTES);
                }
            };
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return delegate.getStream(index);
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
            delegate.setOperationResult(operationResultOk);
        }

        public void setTotal(long total) throws SevenZipException {
            delegate.setTotal(total);
        }

        public void setCompleted(long complete) throws SevenZipException {
            delegate.setCompleted(complete);
        }
    }

    @Override
    protected IOutUpdateCallbackBase getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog) {
        return new ArchiveUpdateCallback(new ArchiveUpdateCallbackGeneric(inArchive, changeLog));
    }
}
