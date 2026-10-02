package net.sf.sevenzipjbinding.junit.bug;

import static org.junit.Assert.assertEquals;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ReportExtractResultIndexType;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.junit.Test;

/**
 * Up to 26.03-2.3 (and in 23.01-2.2) the native code declared the JNI signature of
 * {@link IArchiveExtractCallback#reportExtractResult(ReportExtractResultIndexType, int, ExtractOperationResult)}
 * without the <code>L...;</code> around its first parameter type. The method lookup could never succeed, so
 * every time the engine called <code>ReportExtractResult</code> the JVM was aborted ("Method not found:
 * reportExtractResult()"). The 7z handler calls it when a solid block fails to decode <em>after</em> all
 * requested files were written.
 * <p>
 * The test archive triggers exactly that, deterministically: a password-protected 7z (one file "a.txt",
 * "top secret\n", password "Secret123") extracted <em>without</em> a password. The garbage it decrypts to
 * happens to decode into a complete 11-byte file (CRCERROR), and the block then fails (DATAERROR). Only about
 * 1 in 300 random archives of this kind behave so; this one was captured from such a run (found on real
 * arm64 hardware, 2026-10-01).
 */
public class ReportExtractResultBlockErrorTest {
    private static final String ARCHIVE = "testdata/bug/7z block data error after the requested file.7z";

    /** Records the callbacks; overrides reportExtractResult() only if asked to. */
    private static class RecordingCallback implements IArchiveExtractCallback {
        final List<String> calls = new ArrayList<String>();

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) {
            if (extractAskMode != ExtractAskMode.EXTRACT) {
                return null;
            }
            calls.add("getStream(" + index + ")");
            return new ISequentialOutStream() {
                public int write(byte[] data) {
                    return data.length;
                }
            };
        }

        public void prepareOperation(ExtractAskMode extractAskMode) {
        }

        public void setOperationResult(ExtractOperationResult extractOperationResult) {
            calls.add("setOperationResult(" + extractOperationResult + ")");
        }

        public void setTotal(long total) {
        }

        public void setCompleted(long complete) {
        }
    }

    private static class OverridingCallback extends RecordingCallback {
        @Override
        public void reportExtractResult(ReportExtractResultIndexType indexType, int index,
                ExtractOperationResult extractOperationResult) {
            calls.add("reportExtractResult(" + indexType + ", " + index + ", " + extractOperationResult + ")");
        }
    }

    private static void extract(IArchiveExtractCallback callback) throws Exception {
        RandomAccessFile file = new RandomAccessFile(ARCHIVE, "r");
        try {
            IInArchive archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(file));
            try {
                archive.extract(null, false, callback);
            } finally {
                archive.close();
            }
        } finally {
            file.close();
        }
    }

    @Test
    public void blockErrorReachesTheJavaCallback() throws Exception {
        OverridingCallback callback = new OverridingCallback();
        extract(callback);
        assertEquals("[getStream(0), setOperationResult(CRCERROR), reportExtractResult(BLOCK_INDEX, 0, DATAERROR)]",
                callback.calls.toString());
    }

    @Test
    public void defaultReportExtractResultIgnoresTheBlockError() throws Exception {
        RecordingCallback callback = new RecordingCallback();
        extract(callback);
        assertEquals("[getStream(0), setOperationResult(CRCERROR)]", callback.calls.toString());
    }

    @Test
    public void extractSlowReturnsTheItemVerdict() throws Exception {
        RandomAccessFile file = new RandomAccessFile(ARCHIVE, "r");
        try {
            IInArchive archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(file));
            try {
                ExtractOperationResult result = archive.getSimpleInterface().getArchiveItem(0)
                        .extractSlow(new ISequentialOutStream() {
                            public int write(byte[] data) throws SevenZipException {
                                return data.length;
                            }
                        });
                assertEquals(ExtractOperationResult.CRCERROR, result);
            } finally {
                archive.close();
            }
        } finally {
            file.close();
        }
    }
}
