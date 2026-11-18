package net.sf.sevenzipjbinding.junit.initialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

/**
 * A native library that can't be copied into the temporary directory must be reported as the declared
 * {@link SevenZipNativeInitializationException}, recorded as {@link SevenZip#getLastInitializationException()}.
 * <p>
 * Initialization happens once per JVM, so the scenario runs in a fresh child JVM ({@link Child}). The child blocks the
 * library sub-directory with a regular file of the same name, so every copy into it fails on every OS, even as root.
 */
public class InitializationCopyFailureTest {
    private static final String CHILD_OK = "CHILD-OK";
    private static final long CHILD_TIMEOUT_SECONDS = 300;

    @Test
    public void copyFailureIsReportedAsDeclaredInitializationException() throws Exception {
        File tmpDir = createTempDirectory();
        try {
            String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            List<String> command = Arrays.asList(java, "-Xmx64m", "-cp", System.getProperty("java.class.path"),
                    Child.class.getName(), tmpDir.getAbsolutePath());
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            process.getOutputStream().close();
            String output = readAll(process.getInputStream());
            assertTrue("child JVM timed out", process.waitFor(CHILD_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            assertEquals("child JVM output:\n" + output, 0, process.exitValue());
            assertTrue("child JVM output:\n" + output, output.contains(CHILD_OK));
        } finally {
            deleteRecursively(tmpDir);
        }
    }

    /**
     * Runs in the child JVM: {@code Child <tmp-directory>}. Prints {@value InitializationCopyFailureTest#CHILD_OK}
     * and exits with 0 on success.
     */
    public static class Child {
        public static void main(String[] args) throws Exception {
            File tmpDir = new File(args[0]);
            String platform = SevenZip.getPlatformBestMatch();
            Properties libProperties = new Properties();
            try (InputStream in = SevenZip.class
                    .getResourceAsStream("/" + platform + "/sevenzipjbinding-lib.properties")) {
                libProperties.load(in);
            }
            File blocker = new File(tmpDir, "SevenZipJBinding-" + libProperties.getProperty("build.ref"));
            if (!blocker.createNewFile()) {
                fail("can't create " + blocker);
            }

            try {
                SevenZip.initSevenZipFromPlatformJAR(tmpDir);
                fail("initialization succeeded although the library can't be copied");
            } catch (SevenZipNativeInitializationException e) {
                if (!String.valueOf(e.getMessage()).contains("can't copy native library")) {
                    fail("unexpected message: " + e.getMessage());
                }
                if (SevenZip.getLastInitializationException() != e) {
                    fail("exception not recorded as the last initialization exception");
                }
                if (SevenZip.isInitializedSuccessfully()) {
                    fail("reported as initialized");
                }
            }
            System.out.println(CHILD_OK);
            System.exit(0);
        }

        private static void fail(String message) {
            System.out.println("FAILED: " + message);
            System.exit(1);
        }
    }

    private static File createTempDirectory() throws IOException {
        File dir = File.createTempFile("sevenzipjbinding-copy-failure-", "");
        if (!dir.delete() || !dir.mkdir()) {
            throw new IOException("can't create temporary directory " + dir);
        }
        return dir;
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), Charset.defaultCharset());
    }

    private static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }
}
