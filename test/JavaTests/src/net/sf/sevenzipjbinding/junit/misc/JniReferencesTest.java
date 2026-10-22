package net.sf.sevenzipjbinding.junit.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IArchiveOpenCallback;
import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.IOutFeatureSetSolid;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * The native code must not let JNI local references pile up over the items of one native call, and must not use a
 * local reference on another thread.
 * <p>
 * Local references live until the native method returns, so every reference a per-item callback forgets to delete
 * stays alive for the whole extraction. For the output streams (PR #66) that kept all of them in memory and could
 * exhaust the heap on archives with very many files. Up to 26.03-2.3 the extract, update and volume callbacks still
 * leaked one to three references per item, and the update callback used the local reference to its
 * <code>OutArchiveImpl</code> on 7-Zip's encoder threads, which only HotSpot tolerates (a checking JVM aborts: "Bad
 * global or local ref passed to JNI").
 * <p>
 * Each scenario runs {@value #ITEM_COUNT} items in a child JVM with <code>-Xcheck:jni</code>, which reports a growing
 * local reference table ("JNI local refs: N, exceeds capacity") and invalid references ("Bad global or local ref
 * passed to JNI"; both seen on JDK 8 and 17). JVMs without these checks pass trivially.
 */
public class JniReferencesTest {
    private static final int ITEM_COUNT = 300;
    private static final String CHILD_OK = "CHILD-OK";
    private static final long CHILD_TIMEOUT_SECONDS = 300;

    @Test
    public void extract7zItems() throws Exception {
        runChild("extract-7z");
    }

    @Test
    public void extractZipItems() throws Exception {
        runChild("extract-zip");
    }

    @Test
    public void create7zItemsMultithreaded() throws Exception {
        runChild("create-7z");
    }

    @Test
    public void createZipItems() throws Exception {
        runChild("create-zip");
    }

    @Test
    public void updateZipItems() throws Exception {
        runChild("update-zip");
    }

    @Test
    public void openSplitArchiveWithManyVolumes() throws Exception {
        runChild("volumes");
    }

    private static void runChild(String scenario) throws Exception {
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        List<String> command = Arrays.asList(java, "-Xcheck:jni", "-Xmx128m", "-cp",
                System.getProperty("java.class.path"), Child.class.getName(), scenario);
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        process.getOutputStream().close();
        String output = readAll(process.getInputStream());
        assertTrue("child JVM timed out", process.waitFor(CHILD_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        List<String> problems = new ArrayList<String>();
        for (String line : output.split("\r?\n")) {
            if (line.contains("JNI local refs") || line.contains("FATAL ERROR") || line.contains("Bad global or local ref")) {
                problems.add(line.trim());
            }
        }
        assertEquals("checked JNI reports problems (scenario " + scenario + "), first 5: "
                + problems.subList(0, Math.min(5, problems.size())), 0, problems.size());
        assertEquals("child JVM output:\n" + output, 0, process.exitValue());
        assertTrue("child JVM output:\n" + output, output.contains(CHILD_OK));
    }

    /**
     * Runs in the child JVM: {@code Child <scenario>}. Prints {@value JniReferencesTest#CHILD_OK} and exits with 0 on
     * success.
     */
    public static class Child {
        public static void main(String[] args) throws Exception {
            SevenZip.initSevenZipFromPlatformJAR();
            String scenario = args[0];
            if (scenario.equals("extract-7z")) {
                expectExtracted(open(ArchiveFormat.SEVEN_ZIP, create(ArchiveFormat.SEVEN_ZIP, 1)));
            } else if (scenario.equals("extract-zip")) {
                expectExtracted(open(ArchiveFormat.ZIP, create(ArchiveFormat.ZIP, 1)));
            } else if (scenario.equals("create-7z")) {
                expectExtracted(open(ArchiveFormat.SEVEN_ZIP, create(ArchiveFormat.SEVEN_ZIP, 4)));
            } else if (scenario.equals("create-zip")) {
                create(ArchiveFormat.ZIP, 1);
            } else if (scenario.equals("update-zip")) {
                expectExtracted(open(ArchiveFormat.ZIP, update(create(ArchiveFormat.ZIP, 1))));
            } else if (scenario.equals("volumes")) {
                openVolumes(create(ArchiveFormat.SEVEN_ZIP, 1));
            } else {
                fail("unknown scenario " + scenario);
            }
            System.out.println(CHILD_OK);
            System.exit(0);
        }

        private static byte[] content(int index) {
            return ("item " + index).getBytes();
        }

        private static byte[] create(ArchiveFormat format, int threadCount) throws SevenZipException {
            ByteArrayStream outputStream = new ByteArrayStream(1024 * 1024);
            IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(format);
            try {
                if (format == ArchiveFormat.SEVEN_ZIP) {
                    ((IOutFeatureSetMultithreading) outArchive).setThreadCount(threadCount);
                    // One file per block: one input stream per item, read on the encoder threads
                    ((IOutFeatureSetSolid) outArchive).setSolidFiles(1);
                }
                outArchive.createArchive(outputStream, ITEM_COUNT, new IOutCreateCallback<IOutItemAllFormats>() {
                    public void setOperationResult(boolean operationResultOk) {
                    }

                    public void setTotal(long total) {
                    }

                    public void setCompleted(long complete) {
                    }

                    public IOutItemAllFormats getItemInformation(int index,
                            OutItemFactory<IOutItemAllFormats> outItemFactory) {
                        IOutItemAllFormats item = outItemFactory.createOutItem();
                        item.setPropertyPath("item" + index + ".txt");
                        item.setDataSize(Long.valueOf(content(index).length));
                        return item;
                    }

                    public ISequentialInStream getStream(int index) {
                        return new ByteArrayStream(content(index), true);
                    }
                });
            } finally {
                closeQuietly(outArchive);
            }
            return Arrays.copyOf(outputStream.getBytes(), (int) outputStream.getSize());
        }

        /**
         * Rewrite the archive keeping all items: every item goes through GetUpdateItemInfo() with an attached
         * in-archive.
         */
        private static byte[] update(byte[] archive) throws SevenZipException {
            IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.ZIP, new ByteArrayStream(archive, false));
            ByteArrayStream outputStream = new ByteArrayStream(1024 * 1024);
            try {
                IOutUpdateArchive<IOutItemAllFormats> outArchive = inArchive.getConnectedOutArchive();
                outArchive.updateItems(outputStream, ITEM_COUNT, new IOutCreateCallback<IOutItemAllFormats>() {
                    public void setOperationResult(boolean operationResultOk) {
                    }

                    public void setTotal(long total) {
                    }

                    public void setCompleted(long complete) {
                    }

                    public IOutItemAllFormats getItemInformation(int index,
                            OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                        return outItemFactory.createOutItem(index);
                    }

                    public ISequentialInStream getStream(int index) {
                        return null;
                    }
                });
            } finally {
                closeQuietly(inArchive);
            }
            return Arrays.copyOf(outputStream.getBytes(), (int) outputStream.getSize());
        }

        private static IInArchive open(ArchiveFormat format, byte[] archive) throws SevenZipException {
            return SevenZip.openInArchive(format, new ByteArrayStream(archive, false));
        }

        /**
         * Extract all items in one call: getStream(), prepareOperation() and setOperationResult() for each item.
         */
        private static void expectExtracted(IInArchive inArchive) throws SevenZipException {
            try {
                if (inArchive.getNumberOfItems() != ITEM_COUNT) {
                    fail("items: " + inArchive.getNumberOfItems());
                }
                final int[] okCount = new int[1];
                inArchive.extract(null, false, new IArchiveExtractCallback() {
                    public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
                        return new ISequentialOutStream() {
                            public int write(byte[] data) {
                                return data.length;
                            }
                        };
                    }

                    public void prepareOperation(ExtractAskMode extractAskMode) {
                    }

                    public void setOperationResult(ExtractOperationResult extractOperationResult) {
                        if (extractOperationResult == ExtractOperationResult.OK) {
                            okCount[0]++;
                        }
                    }

                    public void setTotal(long total) {
                    }

                    public void setCompleted(long complete) {
                    }
                });
                if (okCount[0] != ITEM_COUNT) {
                    fail("extracted OK: " + okCount[0]);
                }
            } finally {
                closeQuietly(inArchive);
            }
        }

        /**
         * Split the archive into {@value JniReferencesTest#ITEM_COUNT} volumes and open them in one call: the split
         * handler asks the volume callback for each volume.
         */
        private static void openVolumes(byte[] archive) throws SevenZipException {
            final Map<String, byte[]> volumes = new HashMap<String, byte[]>();
            int volumeSize = (archive.length + ITEM_COUNT - 1) / ITEM_COUNT;
            for (int i = 0; i * volumeSize < archive.length; i++) {
                volumes.put(String.format("archive.7z.%03d", Integer.valueOf(i + 1)), Arrays.copyOfRange(archive,
                        i * volumeSize, Math.min(archive.length, (i + 1) * volumeSize)));
            }
            if (volumes.size() < ITEM_COUNT / 2) {
                fail("volumes: " + volumes.size());
            }

            class VolumeCallback implements IArchiveOpenCallback, IArchiveOpenVolumeCallback {
                String currentName = "archive.7z.001";

                public Object getProperty(PropID propID) {
                    return propID == PropID.NAME ? currentName : null;
                }

                public IInStream getStream(String filename) {
                    byte[] volume = volumes.get(filename);
                    if (volume == null) {
                        return null;
                    }
                    currentName = filename;
                    return new ByteArrayStream(volume, false);
                }

                public void setTotal(Long files, Long bytes) {
                }

                public void setCompleted(Long files, Long bytes) {
                }
            }
            IInArchive inArchive = SevenZip.openInArchive(ArchiveFormat.SPLIT,
                    new ByteArrayStream(volumes.get("archive.7z.001"), false), new VolumeCallback());
            try {
                if (inArchive.getNumberOfItems() != 1) {
                    fail("split items: " + inArchive.getNumberOfItems());
                }
                Long size = (Long) inArchive.getProperty(0, PropID.SIZE);
                if (size == null || size.longValue() != archive.length) {
                    fail("joined size " + size + ", expected " + archive.length);
                }
            } finally {
                closeQuietly(inArchive);
            }
        }

        private static void closeQuietly(java.io.Closeable closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                fail("close: " + e);
            }
        }

        private static void fail(String message) {
            System.out.println("FAILED: " + message);
            System.exit(1);
        }
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
}
