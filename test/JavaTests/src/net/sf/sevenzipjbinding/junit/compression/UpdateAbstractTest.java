package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    interface ArchiveUpdater {
        void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog) throws Exception;
    }

    static class ChangeLog {
        private Map<Integer, Change> indexChangeMap = new HashMap<Integer, Change>();
        private List<Change> newChanges = new ArrayList<Change>();
        private Change[] changes;

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

        private void reindex(IInArchive inArchive) throws SevenZipException {
            int count = inArchive.getNumberOfItems();
            List<Change> updatedChangeList = new ArrayList<Change>();
            for (int i = 0; i < count; i++) {
                Change change = indexChangeMap.get(i);
                if (change == null) {
                    updatedChangeList.add(null);
                    continue;
                }
                if (change.deleted) {
                    continue;
                }
                change.oldIndex = i;
                change.oldPath = inArchive.getStringProperty(i, PropID.PATH);
                change.newIndex = updatedChangeList.size();
                updatedChangeList.add(change);
            }
            for (int i = 0; i < newChanges.size(); i++) {
                Change change = newChanges.get(i);
                change.newIndex = updatedChangeList.size();
                updatedChangeList.add(change);
            }
            changes = updatedChangeList.toArray(new Change[updatedChangeList.size()]);
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

        public ArchiveUpdateCallbackGeneric(IInArchive inArchive, ChangeLog changeLog) {
            this.inArchive = inArchive;
            this.changeLog = changeLog;
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
            return inArchive.getProperty(getOldArchiveItemIndex(index), propID);
        }

    }

    protected abstract IOutUpdateCallbackBase getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog);

    private static final int OUTARCHIVE_MAX_SIZE = 10000000;
    private VirtualContentConfiguration virtualContentConfiguration = new VirtualContentConfiguration();

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
        ChangeLog changeLog = new ChangeLog();
        for (ArchiveUpdater archiveUpdater : archiveUpdaters) {
            archiveUpdater.prepareArchiveUpdate(inArchive, changeLog);
        }
        changeLog.reindex(inArchive);
        IOutUpdateCallbackBase updateCallbackBase = getOutUpdateCallbackBase(inArchive, changeLog);
        if (updateCallbackBase instanceof IOutUpdateCallbackGeneric) {
            connectedOutArchive.updateItems(byteArrayStreamUpdated, 1, (IOutUpdateCallbackGeneric) updateCallbackBase);
        } else {
            connectedOutArchive.updateItems(byteArrayStreamUpdated, 1,
                    (IOutUpdateCallback<IOutItemCallbackBase>) updateCallbackBase);
        }

        byteArrayStreamUpdated.rewind();
        IInArchive updatedInArchive = closeLater(SevenZip.openInArchive(getArchiveFormat(), byteArrayStreamUpdated));

        updateVirtualContent(virtualContent, changeLog);

        virtualContent.verifyInArchive(updatedInArchive);
    }

    private void updateVirtualContent(VirtualContent virtualContent, ChangeLog changeLog) {
        for (Change change : changeLog.changes) {
            if (change == null) {
                continue;
            }
            if (change.deleted) {
                virtualContent.deleteItemByPath(change.oldPath);
                continue;
            }
            if (change.newContent != null) {
                virtualContent.updateItemContentByPath(change.oldPath, change.newContent);
            }
            Map<PropID, Object> propertyMap = change.propertyChangeMap;
            String newPath = null;
            for (PropID propID : propertyMap.keySet()) {
                switch (propID) {
                case LAST_MODIFICATION_TIME:
                    virtualContent.updateItemLastModificationTimeByPath(change.oldPath, (Date) propertyMap.get(propID));
                    break;
                case PATH:
                    if (getArchiveFormat() != ArchiveFormat.BZIP2) {
                        newPath = (String) propertyMap.get(propID);
                    }
                    break;
                case USER:
                    virtualContent.updateUserByPath(change.oldPath, (String) propertyMap.get(propID));
                    break;
                case GROUP:
                    virtualContent.updateGroupByPath(change.oldPath, (String) propertyMap.get(propID));
                    break;
                default:
                    fail("PropID not supported: " + propID);
                }
            }
            if (newPath != null) {
                virtualContent.updateItemPathByPath(change.oldPath, newPath);
            }
        }
    }
}
