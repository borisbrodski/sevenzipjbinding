package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.IOutItemCallbackBase;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.IOutUpdateCallback;
import net.sf.sevenzipjbinding.IOutUpdateCallbackBase;
import net.sf.sevenzipjbinding.IOutUpdateCallbackGeneric;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent;
import net.sf.sevenzipjbinding.junit.tools.VirtualContent.VirtualContentConfiguration;
import net.sf.sevenzipjbinding.util.ByteArrayStream;


/**
 * Abstract class for testing single file archive updates.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class UpdateAbstractTest extends CompressAbstractTest {
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

    public class ArchiveUpdateCallbackGeneric implements IOutUpdateCallbackGeneric {

        private IInArchive inArchive;
        private ChangeLog changeLog;
        private int numberOfItems;

        public ArchiveUpdateCallbackGeneric(IInArchive inArchive, ChangeLog changeLog) throws SevenZipException {
            this.inArchive = inArchive;
            this.changeLog = changeLog;
            numberOfItems = inArchive.getNumberOfItems();
        }

        public boolean isNewData(int index) throws SevenZipException {
            Change change = changeLog.changes[index];
            return change != null && change.newContent != null;
        }

        public boolean isNewProperties(int index) throws SevenZipException {
            Change change = changeLog.changes[index];
            return change != null && !change.propertyChangeMap.isEmpty();
        }

        public int getOldArchiveItemIndex(int index) throws SevenZipException {
            Change change = changeLog.changes[index];
            if (change != null && change.oldIndex != null) {
                return change.oldIndex;
            }
            if (index >= numberOfItems) {
                return -1;
            }
            return index;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            Change change = changeLog.changes[index];
            if (change != null && change.newContent != null) {
                return new ByteArrayStream(change.newContent, false);
            }
            return null;
        }

        public void setOperationResult(boolean operationResultOk) throws SevenZipException {

        }

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public Object getProperty(int index, PropID propID) throws SevenZipException {
            Change change = changeLog.changes[index];
            if (change != null) {
                if (propID == PropID.SIZE && change.newContent != null) {
                    return (long) change.newContent.length;
                }
                if (change.propertyChangeMap.containsKey(propID)) {
                    return change.propertyChangeMap.get(propID);
                }
            }
            int oldArchiveItemIndex = getOldArchiveItemIndex(index);
            if (oldArchiveItemIndex == -1) {
                return null;
            }
            return inArchive.getProperty(oldArchiveItemIndex, propID);
        }

    }

    protected abstract IOutUpdateCallbackBase getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog)
            throws SevenZipException;

    private static final int OUTARCHIVE_MAX_SIZE = 10000000;
    private VirtualContentConfiguration virtualContentConfiguration = new VirtualContentConfiguration();

    protected ArchiveUpdater updaterContent = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            change.newContent = new byte[100];
            random.get().nextBytes(change.newContent);
        }
    };

    protected ArchiveUpdater updaterLastModificationTime = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            Date newLastModificationTime = getDate(10 * WEEK);
            change.propertyChangeMap.put(PropID.LAST_MODIFICATION_TIME, newLastModificationTime);
        }
    };

    protected ArchiveUpdater updaterUserGroup = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            change.propertyChangeMap.put(PropID.USER, inArchive.getStringProperty(0, PropID.USER) + "_new");
            change.propertyChangeMap.put(PropID.GROUP, inArchive.getStringProperty(0, PropID.GROUP) + "_new");
        }
    };

    protected ArchiveUpdater updaterPath = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            change.propertyChangeMap.put(PropID.PATH, change.oldPath + "_new");
        }
    };

    @SuppressWarnings("unchecked")
    protected void testUpdate(final int countOfFiles, final int directoriesDepth, final int maxSubdirectories,
            final int averageFileLength, final int deltaFileLength, ArchiveUpdater... archiveUpdaters) throws Exception {
        VirtualContent virtualContent = new VirtualContent(virtualContentConfiguration);
        virtualContent.fillRandomly(countOfFiles, directoriesDepth, maxSubdirectories, averageFileLength,
                deltaFileLength, null);
        ArchiveFormat archiveFormat = getArchiveFormat();
        if (archiveFormat == ArchiveFormat.BZIP2) {
            virtualContent.updateItemPathByPath(virtualContent.getItemPath(0), "");
        }
        IOutCreateArchive<IOutItemCallback> outArchive = SevenZip.openOutArchive(archiveFormat);
        ByteArrayStream byteArrayStream;
        boolean ok = false;
        try {
            byteArrayStream = new ByteArrayStream(OUTARCHIVE_MAX_SIZE);

            virtualContent.createOutArchive(outArchive, byteArrayStream);
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
        IInArchive inArchive = closeLater(SevenZip.openInArchive(archiveFormat, byteArrayStream));
        IOutUpdateArchive<IOutItemCallbackBase> connectedOutArchive = inArchive.getConnectedOutArchive();

        ByteArrayStream byteArrayStreamUpdated = new ByteArrayStream(OUTARCHIVE_MAX_SIZE);
        ChangeLog changeLog = new ChangeLog(inArchive);
        for (ArchiveUpdater archiveUpdater : archiveUpdaters) {
            archiveUpdater.prepareArchiveUpdate(inArchive, changeLog, getDefaultIndex());
        }
        int newCount = changeLog.reindex(inArchive);
        IOutUpdateCallbackBase updateCallbackBase = getOutUpdateCallbackBase(inArchive, changeLog);

        if (updateCallbackBase instanceof IOutUpdateCallbackGeneric) {
            connectedOutArchive.updateItems(byteArrayStreamUpdated, newCount,
                    (IOutUpdateCallbackGeneric) updateCallbackBase);
        } else {
            connectedOutArchive.updateItems(byteArrayStreamUpdated, newCount,
                    (IOutUpdateCallback<IOutItemCallbackBase>) updateCallbackBase);
        }

        byteArrayStreamUpdated.rewind();
        IInArchive updatedInArchive = closeLater(SevenZip.openInArchive(getArchiveFormat(), byteArrayStreamUpdated));

        updateVirtualContent(virtualContent, changeLog);

        virtualContent.verifyInArchive(updatedInArchive);
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
}
