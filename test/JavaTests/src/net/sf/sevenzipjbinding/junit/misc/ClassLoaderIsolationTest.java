package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutStream;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * 7-Zip-JBinding must work when its classes are loaded by a class loader the system class loader can't see: a servlet
 * container, a Spring Boot fat jar, an OSGi bundle or an IDE plug-in (issues #7, #15, #35).
 * <p>
 * 7-Zip runs its encoder and decoder threads itself and the binding attaches them to the JVM. An attached thread has no
 * Java frames, so a <code>FindClass</code> on it resolves through the system class loader and fails for every binding
 * class. Up to 26.06-2.3 the native code then aborted the JVM ("FATAL ERROR: Class net/sf/sevenzipjbinding/IInStream
 * not found"). Two things are checked for every scenario:
 * <ul>
 * <li>the binding itself works (the archive round trip succeeds),</li>
 * <li>the user's callbacks and streams, when 7-Zip calls them on one of its own threads, see the context class loader
 * of the thread that called the binding, so that their code can resolve its classes the way it does on the calling
 * thread.</li>
 * </ul>
 * The native library can be loaded by only one class loader per JVM, so each scenario runs in a child JVM. Its system
 * class path holds a single class, {@link ClassLoaderIsolationLauncher} (top level: the macOS launcher resolves the
 * enclosing class of a nested main class); the test classes and the binding jars go into a
 * <code>URLClassLoader</code> with no parent (an isolated plug-in) or with the system class loader as parent (the fat
 * jar and web application shape: parent delegation, but the jars aren't on the system class path). The launcher
 * checks that the system class loader indeed can't see the binding, then runs {@link Child} through the custom loader.
 * <p>
 * 7-Zip creates threads according to the thread count, which defaults to the number of CPUs. The scenarios don't rely
 * on that default: compression sets the thread count explicitly, and the extraction of the filtered 7z archive gets its
 * decoder thread from the structure of the archive (see {@link Scenario}). A scenario that is supposed to run callbacks
 * on 7-Zip threads fails if none did, so it can't pass by proving nothing.
 */
public class ClassLoaderIsolationTest {
    private static final long CHILD_TIMEOUT_SECONDS = 300;
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;

    /**
     * One item of {@value #FILTERED_7Z_ITEM_SIZE} bytes of {@link Child#data(int)}, compressed with
     * <code>7z a -t7z -m0=BCJ -m1=LZMA2 -mx=1 -ms=off</code>. A folder with two coders (BCJ filter and LZMA2) is decoded
     * by 7-Zip's multithreaded coder mixer, which runs the LZMA2 decoder on its own thread and reads the archive from
     * there, independently of the number of CPUs.
     */
    private static final String FILTERED_7Z = "testdata/misc/classloader/bcj-lzma2.7z";
    private static final int FILTERED_7Z_ITEM_SIZE = 256 * KB;

    /**
     * Whether a scenario must see callbacks on 7-Zip's own threads to prove anything.
     */
    public enum OffThreadCallbacks {
        /** 7-Zip creates the thread independently of the number of CPUs. */
        REQUIRED,
        /** 7-Zip creates the thread only with several CPUs; on one CPU the scenario runs single-threaded. */
        REQUIRED_WITH_SEVERAL_CPUS,
        /** Reference scenario. */
        NOT_REQUIRED
    }

    /**
     * Archive bytes prepared in the test JVM for an extraction scenario.
     */
    public interface ArchiveSource {
        byte[] bytes() throws Exception;
    }

    /**
     * What the child JVM does with the binding.
     */
    public interface ChildAction {
        void run(Child child) throws Exception;
    }

    /**
     * The scenarios. The test JVM passes the name of the constant to the child, which resolves it again through its own
     * class loader: the launcher never sees this enum.
     */
    public enum Scenario {
        /**
         * 7z compression of 1 MB with two threads: 7-Zip asks for the item stream and reads it from a compression
         * thread (the site of the "IInStream not found" abort).
         */
        CREATE_7Z_TWO_THREADS(OffThreadCallbacks.REQUIRED, null, //
                child -> child.createAndExtract(ArchiveFormat.SEVEN_ZIP, 2, MB)),

        /**
         * Extraction of a 7z archive with three LZMA2 blocks (3 MB, created in the test JVM with two threads at level
         * 1). With several CPUs 7-Zip decodes the blocks in parallel and reports the item from a decoder thread (the
         * site of the "ExtractOperationResult not found" abort). The decoder thread count follows the number of CPUs
         * and can't be set through the binding, so on one CPU this scenario is a plain extraction.
         */
        EXTRACT_7Z_MULTI_BLOCK(OffThreadCallbacks.REQUIRED_WITH_SEVERAL_CPUS, //
                () -> multiBlock7z(), //
                child -> child.extractArchiveFile(ArchiveFormat.SEVEN_ZIP, 3 * MB)),

        /**
         * Extraction of the {@link ClassLoaderIsolationTest#FILTERED_7Z BCJ + LZMA2 archive}: the LZMA2 decoder runs on
         * a 7-Zip thread and reads the archive stream from there, on any number of CPUs.
         */
        EXTRACT_7Z_FILTERED(OffThreadCallbacks.REQUIRED, //
                () -> Files.readAllBytes(Paths.get(FILTERED_7Z)), //
                child -> child.extractArchiveFile(ArchiveFormat.SEVEN_ZIP, FILTERED_7Z_ITEM_SIZE)),

        /**
         * Zip round trip with two items. Zip was never affected; it checks the custom class loader setup as such. The
         * binding offers no thread count for zip, so 7-Zip's default applies.
         */
        ZIP(OffThreadCallbacks.NOT_REQUIRED, null, //
                child -> child.createAndExtract(ArchiveFormat.ZIP, 0, MB, MB / 2));

        final OffThreadCallbacks offThreadCallbacks;
        final ArchiveSource archive;
        final ChildAction action;

        Scenario(OffThreadCallbacks offThreadCallbacks, ArchiveSource archive, ChildAction action) {
            this.offThreadCallbacks = offThreadCallbacks;
            this.archive = archive;
            this.action = action;
        }
    }

    /**
     * Receives a note from every callback and stream method the binding invokes.
     */
    public interface CallbackMonitor {
        CallbackMonitor NONE = what -> {
        };

        void callback(String what);
    }

    /**
     * Runs in the child JVM, loaded by the custom class loader: <code>Child &lt;scenario&gt; [&lt;archive file&gt;]</code>.
     * Prints {@value #OK} and exits with 0 on success. Every callback and stream method records whether it ran on a
     * 7-Zip thread and, if so, whether that thread has the context class loader of the main thread.
     */
    public static class Child implements CallbackMonitor {
        public static final String OK = "CHILD-OK";
        private static final int MAX_PROBLEMS = 10;

        private final File archiveFile;
        private final Thread mainThread = Thread.currentThread();
        private final ClassLoader expectedLoader = Child.class.getClassLoader();
        private int offThreadCallbacks; // guarded by this
        private final Set<String> problems = new LinkedHashSet<String>(); // guarded by this

        public static void main(String[] args) throws Exception {
            if (Child.class.getClassLoader() == ClassLoader.getSystemClassLoader()) {
                fail("Child was loaded by the system class loader");
            }
            Scenario scenario = Scenario.valueOf(args[0]);
            Child child = new Child(args.length > 1 ? new File(args[1]) : null);
            SevenZip.initSevenZipFromPlatformJAR();
            scenario.action.run(child);
            child.verify(scenario.offThreadCallbacks);
            System.out.println(OK + ": " + child.offThreadCallbacks + " callbacks on 7-Zip threads");
            System.exit(0);
        }

        Child(File archiveFile) {
            this.archiveFile = archiveFile;
        }

        public synchronized void callback(String what) {
            Thread thread = Thread.currentThread();
            if (thread == mainThread) {
                return;
            }
            offThreadCallbacks++;
            ClassLoader loader = thread.getContextClassLoader();
            if (loader != expectedLoader && problems.size() < MAX_PROBLEMS) {
                problems.add(what + " on 7-Zip thread '" + thread.getName() + "': context class loader " + loader
                        + ", expected " + expectedLoader);
            }
        }

        synchronized void verify(OffThreadCallbacks expectation) {
            if (!problems.isEmpty()) {
                fail(problems.toString());
            }
            boolean required = expectation == OffThreadCallbacks.REQUIRED
                    || (expectation == OffThreadCallbacks.REQUIRED_WITH_SEVERAL_CPUS
                            && Runtime.getRuntime().availableProcessors() > 1);
            if (required && offThreadCallbacks == 0) {
                fail("no callback ran on a 7-Zip thread, the scenario proves nothing");
            }
        }

        /**
         * Create an archive from items of the given sizes and extract it again.
         *
         * @param threadCount
         *            passed to {@link IOutFeatureSetMultithreading#setThreadCount(int)}, 0 for formats without it
         */
        void createAndExtract(ArchiveFormat format, int threadCount, int... itemSizes) throws Exception {
            byte[][] items = items(itemSizes);
            byte[] archive = create(this, format, threadCount, -1, items);
            try (IInArchive inArchive = SevenZip.openInArchive(format,
                    new MonitoredInStream(this, new ByteArrayStream(archive, false)))) {
                expectExtracted(inArchive, items);
            }
        }

        /**
         * Extract the archive file prepared by the test JVM and compare it with items of the given sizes.
         */
        void extractArchiveFile(ArchiveFormat format, int... itemSizes) throws Exception {
            try (RandomAccessFile file = new RandomAccessFile(archiveFile, "r");
                    IInArchive inArchive = SevenZip.openInArchive(format,
                            new MonitoredInStream(this, new RandomAccessFileInStream(file)))) {
                expectExtracted(inArchive, items(itemSizes));
            }
        }

        /**
         * Deterministic, moderately compressible data (a text over eight letters).
         */
        static byte[] data(int size) {
            Random random = new Random(size);
            byte[] data = new byte[size];
            for (int i = 0; i < size; i++) {
                data[i] = (byte) ('a' + random.nextInt(8));
            }
            return data;
        }

        static byte[][] items(int... sizes) {
            byte[][] items = new byte[sizes.length][];
            for (int i = 0; i < sizes.length; i++) {
                items[i] = data(sizes[i]);
            }
            return items;
        }

        /**
         * Create an archive with one item per element of <code>items</code>.
         *
         * @param threadCount
         *            passed to {@link IOutFeatureSetMultithreading#setThreadCount(int)}, 0 for formats without it
         * @param level
         *            compression level, negative for the default
         */
        static byte[] create(final CallbackMonitor monitor, ArchiveFormat format, int threadCount, int level,
                final byte[][] items) throws Exception {
            ByteArrayStream outputStream = new ByteArrayStream(Integer.MAX_VALUE);
            try (IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(format)) {
                if (threadCount > 0) {
                    ((IOutFeatureSetMultithreading) outArchive).setThreadCount(threadCount);
                }
                if (level >= 0) {
                    ((IOutFeatureSetLevel) outArchive).setLevel(level);
                }
                outArchive.createArchive(new MonitoredOutStream(monitor, outputStream), items.length,
                        new IOutCreateCallback<IOutItemAllFormats>() {
                            public void setOperationResult(boolean operationResultOk) {
                                monitor.callback("IOutCreateCallback.setOperationResult()");
                            }

                            public void setTotal(long total) {
                                monitor.callback("IOutCreateCallback.setTotal()");
                            }

                            public void setCompleted(long complete) {
                                monitor.callback("IOutCreateCallback.setCompleted()");
                            }

                            public IOutItemAllFormats getItemInformation(int index,
                                    OutItemFactory<IOutItemAllFormats> outItemFactory) {
                                monitor.callback("IOutCreateCallback.getItemInformation()");
                                IOutItemAllFormats item = outItemFactory.createOutItem();
                                item.setPropertyPath("item" + index + ".txt");
                                item.setDataSize(Long.valueOf(items[index].length));
                                return item;
                            }

                            public ISequentialInStream getStream(int index) {
                                monitor.callback("IOutCreateCallback.getStream()");
                                return new MonitoredSequentialInStream(monitor, new ByteArrayStream(items[index],
                                        true));
                            }
                        });
            }
            return Arrays.copyOf(outputStream.getBytes(), (int) outputStream.getSize());
        }

        /**
         * Extract all items in one call and compare them with <code>expected</code>.
         */
        void expectExtracted(IInArchive inArchive, final byte[][] expected) throws SevenZipException {
            if (inArchive.getNumberOfItems() != expected.length) {
                fail("items: " + inArchive.getNumberOfItems() + ", expected " + expected.length);
            }
            final List<String> results = new ArrayList<String>();
            final ByteArrayStream[] extracted = new ByteArrayStream[expected.length];
            inArchive.extract(null, false, new IArchiveExtractCallback() {
                public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
                    callback("IArchiveExtractCallback.getStream()");
                    extracted[index] = new ByteArrayStream(Integer.MAX_VALUE);
                    return new MonitoredOutStream(Child.this, extracted[index]);
                }

                public void prepareOperation(ExtractAskMode extractAskMode) {
                    callback("IArchiveExtractCallback.prepareOperation()");
                }

                public void setOperationResult(ExtractOperationResult extractOperationResult) {
                    callback("IArchiveExtractCallback.setOperationResult()");
                    if (extractOperationResult != ExtractOperationResult.OK) {
                        results.add("operation result " + extractOperationResult);
                    }
                }

                public void setTotal(long total) {
                    callback("IArchiveExtractCallback.setTotal()");
                }

                public void setCompleted(long complete) {
                    callback("IArchiveExtractCallback.setCompleted()");
                }
            });
            if (!results.isEmpty()) {
                fail(results.toString());
            }
            for (int i = 0; i < expected.length; i++) {
                if (extracted[i] == null) {
                    fail("item " + i + " not extracted");
                }
                byte[] bytes = Arrays.copyOf(extracted[i].getBytes(), (int) extracted[i].getSize());
                if (!Arrays.equals(bytes, expected[i])) {
                    fail("item " + i + ": " + bytes.length + " bytes extracted, expected " + expected[i].length
                            + (bytes.length == expected[i].length ? " (content differs)" : ""));
                }
            }
        }

        private static void fail(String message) {
            System.out.println("FAILED: " + message);
            System.exit(1);
        }

        static class MonitoredSequentialInStream implements ISequentialInStream {
            private final CallbackMonitor monitor;
            private final ISequentialInStream delegate;

            MonitoredSequentialInStream(CallbackMonitor monitor, ISequentialInStream delegate) {
                this.monitor = monitor;
                this.delegate = delegate;
            }

            public int read(byte[] data) throws SevenZipException {
                monitor.callback("ISequentialInStream.read()");
                return delegate.read(data);
            }

            public void close() throws IOException {
                delegate.close();
            }
        }

        static class MonitoredInStream implements IInStream {
            private final CallbackMonitor monitor;
            private final IInStream delegate;

            MonitoredInStream(CallbackMonitor monitor, IInStream delegate) {
                this.monitor = monitor;
                this.delegate = delegate;
            }

            public int read(byte[] data) throws SevenZipException {
                monitor.callback("IInStream.read()");
                return delegate.read(data);
            }

            public long seek(long offset, int seekOrigin) throws SevenZipException {
                monitor.callback("IInStream.seek()");
                return delegate.seek(offset, seekOrigin);
            }

            public void close() throws IOException {
                delegate.close();
            }
        }

        /**
         * Seekable, since 7z and zip require an {@link IOutStream} to write the archive to.
         */
        static class MonitoredOutStream implements IOutStream {
            private final CallbackMonitor monitor;
            private final IOutStream delegate;

            MonitoredOutStream(CallbackMonitor monitor, IOutStream delegate) {
                this.monitor = monitor;
                this.delegate = delegate;
            }

            public int write(byte[] data) throws SevenZipException {
                monitor.callback("IOutStream.write()");
                return delegate.write(data);
            }

            public long seek(long offset, int seekOrigin) throws SevenZipException {
                monitor.callback("IOutStream.seek()");
                return delegate.seek(offset, seekOrigin);
            }

            public void setSize(long newSize) throws SevenZipException {
                monitor.callback("IOutStream.setSize()");
                delegate.setSize(newSize);
            }
        }
    }

    @Test
    public void create7zTwoThreadsIsolatedLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.ISOLATED, Scenario.CREATE_7Z_TWO_THREADS);
    }

    @Test
    public void create7zTwoThreadsDelegatingLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.DELEGATING, Scenario.CREATE_7Z_TWO_THREADS);
    }

    @Test
    public void extractMultiBlock7zIsolatedLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.ISOLATED, Scenario.EXTRACT_7Z_MULTI_BLOCK);
    }

    @Test
    public void extractMultiBlock7zDelegatingLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.DELEGATING, Scenario.EXTRACT_7Z_MULTI_BLOCK);
    }

    @Test
    public void extractFiltered7zIsolatedLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.ISOLATED, Scenario.EXTRACT_7Z_FILTERED);
    }

    @Test
    public void extractFiltered7zDelegatingLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.DELEGATING, Scenario.EXTRACT_7Z_FILTERED);
    }

    @Test
    public void createAndExtractZipIsolatedLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.ISOLATED, Scenario.ZIP);
    }

    @Test
    public void createAndExtractZipDelegatingLoader() throws Exception {
        runChild(ClassLoaderIsolationLauncher.DELEGATING, Scenario.ZIP);
    }

    private static void runChild(String loaderShape, Scenario scenario) throws Exception {
        File workDir = Files.createTempDirectory("sevenzipjbinding-classloader").toFile();
        try {
            File launcherDir = new File(workDir, "launcher");
            copyClassFile(ClassLoaderIsolationLauncher.class, launcherDir);

            String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            List<String> command = new ArrayList<String>(Arrays.asList(java, "-Xmx128m", "-cp",
                    launcherDir.getPath(), ClassLoaderIsolationLauncher.class.getName(), loaderShape,
                    System.getProperty("java.class.path"), Child.class.getName(), scenario.name()));
            if (scenario.archive != null) {
                File archive = new File(workDir, "archive");
                Files.write(archive.toPath(), scenario.archive.bytes());
                command.add(archive.getPath());
            }

            Process process = new ProcessBuilder(command).directory(workDir).redirectErrorStream(true).start();
            process.getOutputStream().close();
            OutputReader output = new OutputReader(process.getInputStream());
            boolean finished = process.waitFor(CHILD_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
            }
            String report = "child JVM (" + loaderShape + " loader, scenario " + scenario + ") "
                    + (finished ? "exit code " + process.exitValue() : "timed out") + ", output:\n" + output.text()
                    + crashLogs(workDir);
            assertTrue(report, finished);
            assertTrue(report, output.text().contains(ClassLoaderIsolationLauncher.OK));
            assertEquals(report, 0, process.exitValue());
            assertTrue(report, output.text().contains(Child.OK));
        } finally {
            deleteRecursively(workDir);
        }
    }

    /**
     * One item of 3 MB compressed with two threads at level 1: LZMA2 blocks of 1 MB, so three blocks for 7-Zip's
     * multithreaded decoder.
     */
    private static byte[] multiBlock7z() throws Exception {
        if (!SevenZip.isInitializedSuccessfully()) {
            SevenZip.initSevenZipFromPlatformJAR();
        }
        return Child.create(CallbackMonitor.NONE, ArchiveFormat.SEVEN_ZIP, 2, 1, Child.items(3 * MB));
    }

    /**
     * Copy the class file of <code>clazz</code> into <code>dir</code> (package directories included), so that a JVM
     * with <code>-cp dir</code> sees exactly this one class.
     */
    private static void copyClassFile(Class<?> clazz, File dir) throws IOException {
        String path = clazz.getName().replace('.', '/') + ".class";
        File target = new File(dir, path);
        target.getParentFile().mkdirs();
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(path)) {
            assertTrue("class file not found: " + path, in != null);
            Files.copy(in, target.toPath());
        }
    }

    private static String crashLogs(File workDir) throws IOException {
        StringBuilder result = new StringBuilder();
        File[] files = workDir.listFiles();
        if (files == null) {
            return "";
        }
        for (File file : files) {
            if (file.getName().startsWith("hs_err_pid")) {
                String[] lines = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())
                        .split("\r?\n");
                result.append("\n--- ").append(file.getName()).append(" (first lines) ---\n");
                for (int i = 0; i < Math.min(lines.length, 40); i++) {
                    result.append(lines[i]).append('\n');
                }
            }
        }
        return result.toString();
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

    /**
     * Drains the child's output on its own thread, so that a hanging child can't block the test beyond the timeout.
     */
    private static class OutputReader extends Thread {
        private final InputStream in;
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        OutputReader(InputStream in) {
            super("child output reader");
            this.in = in;
            setDaemon(true);
            start();
        }

        public void run() {
            try {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    bytes.write(buffer, 0, read);
                }
            } catch (IOException e) {
                // Child gone, keep what we have
            }
        }

        String text() throws InterruptedException {
            join(TimeUnit.SECONDS.toMillis(10));
            synchronized (bytes) {
                return new String(bytes.toByteArray(), Charset.defaultCharset());
            }
        }
    }
}
