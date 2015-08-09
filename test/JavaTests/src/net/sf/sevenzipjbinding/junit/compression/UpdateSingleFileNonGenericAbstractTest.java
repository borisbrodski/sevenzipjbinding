package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;

/**
 * Base class for all update single file tests using non-generic update classback.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class UpdateSingleFileNonGenericAbstractTest<T extends IOutItemBase> extends UpdateSingleFileAbstractTest<T> {
    public class ArchiveUpdateCallback implements IOutCreateCallback<T> {
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

        public T getItemInformation(int index, OutItemFactory<T> outItemFactory) throws SevenZipException {
            T outItem = delegate.createOutItem(index, outItemFactory);
            T delegateOutItem = delegate.createOutItem(index, outItemFactory);

            if (!(delegateOutItem instanceof IOutItemAllFormats)) {
                throw new RuntimeException("Expected implementation of the IOutItemAllFormats interface");
            }
            delegate.fillOutItem(index, (IOutItemAllFormats) delegateOutItem);

            outItem.setDataStream(delegateOutItem.getDataStream());
            outItem.setDataSize(delegateOutItem.getDataSize());
            outItem.setUpdateIsNewData(delegateOutItem.getUpdateIsNewData());
            outItem.setUpdateIsNewProperties(delegateOutItem.getUpdateIsNewProperties());

            copyFromDelegate(outItem, (IOutItemAllFormats) delegateOutItem);

            return outItem;
        }


        public void freeResources(int index, T outItem) throws SevenZipException {
        }
    }

    protected abstract void copyFromDelegate(T outItem, IOutItemAllFormats delegateOutItem);

    @Override
    protected IOutCreateCallback<T> getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog)
            throws SevenZipException {
        return new ArchiveUpdateCallback(new ArchiveUpdateCallbackGeneric(inArchive, changeLog));
    }

}
