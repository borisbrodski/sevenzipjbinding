package net.sf.sevenzipjbinding.springboottest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

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
import net.sf.sevenzipjbinding.IOutUpdateArchive;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * Creates, extracts and updates in-memory archives. Items are keyed by path: 7z reorders items on update (items kept
 * from the old archive stay in their old block, re-compressed ones follow). Every callback and every stream method
 * reports to the {@link CallbackProbe} first.
 */
@Service
public class ArchiveService {
    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    private final CallbackProbe probe;

    public ArchiveService(CallbackProbe probe) throws SevenZipNativeInitializationException {
        this.probe = probe;
        SevenZip.initSevenZipFromPlatformJAR();
    }

    /**
     * Deterministic, moderately compressible data (a text over eight letters).
     */
    public static byte[] data(int size) {
        Random random = new Random(size);
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) ('a' + random.nextInt(8));
        }
        return data;
    }

    /**
     * Items <code>item0.txt</code>, <code>item1.txt</code>, ... of the given sizes.
     */
    public static Map<String, byte[]> items(int... sizes) {
        Map<String, byte[]> items = new LinkedHashMap<>();
        for (int i = 0; i < sizes.length; i++) {
            items.put("item" + i + ".txt", data(sizes[i]));
        }
        return items;
    }

    /**
     * Create an archive with the given items.
     *
     * @param threadCount
     *            passed to {@link IOutFeatureSetMultithreading#setThreadCount(int)}, 0 for formats without it
     * @param level
     *            compression level, negative for the default
     */
    public byte[] create(ArchiveFormat format, int threadCount, int level, Map<String, byte[]> items)
            throws IOException {
        probe.begin();
        List<String> paths = new ArrayList<>(items.keySet());
        ByteArrayStream outputStream = new ByteArrayStream(Integer.MAX_VALUE);
        try (IOutCreateArchive<IOutItemAllFormats> outArchive = SevenZip.openOutArchive(format)) {
            if (threadCount > 0) {
                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(threadCount);
            }
            if (level >= 0) {
                ((IOutFeatureSetLevel) outArchive).setLevel(level);
            }
            outArchive.createArchive(new ProbedOutStream(outputStream), items.size(),
                    new ProbedCreateCallback("IOutCreateCallback") {
                        @Override
                        public IOutItemAllFormats getItemInformation(int index,
                                OutItemFactory<IOutItemAllFormats> outItemFactory) {
                            probe.callback("IOutCreateCallback.getItemInformation()");
                            IOutItemAllFormats item = outItemFactory.createOutItem();
                            item.setPropertyPath(paths.get(index));
                            item.setDataSize((long) items.get(paths.get(index)).length);
                            return item;
                        }

                        @Override
                        public ISequentialInStream getStream(int index) {
                            probe.callback("IOutCreateCallback.getStream()");
                            return new ProbedInStream(new ByteArrayStream(items.get(paths.get(index)), true));
                        }
                    });
        }
        return bytes(outputStream);
    }

    /**
     * Extract all items in one call and compare them with <code>expected</code>.
     */
    public void extract(ArchiveFormat format, byte[] archive, Map<String, byte[]> expected) throws IOException {
        probe.begin();
        try (IInArchive inArchive = SevenZip.openInArchive(format,
                new ProbedInStream(new ByteArrayStream(archive, false)))) {
            int count = inArchive.getNumberOfItems();
            if (count != expected.size()) {
                throw new IllegalStateException("items: " + count + ", expected " + expected.size());
            }
            String[] paths = new String[count];
            for (int i = 0; i < count; i++) {
                paths[i] = (String) inArchive.getProperty(i, PropID.PATH);
                if (!expected.containsKey(paths[i])) {
                    throw new IllegalStateException("unexpected item " + paths[i]);
                }
            }
            ByteArrayStream[] extracted = new ByteArrayStream[count];
            inArchive.extract(null, false, new IArchiveExtractCallback() {
                @Override
                public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
                    probe.callback("IArchiveExtractCallback.getStream()");
                    extracted[index] = new ByteArrayStream(Integer.MAX_VALUE);
                    return new ProbedOutStream(extracted[index]);
                }

                @Override
                public void prepareOperation(ExtractAskMode extractAskMode) {
                    probe.callback("IArchiveExtractCallback.prepareOperation()");
                }

                @Override
                public void setOperationResult(ExtractOperationResult extractOperationResult) {
                    probe.callback("IArchiveExtractCallback.setOperationResult()");
                    if (extractOperationResult != ExtractOperationResult.OK) {
                        throw new IllegalStateException("operation result " + extractOperationResult);
                    }
                }

                @Override
                public void setTotal(long total) {
                    probe.callback("IArchiveExtractCallback.setTotal()");
                }

                @Override
                public void setCompleted(long complete) {
                    probe.callback("IArchiveExtractCallback.setCompleted()");
                }
            });
            for (int i = 0; i < count; i++) {
                if (extracted[i] == null) {
                    throw new IllegalStateException(paths[i] + " not extracted");
                }
                byte[] bytes = bytes(extracted[i]);
                byte[] expectedBytes = expected.get(paths[i]);
                if (!Arrays.equals(bytes, expectedBytes)) {
                    throw new IllegalStateException(paths[i] + ": " + bytes.length + " bytes extracted, expected "
                            + expectedBytes.length + (bytes.length == expectedBytes.length ? " (content differs)" : ""));
                }
            }
        }
    }

    /**
     * Update an archive: replace the content of one item and append a new one.
     *
     * @param threadCount
     *            passed to {@link IOutFeatureSetMultithreading#setThreadCount(int)} if the format supports it
     * @return the updated archive
     */
    public byte[] update(ArchiveFormat format, byte[] archive, int threadCount, String pathToReplace,
            byte[] newContent, String pathToAdd, byte[] contentToAdd) throws IOException {
        probe.begin();
        ByteArrayStream outputStream = new ByteArrayStream(Integer.MAX_VALUE);
        try (IInArchive inArchive = SevenZip.openInArchive(format,
                new ProbedInStream(new ByteArrayStream(archive, false)))) {
            int oldCount = inArchive.getNumberOfItems();
            int indexToReplace = -1;
            for (int i = 0; i < oldCount; i++) {
                if (pathToReplace.equals(inArchive.getProperty(i, PropID.PATH))) {
                    indexToReplace = i;
                }
            }
            if (indexToReplace < 0) {
                throw new IllegalStateException(pathToReplace + " not in the archive");
            }
            int indexToReplaceFinal = indexToReplace;
            IOutUpdateArchive<IOutItemAllFormats> outArchive = inArchive.getConnectedOutArchive();
            if (threadCount > 0 && outArchive instanceof IOutFeatureSetMultithreading) {
                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(threadCount);
            }
            outArchive.updateItems(new ProbedOutStream(outputStream), oldCount + 1,
                    new ProbedCreateCallback("IOutCreateCallback(update)") {
                        @Override
                        public IOutItemAllFormats getItemInformation(int index,
                                OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                            probe.callback("IOutCreateCallback(update).getItemInformation()");
                            if (index == oldCount) {
                                IOutItemAllFormats item = outItemFactory.createOutItem();
                                item.setPropertyPath(pathToAdd);
                                item.setDataSize((long) contentToAdd.length);
                                return item;
                            }
                            if (index != indexToReplaceFinal) {
                                return outItemFactory.createOutItem(index);
                            }
                            IOutItemAllFormats item = outItemFactory.createOutItemAndCloneProperties(index);
                            item.setUpdateIsNewProperties(true); // new data requires new properties (cloned here)
                            item.setUpdateIsNewData(true);
                            item.setDataSize((long) newContent.length);
                            return item;
                        }

                        @Override
                        public ISequentialInStream getStream(int index) {
                            probe.callback("IOutCreateCallback(update).getStream()");
                            if (index == oldCount) {
                                return new ProbedInStream(new ByteArrayStream(contentToAdd, true));
                            }
                            if (index == indexToReplaceFinal) {
                                return new ProbedInStream(new ByteArrayStream(newContent, true));
                            }
                            return null;
                        }
                    });
        }
        return bytes(outputStream);
    }

    private static byte[] bytes(ByteArrayStream stream) {
        return Arrays.copyOf(stream.getBytes(), (int) stream.getSize());
    }

    private abstract class ProbedCreateCallback implements IOutCreateCallback<IOutItemAllFormats> {
        private final String name;

        ProbedCreateCallback(String name) {
            this.name = name;
        }

        @Override
        public void setOperationResult(boolean operationResultOk) {
            probe.callback(name + ".setOperationResult()");
        }

        @Override
        public void setTotal(long total) {
            probe.callback(name + ".setTotal()");
        }

        @Override
        public void setCompleted(long complete) {
            probe.callback(name + ".setCompleted()");
        }
    }

    private class ProbedInStream implements IInStream {
        private final IInStream delegate;

        ProbedInStream(IInStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read(byte[] data) throws SevenZipException {
            probe.callback("IInStream.read()");
            return delegate.read(data);
        }

        @Override
        public long seek(long offset, int seekOrigin) throws SevenZipException {
            probe.callback("IInStream.seek()");
            return delegate.seek(offset, seekOrigin);
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }

    /**
     * 7z and zip need a seekable output ({@link IOutStream}).
     */
    private class ProbedOutStream implements IOutStream {
        private final IOutStream delegate;

        ProbedOutStream(IOutStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int write(byte[] data) throws SevenZipException {
            probe.callback("IOutStream.write()");
            return delegate.write(data);
        }

        @Override
        public long seek(long offset, int seekOrigin) throws SevenZipException {
            probe.callback("IOutStream.seek()");
            return delegate.seek(offset, seekOrigin);
        }

        @Override
        public void setSize(long newSize) throws SevenZipException {
            probe.callback("IOutStream.setSize()");
            delegate.setSize(newSize);
        }
    }
}
