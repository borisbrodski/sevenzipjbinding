package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;
import net.sf.sevenzipjbinding.junit.tools.RandomTools;

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

    private static final DirectoryTreeConfiguration CONF_FLAT = //
            new DirectoryTreeConfiguration(10, 0, 0, 100000, 50000);
    private static final DirectoryTreeConfiguration CONF_FLAT_LOW_MEMORY = //
            new DirectoryTreeConfiguration(10, 0, 0, 10000, 5000);

    private static final DirectoryTreeConfiguration CONF_DEEP = //
            new DirectoryTreeConfiguration(64, 5, 2, 10000, 5000);
    private static final DirectoryTreeConfiguration CONF_DEEP_LOW_MEMORY = //
            new DirectoryTreeConfiguration(64, 5, 2, 1000, 500);

    DirectoryTreeConfiguration getConfDeep() {
        if (TestConfiguration.getCurrent().isOnLowMemory()) {
            return CONF_DEEP_LOW_MEMORY;
        }
        return CONF_DEEP;
    }

    DirectoryTreeConfiguration getConfFlat() {
        if (TestConfiguration.getCurrent().isOnLowMemory()) {
            return CONF_FLAT_LOW_MEMORY;
        }
        return CONF_FLAT;
    }

    protected final ArchiveUpdater updaterAddNewItem = new ArchiveUpdater() {
        @Override
        public void prepareArchiveUpdate(IInArchive inArchive, ChangeLog changeLog, int index) throws Exception {
            Change change = changeLog.addNewChange();

            File file = new File((String) inArchive.getProperty(index, PropID.PATH));
            String path;

            do {
                path = new File(file.getParentFile(), RandomTools.getRandomFilename()).getPath();
            } while (changeLog.usedUpperCaseNames.contains(path.toUpperCase()));

            changeLog.usedUpperCaseNames.add(path.toUpperCase());

            change.newContent = new byte[100];
            getRandom().nextBytes(change.newContent);

            change.propertyChangeMap.put(PropID.PATH, path);
            change.propertyChangeMap.put(PropID.IS_FOLDER, false);
        }
    };

    protected final ArchiveUpdater updaterDeleteItem = new ArchiveUpdater() {
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
    @Multithreaded
    @Repeat
    public void testEmptyUpdateConfFlat() throws Exception {
        testUpdate(getConfFlat());
    }

    @Test
    @Multithreaded
    @Repeat
    public void testEmptyUpdateConfDeepFlat() throws Exception {
        testUpdate(getConfDeep());
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateContentConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateContentConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateLastModificationTimeConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateLastModificationTimeConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateContentAndLastModificationTimeConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(
                        updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateContentAndLastModificationTimeConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(
                        updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePathConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePathConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePathAndContentAndLastModificationTimeConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePathAndContentAndLastModificationTimeConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterPath, getIndexies(conf, 5)), //
                new IndexArchiveUpdater(updaterLastModificationTime, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePathAndContentAndLastModificationTimeSameIndexiesConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        int[] indexies = getIndexies(conf, 5);
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, indexies), //
                new IndexArchiveUpdater(updaterPath, indexies), //
                new IndexArchiveUpdater(updaterLastModificationTime, indexies));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePathAndContentAndLastModificationTimeSameIndexiesConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        int[] indexies = getIndexies(conf, 5);
        testUpdate(conf, new IndexArchiveUpdater(updaterContent, indexies), //
                new IndexArchiveUpdater(updaterPath, indexies), //
                new IndexArchiveUpdater(updaterLastModificationTime, indexies));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateUserGroupConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterUserGroup, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateUserGroupConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterUserGroup, getIndexies(conf, 5)));
    }

    // ------------------------------------------------------------------------------------------
    // Multiple files specific tests
    // ------------------------------------------------------------------------------------------

    @Test
    @Multithreaded
    @Repeat
    public void testAddNewItemConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterAddNewItem, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testAddNewItemConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterAddNewItem, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testDeleteItemConfFlat() throws Exception {
        DirectoryTreeConfiguration conf = getConfFlat();
        testUpdate(conf, new IndexArchiveUpdater(updaterDeleteItem, getIndexies(conf, 5)));
    }

    @Test
    @Multithreaded
    @Repeat
    public void testDeleteItemConfDeep() throws Exception {
        DirectoryTreeConfiguration conf = getConfDeep();
        testUpdate(conf, new IndexArchiveUpdater(updaterDeleteItem, getIndexies(conf, 5)));
    }

    private void testUpdate(final DirectoryTreeConfiguration conf, final ArchiveUpdater... archiveUpdaters)
            throws Exception {
        testUpdate(conf.countOfFiles, //
                conf.directoriesDepth, //
                conf.maxSubdirectories, //
                conf.averageFileLength, //
                conf.deltaFileLength, //
                archiveUpdaters);
    }

    private int[] getIndexies(DirectoryTreeConfiguration conf, int amount) {
        assertTrue(amount < conf.countOfFiles);
        int[] result = new int[amount];
        int i = 0;
        whileLoop:
        while (i >= amount) {
            int index = getRandom().nextInt(conf.countOfFiles);
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
