package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.PropID;

import org.junit.Test;

public abstract class UpdateMultipleFilesAbstractTest<T extends IOutItemBase> extends UpdateAbstractTest<T> {
    private static class DirectoryTreeConfiguration {
        final int countOfFiles;
        final int directoriesDepth;
        final int maxSubdirectories;
        final int averageFileLength;
        final int deltaFileLength;

        public DirectoryTreeConfiguration(int countOfFiles, int directoriesDepth, int maxSubdirectories,
                int averageFileLength, int deltaFileLength) {
            this.countOfFiles = countOfFiles;
            this.directoriesDepth = directoriesDepth;
            this.maxSubdirectories = maxSubdirectories;
            this.averageFileLength = averageFileLength;
            this.deltaFileLength = deltaFileLength;
        }
    }
    private static class IndexArchiveUpdater extends ArchiveUpdater {
        ArchiveUpdater archiveUpdater;
        int[] indexies;

        public IndexArchiveUpdater(ArchiveUpdater archiveUpdater, int... indexies) {
            this.archiveUpdater = archiveUpdater;
            this.indexies = indexies;
        }

        @Override
        void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int defaultIndexies) throws Exception {
            for (int index : indexies) {
                archiveUpdater.prepareArchiveUpdate(inArchive, changeLog, index);
            }
        }
    }

    private static final DirectoryTreeConfiguration CONF_FLAT = new DirectoryTreeConfiguration(10, 0, 0, 100000, 50000);
    private static final DirectoryTreeConfiguration CONF_DEEP = new DirectoryTreeConfiguration(64, 5, 2, 10000, 5000);

    protected ArchiveUpdater updaterAddNewItem = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.addNewChange();

            File file = new File((String) inArchive.getProperty(index, PropID.PATH));
            String path;

            do {
                path = new File(file.getParentFile(), getRandomFilename(random.get())).getPath();
            } while (changeLog.usedUpperCaseNames.contains(path.toUpperCase()));

            changeLog.usedUpperCaseNames.add(path.toUpperCase());

            change.newContent = new byte[100];
            random.get().nextBytes(change.newContent);

            change.propertyChangeMap.put(PropID.PATH, path);
            change.propertyChangeMap.put(PropID.IS_FOLDER, false);
        }
    };

    protected ArchiveUpdater updaterDeleteItem = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.updateItem(index);
            change.deleted = true;
        }
    };

    @Override
    protected int getDefaultIndex() {
        return -1;
    }

    @Test
    public void testEmptyUpdateConfFlat() throws Exception {
        testUpdate(false, CONF_FLAT);
    }

    @Test
    public void testEmptyUpdateConfFlatMultithreaded() throws Exception {
        testUpdate(true, CONF_FLAT);
    }

    @Test
    public void testEmptyUpdateConfDeepFlat() throws Exception {
        testUpdate(false, CONF_DEEP);
    }

    @Test
    public void testEmptyUpdateConfDeepMultithreaded() throws Exception {
        testUpdate(true, CONF_DEEP);
    }

    @Test
    public void testUpdateContentConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateContentConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateContentConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateContentConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateLastModificationTimeConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateLastModificationTimeConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateLastModificationTimeConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateLastModificationTimeConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateContentAndLastModificationTimeConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), new IndexArchiveUpdater(
                        updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateContentAndLastModificationTimeConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(true, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), new IndexArchiveUpdater(
                        updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateContentAndLastModificationTimeConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), new IndexArchiveUpdater(
                        updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateContentAndLastModificationTimeConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), new IndexArchiveUpdater(
                        updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(true, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, //
                new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeSameIndexiesConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        int[] indexies = getIndexies(conf, 5);
        testUpdate(false, conf, //
                new IndexArchiveUpdater(updaterContent, indexies), //
                new IndexArchiveUpdater(updaterPath, indexies), //
                new IndexArchiveUpdater(updaterLastModificationTime, indexies));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeSameIndexiesConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        int[] indexies = getIndexies(conf, 5);
        testUpdate(true, conf, //
                new IndexArchiveUpdater(updaterContent, indexies), //
                new IndexArchiveUpdater(updaterPath, indexies), //
                new IndexArchiveUpdater(updaterLastModificationTime, indexies));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeSameIndexiesConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        int[] indexies = getIndexies(conf, 5);
        testUpdate(false, conf, //
                new IndexArchiveUpdater(updaterContent, indexies), //
                new IndexArchiveUpdater(updaterPath, indexies), //
                new IndexArchiveUpdater(updaterLastModificationTime, indexies));
    }

    @Test
    public void testUpdatePathAndContentAndLastModificationTimeSameIndexiesConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        int[] indexies = getIndexies(conf, 5);
        testUpdate(true, conf, //
                new IndexArchiveUpdater(updaterContent, indexies), //
                new IndexArchiveUpdater(updaterPath, indexies), //
                new IndexArchiveUpdater(updaterLastModificationTime, indexies));
    }

    @Test
    public void testUpdateUserGroupConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterUserGroup, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateUserGroupConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterUserGroup, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateUserGroupConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterUserGroup, getIndexies(conf, 5)));
    }

    @Test
    public void testUpdateUserGroupConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterUserGroup, getIndexies(conf, 5)));
    }

    // ------------------------------------------------------------------------------------------
    // Multiple files specific tests
    // ------------------------------------------------------------------------------------------

    @Test
    public void testAddNewItemConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterAddNewItem, getIndexies(conf, 5)));
    }

    @Test
    public void testAddNewItemConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterAddNewItem, getIndexies(conf, 5)));
    }

    @Test
    public void testAddNewItemConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterAddNewItem, getIndexies(conf, 5)));
    }

    @Test
    public void testAddNewItemConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterAddNewItem, getIndexies(conf, 5)));
    }

    @Test
    public void testDeleteItemConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterDeleteItem, getIndexies(conf, 5)));
    }

    @Test
    public void testDeleteItemConfFlatMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_FLAT;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterDeleteItem, getIndexies(conf, 5)));
    }

    @Test
    public void testDeleteItemConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(false, conf, new IndexArchiveUpdater(updaterDeleteItem, getIndexies(conf, 5)));
    }

    @Test
    public void testDeleteItemConfDeepMultithreaded() throws Exception {
        DirectoryTreeConfiguration conf = CONF_DEEP;
        testUpdate(true, conf, new IndexArchiveUpdater(updaterDeleteItem, getIndexies(conf, 5)));
    }

    private void testUpdate(boolean multithreaded, final DirectoryTreeConfiguration conf,
            final ArchiveUpdater... archiveUpdaters) throws Exception {
        testSingleOrMultithreaded(multithreaded, new RunnableThrowsException() {
            public void run() throws Exception {
                testUpdate(conf.countOfFiles, //
                        conf.directoriesDepth, //
                        conf.maxSubdirectories, //
                        conf.averageFileLength, //
                        conf.deltaFileLength, //
                        archiveUpdaters);

            }
        });
    }

    private int[] getIndexies(DirectoryTreeConfiguration conf, int amount) {
        assertTrue(amount < conf.countOfFiles);
        int[] result = new int[amount];
        int i = 0;
        whileLoop:
        while (i >= amount) {
            int index = random.get().nextInt(conf.countOfFiles);
            for (int j = 0; j < i; j++) {
                if (result[j] == index) {
                    continue whileLoop;
                }
            }
            result[i] = index;
        }
        return result;
    }
}
