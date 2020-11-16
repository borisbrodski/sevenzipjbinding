package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetEncryptHeader;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.AbstractTestContext;
import net.sf.sevenzipjbinding.junit.compression.UpdateAbstractTest.UpdateAbstractTestContext;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;


/**
 * Abstract class for testing single file archive updates.
 *
 * @param <T>
 *            type of the out item class
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public abstract class UpdateAbstractTest<T extends IOutItemBase>
        extends CompressAbstractTest<UpdateAbstractTestContext> {
    public static class UpdateAbstractTestContext extends AbstractTestContext {
        VirtualContentConfiguration virtualContentConfiguration = new VirtualContentConfiguration();

        boolean useEncryption;
        boolean useEncryptionNoPassword;
        boolean useHeaderEncryption;
    }

    protected static abstract class ArchiveUpdater {
        abstract void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception;
    }

    static class ChangeLog {
        private Map<Integer, Change> indexChangeMap = new HashMap<Integer, Change>();
        private List<Change> newChanges = new ArrayList<Change>();
        private List<Change> deleteChanges = new ArrayList<Change>();
        private Change[] changes;
        Set<String> usedUpperCaseNames = new HashSet<String>();

        ChangeLog(IInArchive inArchive) throws SevenZipException {
            for (int i = 0; i < inArchive.getNumberOfItems(); i++) {
                usedUpperCaseNames.add(inArchive.getStringProperty(i, PropID.PATH).toUpperCase());
            }
        }

        public Change addNewChange() {
            Change change = new Change();
            newChanges.add(change);
            return change;
        }

        public Change updateItem(int oldIndex) {
            Change change = indexChangeMap.get(oldIndex);
            if (change != null) {
                return change;
            }
            change = new Change();
            indexChangeMap.put(oldIndex, change);
            return change;
        }

        private int reindex(IInArchive inArchive) throws SevenZipException {
            int count = inArchive.getNumberOfItems();
            int newCount = 0;
            List<Change> updatedChangeList = new ArrayList<Change>();
            for (Integer index : indexChangeMap.keySet()) {
                assertTrue(index < count);
            }
            for (int i = 0; i < count; i++) {
                newCount++;
                Change change = indexChangeMap.get(i);
                if (change == null) {
                    Change indexMappingChange = new Change();
                    indexMappingChange.newIndex = updatedChangeList.size();
                    indexMappingChange.oldIndex = i;
                    updatedChangeList.add(indexMappingChange);
                    continue;
                }
                change.oldPath = inArchive.getStringProperty(i, PropID.PATH);
                change.oldIndex = i;
                if (change.deleted) {
                    newCount--;
                    change.newIndex = -1;
                    deleteChanges.add(change);
                } else {
                    change.newIndex = updatedChangeList.size();
                    updatedChangeList.add(change);
                }
            }
            for (int i = 0; i < newChanges.size(); i++) {
                Change change = newChanges.get(i);
                change.newIndex = updatedChangeList.size();
                updatedChangeList.add(change);
                newCount++;
            }
            changes = updatedChangeList.toArray(new Change[updatedChangeList.size()]);
            return newCount;
        }
    }

    static class Change {
        Integer oldIndex;
        String oldPath;
        byte[] newContent;
        Integer newIndex;
        boolean deleted;
        Map<PropID, Object> propertyChangeMap = new HashMap<PropID, Object>();
    }

    public class ArchiveUpdateCallbackGeneric implements IOutCreateCallback<IOutItemAllFormats> {

        private ChangeLog changeLog;
        private int numberOfItems;

        public ArchiveUpdateCallbackGeneric(IInArchive inArchive, ChangeLog changeLog) throws SevenZipException {
            this.changeLog = changeLog;
            numberOfItems = inArchive.getNumberOfItems();
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {

        }

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        protected <T1 extends IOutItemBase> T1 createOutItem(int index, OutItemFactory<T1> outItemFactory)
                throws SevenZipException {
            Change change = changeLog.changes[index];
            if (change != null && change.oldIndex != null) {
                return outItemFactory.createOutItemAndCloneProperties(change.oldIndex);
            }

            if (index >= numberOfItems) {
                return outItemFactory.createOutItem();
            }
            return outItemFactory.createOutItemAndCloneProperties(index);
        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {
            IOutItemAllFormats outItem = createOutItem(index, outItemFactory);

            fillOutItem(index, outItem);
            return outItem;
        }

        protected void fillOutItem(int index, IOutItemAllFormats outItem) {
            Change change = changeLog.changes[index];

            if (change != null && change.newContent != null) {
                outItem.setUpdateIsNewData(true);
                outItem.setUpdateIsNewProperties(true);
            } else {
                outItem.setUpdateIsNewProperties(change != null && !change.propertyChangeMap.isEmpty());
            }

            if (change != null && change.newContent != null) {
                outItem.setDataSize((long) change.newContent.length);
            }

            if (change != null) {
                String path = (String) change.propertyChangeMap.get(PropID.PATH);
                if (path != null) {
                    outItem.setPropertyPath(path);
                }
                Integer attributes = (Integer) change.propertyChangeMap.get(PropID.ATTRIBUTES);
                if (attributes != null) {
                    outItem.setPropertyAttributes(attributes);
                }
                Integer posixAttributes = (Integer) change.propertyChangeMap.get(PropID.POSIX_ATTRIB);
                if (posixAttributes != null) {
                    outItem.setPropertyPosixAttributes(posixAttributes);
                }
                String user = (String) change.propertyChangeMap.get(PropID.USER);
                if (user != null) {
                    outItem.setPropertyUser(user);
                }
                String group = (String) change.propertyChangeMap.get(PropID.GROUP);
                if (group != null) {
                    outItem.setPropertyGroup(group);
                }
                Date creationTime = (Date) change.propertyChangeMap.get(PropID.CREATION_TIME);
                if (creationTime != null) {
                    outItem.setPropertyCreationTime(creationTime);
                }
                Date modificationTime = (Date) change.propertyChangeMap.get(PropID.LAST_MODIFICATION_TIME);
                if (modificationTime != null) {
                    outItem.setPropertyLastModificationTime(modificationTime);
                }
                Date accessTime = (Date) change.propertyChangeMap.get(PropID.LAST_ACCESS_TIME);
                if (accessTime != null) {
                    outItem.setPropertyLastAccessTime(accessTime);
                }
                Boolean isAnti = (Boolean) change.propertyChangeMap.get(PropID.IS_ANTI);
                if (accessTime != null) {
                    outItem.setPropertyIsAnti(isAnti);
                }
            }
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            Change change = changeLog.changes[index];
            assertTrue(change != null && change.newContent != null);
            return new ByteArrayStream(change.newContent, false);
        }
    }


    static class OutCreateCallbackWithPasswordWrapper<T extends IOutItemBase>
            implements IOutCreateCallback<T>, ICryptoGetTextPassword {
        private IOutCreateCallback<T> delegate;
        private String password;

        OutCreateCallbackWithPasswordWrapper(IOutCreateCallback<T> delegate, String password) {
            this.delegate = delegate;
            this.password = password;
        }

        public void setTotal(long total) throws SevenZipException {
            delegate.setTotal(total);
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
            delegate.setOperationResult(operationResultOk);
        }

        public void setCompleted(long complete) throws SevenZipException {
            delegate.setCompleted(complete);
        }

        public T getItemInformation(int index, OutItemFactory<T> outItemFactory) throws SevenZipException {
            return delegate.getItemInformation(index, outItemFactory);
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return delegate.getStream(index);
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }
    }

    protected abstract IOutCreateCallback<IOutItemAllFormats> getOutUpdateCallbackBase(IInArchive inArchive,
            ChangeLog changeLog)
            throws SevenZipException;

    private static final int OUTARCHIVE_MAX_SIZE = 10000000;
    private static final String DEFAULT_PASSWORD = "test-pass-321";

    protected final ArchiveUpdater updaterContent = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            change.newContent = new byte[100];
            getRandom().nextBytes(change.newContent);
        }
    };

    protected final ArchiveUpdater updaterLastModificationTime = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            Date newLastModificationTime = getDate(10 * WEEK);
            change.propertyChangeMap.put(PropID.LAST_MODIFICATION_TIME, newLastModificationTime);
        }
    };

    protected final ArchiveUpdater updaterUserGroup = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            change.propertyChangeMap.put(PropID.USER, inArchive.getStringProperty(0, PropID.USER) + "_new");
            change.propertyChangeMap.put(PropID.GROUP, inArchive.getStringProperty(0, PropID.GROUP) + "_new");
        }
    };

    protected final ArchiveUpdater updaterPath = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            change.propertyChangeMap.put(PropID.PATH, change.oldPath + "_new");
        }
    };

    public void setUseEncryptionNoPassword(boolean useEncryptionNoPassword) {
        context().useEncryptionNoPassword = useEncryptionNoPassword;
    }

    public void setUseEncryption(boolean useEncryption) {
        context().useEncryption = useEncryption;
    }

    public void setUseHeaderEncryption(boolean useHeaderEncryption) {
        context().useHeaderEncryption = useHeaderEncryption;
    }

    protected void testUpdate(final int countOfFiles, final int directoriesDepth, final int maxSubdirectories,
            final int averageFileLength, final int deltaFileLength, ArchiveUpdater... archiveUpdaters) throws Exception {
        UpdateAbstractTestContext context = context();
        VirtualContent virtualContent = new VirtualContent(context.virtualContentConfiguration);
        ArchiveFormat archiveFormat = getArchiveFormat();
        virtualContent.fillRandomly(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength,
                deltaFileLength, null, archiveFormat == ArchiveFormat.TAR);
        if (archiveFormat == ArchiveFormat.BZIP2) {
            virtualContent.updateItemPathByPath(virtualContent.getItemPath(0), "");
        }

        String password = null;
        if (context.useEncryption && !context.useEncryptionNoPassword) {
            password = DEFAULT_PASSWORD;
        }
        IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(archiveFormat);
        if (context.useHeaderEncryption) {
            assertTrue(outArchive instanceof IOutFeatureSetEncryptHeader);
            ((IOutFeatureSetEncryptHeader) outArchive).setHeaderEncryption(true);
        }
        ByteArrayStream byteArrayStream;
        boolean ok = false;
        try {
            byteArrayStream = new ByteArrayStream(OUTARCHIVE_MAX_SIZE);

            virtualContent.createOutArchive(outArchive, byteArrayStream, context.useEncryption, password);
            ok = true;
        } finally {
            try {
                outArchive.close();
            } catch (Throwable throwable) {
                if (ok) {
                    throw new RuntimeException("Error closing archive", throwable);
                }
            }
        }
        byteArrayStream.rewind();
        IInArchive inArchive;
        if (password == null) {
            inArchive = closeLater(SevenZip.openInArchive(archiveFormat, byteArrayStream));
        } else {
            inArchive = closeLater(SevenZip.openInArchive(archiveFormat, byteArrayStream, password));
        }
        IOutUpdateArchive<IOutItemAllFormats> connectedOutArchive = inArchive.getConnectedOutArchive();

        ByteArrayStream byteArrayStreamUpdated = new ByteArrayStream(OUTARCHIVE_MAX_SIZE);
        ChangeLog changeLog = new ChangeLog(inArchive);
        for (ArchiveUpdater archiveUpdater : archiveUpdaters) {
            archiveUpdater.prepareArchiveUpdate(inArchive, changeLog, getDefaultIndex());
        }
        int newCount = changeLog.reindex(inArchive);
        IOutCreateCallback<IOutItemAllFormats> updateCallbackBase = getOutUpdateCallbackBase(inArchive, changeLog);

        if (context.useEncryption) {
            updateCallbackBase = new OutCreateCallbackWithPasswordWrapper<IOutItemAllFormats>(updateCallbackBase,
                    password);
        }
        connectedOutArchive.updateItems(byteArrayStreamUpdated, newCount, updateCallbackBase);

        byteArrayStreamUpdated.rewind();

        if (context.useHeaderEncryption) {
            assertHeaderCrypted(byteArrayStream, archiveFormat);
        }

        IInArchive updatedInArchive;
        if (password == null) {
            updatedInArchive = closeLater(SevenZip.openInArchive(getArchiveFormat(), byteArrayStreamUpdated));
        } else {
            updatedInArchive = closeLater(SevenZip.openInArchive(getArchiveFormat(), byteArrayStreamUpdated, password));
        }

        updateVirtualContent(virtualContent, changeLog);

        if (context.useEncryption && !context.useEncryptionNoPassword) {
            // byteArrayStream.writeToOutputStream(new FileOutputStream("/tmp/u.7z"), true);
            assertAllItemsCrypted(inArchive);
        }
        virtualContent.verifyInArchive(updatedInArchive, password);
    }

    protected abstract int getDefaultIndex();

    private void updateVirtualContent(VirtualContent virtualContent, ChangeLog changeLog) {
        for (Change change : changeLog.deleteChanges) {
            if (change.deleted) {
                virtualContent.deleteItemByPath(change.oldPath);
            }
        }
        for (Change change : changeLog.changes) {
            if (change == null) {
                continue;
            }
            String pathToSearch = change.oldPath;
            if (change.oldIndex == null) {
                String newPath = (String) change.propertyChangeMap.get(PropID.PATH);
                virtualContent.addItem(change.newIndex, newPath, change.newContent);
                pathToSearch = newPath;
            } else if (change.newContent != null) {
                virtualContent.updateItemContentByPath(change.oldPath, change.newContent);
            }
            Map<PropID, Object> propertyMap = change.propertyChangeMap;
            String newPath = null;
            for (PropID propID : propertyMap.keySet()) {
                switch (propID) {
                case LAST_MODIFICATION_TIME:
                    virtualContent.updateItemLastModificationTimeByPath(pathToSearch, (Date) propertyMap.get(propID));
                    break;
                case PATH:
                    if (getArchiveFormat() != ArchiveFormat.BZIP2) {
                        newPath = (String) propertyMap.get(propID);
                    }
                    break;
                case USER:
                    virtualContent.updateUserByPath(pathToSearch, (String) propertyMap.get(propID));
                    break;
                case GROUP:
                    virtualContent.updateGroupByPath(pathToSearch, (String) propertyMap.get(propID));
                    break;
                case IS_FOLDER:
                    assertFalse("IS_FOLDER=true not yet supported", (Boolean) propertyMap.get(PropID.IS_FOLDER));
                    break;
                default:
                    fail("PropID not supported: " + propID);
                }
            }
            if (newPath != null) {
                virtualContent.updateItemPathByPath(pathToSearch, newPath);
            }
        }
    }

    protected void copyFromDelegate7z(IOutItem7z outItem, IOutItemAllFormats delegateOutItem) {
        outItem.setPropertyAttributes(delegateOutItem.getPropertyAttributes());
        outItem.setPropertyIsAnti(delegateOutItem.getPropertyIsAnti());
        outItem.setPropertyIsDir(delegateOutItem.getPropertyIsDir());
        outItem.setPropertyLastModificationTime(delegateOutItem.getPropertyLastModificationTime());
        outItem.setPropertyPath(delegateOutItem.getPropertyPath());
    }

    protected void copyFromDelegateTar(IOutItemTar outItem, IOutItemAllFormats delegateOutItem) {
        outItem.setPropertyPosixAttributes(delegateOutItem.getPropertyPosixAttributes());
        outItem.setPropertyIsDir(delegateOutItem.getPropertyIsDir());
        outItem.setPropertyLastModificationTime(delegateOutItem.getPropertyLastModificationTime());
        outItem.setPropertyPath(delegateOutItem.getPropertyPath());
        outItem.setPropertyUser(delegateOutItem.getPropertyUser());
        outItem.setPropertyGroup(delegateOutItem.getPropertyGroup());
    }

    protected void copyFromDelegateZip(IOutItemZip outItem, IOutItemAllFormats delegateOutItem) {
        outItem.setPropertyAttributes(delegateOutItem.getPropertyAttributes());
        outItem.setPropertyIsDir(delegateOutItem.getPropertyIsDir());
        outItem.setPropertyLastAccessTime(delegateOutItem.getPropertyLastAccessTime());
        outItem.setPropertyLastModificationTime(delegateOutItem.getPropertyLastModificationTime());
        outItem.setPropertyCreationTime(delegateOutItem.getPropertyCreationTime());
        outItem.setPropertyPath(delegateOutItem.getPropertyPath());
    }
}
