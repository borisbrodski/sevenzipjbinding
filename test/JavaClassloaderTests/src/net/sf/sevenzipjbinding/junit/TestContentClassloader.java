package net.sf.sevenzipjbinding.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestContentClassloader {
    private static final String TEST_ROOT_DIR = "TEST_ROOT_DIR";
    private static final String CLASS__UPDATE_ALTER_ITEMS = "net.sf.sevenzipjbinding.junit.snippets.UpdateAlterItems";

    private static final String SYSTEM_PROPERTY_TMP = "java.io.tmpdir";
    private static final String CLASSPATH_TEST_BUILD_DIR = "CLASSPATH_TEST_BUILD_DIR";

    private static File sevenzipjbindingJarFile;
    private static File sevenzipjbindingTestsJarFile;
    private static File nativeJarFile;
    private static String rootDir;
    private static ClassLoader classLoader;

    @BeforeClass
    public static void init() throws Exception {
        if (System.getProperty(TEST_ROOT_DIR) == null) {
            System.setProperty(TEST_ROOT_DIR, "../JavaTests");
        }

        rootDir = System.getProperty(TEST_ROOT_DIR);

        String dir = System.getenv(CLASSPATH_TEST_BUILD_DIR);
        if (dir == null) {
            fail("Please, set environment variable '" + CLASSPATH_TEST_BUILD_DIR + "' pointing to the build directory");
        }

        File dirFile = new File(dir);
        if (!dirFile.isDirectory()) {
            fail(dirFile.getAbsolutePath() + " is not a directory. Check environment variable '"
                    + CLASSPATH_TEST_BUILD_DIR);
        }

        sevenzipjbindingJarFile = new File(new File(dirFile, "jbinding-java"), "sevenzipjbinding.jar");

        if (!sevenzipjbindingJarFile.exists()) {
            fail(sevenzipjbindingJarFile.getAbsolutePath() + " is not found");
        }

        sevenzipjbindingTestsJarFile = new File(new File(dirFile, "jbinding-java"), "sevenzipjbinding-tests.jar");
        if (!sevenzipjbindingTestsJarFile.exists()) {
            fail(sevenzipjbindingTestsJarFile.getAbsolutePath() + " is not found");
        }

        File[] files = dirFile.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("sevenzipjbinding-") && name.endsWith(".jar");
            }
        });
        if (files.length == 0) {
            fail("Native JAR not found");
        }
        if (files.length > 1) {
            fail("More than one candidate for native JAR was found: " + Arrays.toString(files));
        }

        nativeJarFile = files[0];

        System.out.println("JAR:      " + sevenzipjbindingJarFile.getAbsolutePath());
        System.out.println("JAR-Test: " + sevenzipjbindingTestsJarFile.getAbsolutePath());
        System.out.println("Native:   " + nativeJarFile.getAbsolutePath());

        initializeClassLoader();
    }

    private static void initializeClassLoader() throws MalformedURLException {
        URL[] urls = new URL[] { //
                sevenzipjbindingJarFile.toURI().toURL(), //
                nativeJarFile.toURI().toURL(), //
                sevenzipjbindingTestsJarFile.toURI().toURL(), //
        };
        classLoader = new URLClassLoader(urls);
    }

    @Test
    public void runSingleSnippet() throws Throwable {
        String tmpDir = System.getProperty(SYSTEM_PROPERTY_TMP);
        File archiveFile = new File(tmpDir, "updated-alter-items.7z");

        Class<?> testSnippetClass = classLoader.loadClass(CLASS__UPDATE_ALTER_ITEMS);
        Method initSevenZipFromPlatformJARMethod = testSnippetClass.getMethod("main", String[].class);

        PrintStream oldOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        try {
            initSevenZipFromPlatformJARMethod.invoke(null, new Object[] {
                    new String[] { rootDir + "/testdata/snippets/to-update.7z", archiveFile.getAbsolutePath() } });
        } finally {
            System.setOut(oldOut);
        }
        assertEquals("Update successful", new String(out.toByteArray()).trim());
    }

    // terminate called after throwing an instance of 'CInBufferException'
    @Test
    public void testAll() throws Throwable {
        System.setProperty("TEST_TRACE", "true");
        Class<?> suite = classLoader.loadClass("net.sf.sevenzipjbinding.junit.AllTestSuiteMinimal");
        //        Class<?> test1 = classLoader
        //                .loadClass("net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesNonGeneric7zTest");
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));
        Result result = junit.run(suite);
        //        Result result = junit.run(test1);

        assertEquals(0, result.getFailureCount());
    }

    @Test
    public void testOneTest() throws Throwable {
        System.setProperty("TEST_TRACE", "true");
        Class<?> testclass = classLoader
                .loadClass("net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarVolumeHeaderPassCallbackTest");
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));
        Result result = junit.run(testclass);

        assertEquals(0, result.getFailureCount());
    }
}
