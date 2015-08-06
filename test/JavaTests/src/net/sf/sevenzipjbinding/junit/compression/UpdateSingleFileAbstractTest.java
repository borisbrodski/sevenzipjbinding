package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.IOutItemBase;

import org.junit.Test;

public abstract class UpdateSingleFileAbstractTest<T extends IOutItemBase> extends UpdateAbstractTest<T> {
    @Override
    protected int getDefaultIndex() {
        return 0;
    }

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
