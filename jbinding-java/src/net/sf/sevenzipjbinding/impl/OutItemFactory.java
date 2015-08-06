package net.sf.sevenzipjbinding.impl;

import java.util.Date;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutArchive;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutItemBZip2;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;

public class OutItemFactory<E extends IOutItemBase> {
    private int index;
    private IOutArchive<?> outArchive;

    public OutItemFactory(IOutArchive<?> outArchive, int index) {
        this.outArchive = outArchive;
        this.index = index;
    }

    @SuppressWarnings("unchecked")
    public E createOutItem() {
        return (E) createOutItemIntern();
    }

    private OutItem createOutItemIntern() {
        OutItem outItem = new OutItem(outArchive, index);
        fillDefaultValues(outItem);
        return outItem;
    }

    private void fillDefaultValues(OutItem outItem) {
        if (outArchive.getConnectedInArchive() != null) {
            outItem.setUpdateOldArchiveItemIndex(Integer.valueOf(-1));
            outItem.setUpdateIsNewData(Boolean.TRUE);
            outItem.setUpdateIsNewProperties(Boolean.TRUE);
        }

        switch (outItem.getArchiveFormat()) {
        case SEVEN_ZIP:
            fillDefaultValues7z(outItem);
            break;

        case ZIP:
            fillDefaultValuesZip(outItem);
            break;

        case BZIP2:
            fillDefaultValuesBZip2(outItem);
            break;

        case GZIP:
            fillDefaultValuesGZip(outItem);
            break;

        case TAR:
            fillDefaultValuesTar(outItem);
            break;

        default:
            throw new RuntimeException("No default values strategy for the archive format '"
                    + outItem.getArchiveFormat() + "'");
        }
    }

    private void fillDefaultValues7z(IOutItem7z outItem) {
        outItem.setPropertyIsAnti(Boolean.FALSE);
        outItem.setPropertyIsDir(Boolean.FALSE);
        outItem.setPropertyAttributes(Integer.valueOf(0));
    }

    private void fillDefaultValuesZip(IOutItemZip outItem) {
        outItem.setPropertyIsDir(Boolean.FALSE);
        outItem.setPropertyAttributes(Integer.valueOf(0));
    }

    private void fillDefaultValuesBZip2(IOutItemBZip2 outItem) {
    }

    private void fillDefaultValuesGZip(IOutItemGZip outItem) {
    }

    private void fillDefaultValuesTar(IOutItemTar outItem) {
        outItem.setPropertyIsDir(Boolean.FALSE);
    }

    public E createOutItem(int oldArchiveItemIndex) throws SevenZipException {
        IInArchive inArchive = outArchive.getConnectedInArchive();
        if (inArchive == null) {
            throw new RuntimeException("Not an update operation");
        }

        OutItem outItem = createOutItemIntern();

        outItem.setUpdateOldArchiveItemIndex(Integer.valueOf(oldArchiveItemIndex));
        outItem.setUpdateIsNewData(Boolean.FALSE);
        outItem.setUpdateIsNewProperties(Boolean.FALSE);

        return (E) outItem;
    }

    public E createOutItemAndCloneProperties(int updateOldArchiveItemIndex) throws SevenZipException {
        IInArchive inArchive = outArchive.getConnectedInArchive();
        if (inArchive == null) {
            throw new RuntimeException("Not an update operation");
        }

        OutItem outItem = createOutItemIntern();

        outItem.setUpdateOldArchiveItemIndex(Integer.valueOf(updateOldArchiveItemIndex));
        outItem.setUpdateIsNewData(Boolean.FALSE);
        outItem.setUpdateIsNewProperties(Boolean.TRUE);

        outItem.setPropertyPath((String) inArchive.getProperty(updateOldArchiveItemIndex, PropID.PATH));

        outItem.setPropertyAttributes((Integer) inArchive.getProperty(updateOldArchiveItemIndex, PropID.ATTRIBUTES));
        outItem.setPropertyPosixAttributes((Integer) inArchive.getProperty(updateOldArchiveItemIndex,
                PropID.POSIX_ATTRIB));

        outItem.setPropertyUser((String) inArchive.getProperty(updateOldArchiveItemIndex, PropID.USER));
        outItem.setPropertyGroup((String) inArchive.getProperty(updateOldArchiveItemIndex, PropID.GROUP));

        outItem.setPropertyCreationTime((Date) inArchive.getProperty(updateOldArchiveItemIndex, PropID.CREATION_TIME));
        outItem.setPropertyLastModificationTime((Date) inArchive.getProperty(updateOldArchiveItemIndex,
                PropID.LAST_MODIFICATION_TIME));
        outItem.setPropertyLastAccessTime((Date) inArchive.getProperty(updateOldArchiveItemIndex,
                PropID.LAST_ACCESS_TIME));

        outItem.setPropertyIsAnti((Boolean) inArchive.getProperty(updateOldArchiveItemIndex, PropID.IS_ANTI));
        outItem.setPropertyIsDir((Boolean) inArchive.getProperty(updateOldArchiveItemIndex, PropID.IS_FOLDER));

        return (E) outItem;
    }
}
