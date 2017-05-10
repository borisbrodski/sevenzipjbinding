package net.sf.sevenzipjbinding.junit.compression;

import org.junit.Test;

import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;

public abstract class UpdateSingleFileAbstractTest<T extends IOutItemBase> extends UpdateAbstractTest<T> {
    @Override
    protected int getDefaultIndex() {
        return 0;
    }

    @Test
    @Multithreaded
    @Repeat
    public void testEmptyUpdate() throws Exception {
        testUpdate();
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateContent() throws Exception {
        testUpdate(updaterContent);
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateLastModificationTime() throws Exception {
        testUpdate(updaterLastModificationTime);
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateContentAndLastModificationTime() throws Exception {
        testUpdate(updaterContent, updaterLastModificationTime);
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePath() throws Exception {
        testUpdate(updaterPath);
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdatePathAndContentAndLastModificationTime() throws Exception {
        testUpdate(updaterContent, updaterPath, updaterLastModificationTime);
    }

    @Test
    @Multithreaded
    @Repeat
    public void testUpdateUserGroup() throws Exception {
        testUpdate(updaterUserGroup);
    }

    private void testUpdate(final ArchiveUpdater... archiveUpdaters) throws Exception {
        testUpdate(1, 0, 0, 100000, 50000, archiveUpdaters);
    }
}
