package net.sf.sevenzipjbinding.junit.initialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.junit.Test;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

/**
 * Tests, that 7-Zip-JBinding initialization procedure doesn't verify or overwrite temporary artifacts. This test
 * contains two phases and should be started with:
 * <ul>
 * <li>-Dsevenziptest.standard_initialization_test_phase=1
 * <li>-Dsevenziptest.standard_initialization_test_phase=2
 * </ul>
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public class InitializationDoesNotVerifyArtifactsTest {
    private static final String SYSTEM_PROPERTY_PHASE = "sevenziptest.standard_initialization_test_phase";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Test
    public void test() throws Exception {
        String phase = System.getProperty(SYSTEM_PROPERTY_PHASE);
        if (phase == null) {
            return;
        }

        switch (Integer.valueOf(phase)) {
        case 1:
            phase1();
            break;
        case 2:
            phase2();
        }
    }

    private void phase1() throws SevenZipNativeInitializationException {
        deleteOldTemporaryFiles();
        SevenZip.initSevenZipFromPlatformJAR(new File(TEMP_DIR));
    }

    private void deleteOldTemporaryFiles() {
        File[] directories = getSevenZipJBindingTemporarySubdirectories();
        for (File file : directories) {
            deleteAll(file);
        }
    }

    private void deleteAll(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File subFile : listFiles) {
                deleteAll(subFile);
            }
        }
        assertTrue("Can't remove '" + file.getAbsolutePath() + "'", file.delete());
    }

    private File[] getSevenZipJBindingTemporarySubdirectories() {
        File[] directories = new File(TEMP_DIR).listFiles(new FileFilter() {

            public boolean accept(File file) {
                return file.getName().toLowerCase().startsWith("sevenzipjbinding-");
            }
        });
        return directories;
    }

    private void phase2() throws IOException {
        File[] sevenZipJBindingTemporarySubdirectories = getSevenZipJBindingTemporarySubdirectories();
        assertEquals(1, sevenZipJBindingTemporarySubdirectories.length);
        File[] libraries = sevenZipJBindingTemporarySubdirectories[0].listFiles();
        assertTrue(libraries.length > 0);

        long[] sizes = new long[libraries.length];
        for (int i = 0; i < libraries.length; i++) {
            File library = libraries[i];
            sizes[i] = library.length();
            assertTrue(library.delete());
            assertTrue(library.createNewFile());
            assertEquals(0L, library.length());
        }
        try {
            SevenZip.initSevenZipFromPlatformJAR(new File(TEMP_DIR));
            for (int i = 0; i < libraries.length; i++) {
                assertEquals(sizes[i], libraries[i].length());
            }
        } catch (SevenZipNativeInitializationException e) {
            fail("7-Zip-JBinding initialization failure. Broken library files should be checked"
                    + " and restored from the platform jar.");
        }
    }
}
