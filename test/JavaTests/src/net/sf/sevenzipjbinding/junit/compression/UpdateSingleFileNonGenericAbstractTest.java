package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Base class for all update single file tests using non-generic update classback.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class UpdateSingleFileNonGenericAbstractTest extends UpdateSingleFileAbstractTest<IOutItemAllFormats> {
    public class ArchiveUpdateCallback implements IOutCreateCallback<IOutItemAllFormats> {
        ArchiveUpdateCallbackGeneric delegate;

        protected ArchiveUpdateCallback(ArchiveUpdateCallbackGeneric delegate) {
            this.delegate = delegate;
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

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            IOutItemAllFormats outItem = delegate.createOutItem(index, outItemFactory);
            IOutItemAllFormats delegateOutItem = delegate.createOutItem(index, outItemFactory);

            if (!(delegateOutItem instanceof IOutItemAllFormats)) {
                throw new RuntimeException("Expected implementation of the IOutItemAllFormats interface");
            }
            delegate.fillOutItem(index, delegateOutItem);

            outItem.setDataSize(delegateOutItem.getDataSize());
            outItem.setUpdateIsNewData(delegateOutItem.getUpdateIsNewData());
            outItem.setUpdateIsNewProperties(delegateOutItem.getUpdateIsNewProperties());

            copyFromDelegate(outItem, delegateOutItem);

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return delegate.getStream(index);
        }
    }

    protected abstract void copyFromDelegate(IOutItemAllFormats outItem, IOutItemAllFormats delegateOutItem);

    @Override
    protected IOutCreateCallback<IOutItemAllFormats> getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog)
            throws SevenZipException {
        return new ArchiveUpdateCallback(new ArchiveUpdateCallbackGeneric(inArchive, changeLog));
    }

}
