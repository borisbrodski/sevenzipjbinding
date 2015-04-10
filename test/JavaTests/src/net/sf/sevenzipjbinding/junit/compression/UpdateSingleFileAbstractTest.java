package net.sf.sevenzipjbinding.junit.compression;

import java.util.Date;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;

import org.junit.Test;

public abstract class UpdateSingleFileAbstractTest extends UpdateAbstractTest {
    private ArchiveUpdater updaterContent = new ArchiveUpdater() {
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog) throws Exception {
            Change change = changeLog.updateItem(0);
            change.newContent = new byte[100];
            random.get().nextBytes(change.newContent);
        }
    };

    private ArchiveUpdater updaterLastModificationTime = new ArchiveUpdater() {
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog) throws Exception {
            Change change = changeLog.updateItem(0);
            Date newLastModificationTime = getDate(10 * WEEK);
            change.propertyChangeMap.put(PropID.LAST_MODIFICATION_TIME, newLastModificationTime);
        }
    };

    private ArchiveUpdater updaterUserGroup = new ArchiveUpdater() {
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog) throws Exception {
            Change change = changeLog.updateItem(0);
            change.propertyChangeMap.put(PropID.USER, inArchive.getStringProperty(0, PropID.USER) + "_new");
            change.propertyChangeMap.put(PropID.GROUP, inArchive.getStringProperty(0, PropID.GROUP) + "_new");
        }
    };

    private ArchiveUpdater updaterPath = new ArchiveUpdater() {
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog) throws Exception {
            Change change = changeLog.updateItem(0);
            change.propertyChangeMap.put(PropID.PATH, change.oldPath + "_new");
        }
    };

    @Test
    public void testEmptyUpdate() throws Exception {
        testUpdate(false);
    }

    @Test
    public void testEmptyUpdateMultithreaded() throws Exception {
        testUpdate(true);
    }

    @Test
    public void testUpdateContent() throws Exception {
        testUpdate(false, updaterContent);
    }

    @Test
    public void testUpdateContentMultithreaded() throws Exception {
        testUpdate(true, updaterContent);
    }

    @Test
    public void testUpdateLastModificationTime() throws Exception {
        testUpdate(false, updaterLastModificationTime);
    }

    @Test
    public void testUpdateLastModificationTimeMultithreaded() throws Exception {
        testUpdate(true, updaterLastModificationTime);
    }

    @Test
    public void testUpdateContentAndLastModificationTime() throws Exception {
        testUpdate(false, updaterContent, updaterLastModificationTime);
    }

    @Test
    public void testUpdateContentAndLastModificationTimeMultithreaded() throws Exception {
        testUpdate(true, updaterContent, updaterLastModificationTime);
    }

    @Test
    public void testUpdatePath() throws Exception {
        testUpdate(false, updaterPath);
    }

    @Test
    public void testUpdatePathMultithreaded() throws Exception {
        testUpdate(true, updaterPath);
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTime() throws Exception {
        testUpdate(false, updaterContent, updaterPath, updaterLastModificationTime);
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeMultithreaded() throws Exception {
        testUpdate(true, updaterContent, updaterPath, updaterLastModificationTime);
    }

    @Test
    public void testUpdateUserGroup() throws Exception {
        testUpdate(false, updaterUserGroup);
    }

    @Test
    public void testUpdateUserGroupMultithreaded() throws Exception {
        testUpdate(true, updaterUserGroup);
    }

    private void testUpdate(boolean multithreaded, final ArchiveUpdater... archiveUpdaters) throws Exception {
        testSingleOrMultithreaded(multithreaded, new RunnableThrowsException() {

            public void run() throws Exception {
                testUpdate(1, 0, 0, 100000, 50000, archiveUpdaters);
            }
        });
    }
}
