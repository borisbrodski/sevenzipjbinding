package net.sf.sevenzipjbinding.junit.singlefile.badarchive;

import static org.junit.Assert.fail;

import java.util.Random;

import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

import org.junit.Test;

/**
 * Tests behavior of SevenZipJBinding trying opening random stream of bytes as an archive.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class GarbageArchiveFileTest extends JUnitNativeTestBase {
	private static final int SINGLE_TEST_THREAD_COUNT = 20;
	private static final int SINGLE_TEST_TIMEOUT = 1000 * 60 * SINGLE_TEST_THREAD_COUNT;
	private final Random random = new Random(this.getClass().getCanonicalName().hashCode());

	@Test
	public void openBadArchive() throws Throwable {
		for (int i = 0; i < 900; i++) { // iteration 959 recognized as GZip archive :-)
			try {
				doOpenBadArchive(true);
				fail("Expected exception wasn't thrown. Iteration: " + i);
			} catch (SevenZipException e) {
				assert true;
			} catch (Throwable throwable) {
				System.out.println("Iteration: " + i);
				throw throwable;
			}
		}
	}

	@Test
	public void openBadArchiveMultithreaded() throws Throwable {
		final int[] threadsFinished = new int[] { SINGLE_TEST_THREAD_COUNT };
		final Throwable[] firstThrowable = new Throwable[] { null };
		for (int i = 0; i < SINGLE_TEST_THREAD_COUNT; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						openBadArchive();
					} catch (Throwable e) {
						synchronized (firstThrowable) {
							if (firstThrowable[0] == null) {
								firstThrowable[0] = e;
							}
						}
					} finally {
						synchronized (GarbageArchiveFileTest.this) {
							try {
								threadsFinished[0]--;
								GarbageArchiveFileTest.this.notify();
							} catch (Throwable throwable) {
								throwable.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
		long start = System.currentTimeMillis();
		synchronized (this) {
			while (true) {
				try {
					if (threadsFinished[0] == 0) {
						break;
					}
					wait(SINGLE_TEST_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (System.currentTimeMillis() - start > SINGLE_TEST_TIMEOUT) {
					fail("Time out");
				}
			}
		}
		if (firstThrowable[0] != null) {
			if (firstThrowable[0] instanceof SevenZipException) {
				throw (SevenZipException) firstThrowable[0];
			}
			throw new RuntimeException("Exception in underlying thread", firstThrowable[0]);
		}
	}

	public void doOpenBadArchive(boolean doItActually) throws Exception {
		final byte[] archive = new byte[random.nextInt(1000) + 1000];
		random.nextBytes(archive);
		if (!doItActually) {
			throw new SevenZipException("Skipped");
		}
		IInStream inStream = new IInStream() {
			int offset = 0;

			@Override
			public int read(byte[] data) throws SevenZipException {
				int result = 0;
				for (int i = 0; i < data.length && offset < archive.length; i++) {
					data[i] = archive[offset++];
					result++;
				}
				return result;
			}

			@Override
			public long seek(long newOffset, int seekOrigin) throws SevenZipException {
				switch (seekOrigin) {
				case SEEK_SET:
					offset = (int) newOffset;
					break;
				case SEEK_CUR:
					offset += newOffset;
					break;
				case SEEK_END:
					offset = (int) (archive.length + newOffset);
				}
				return offset >= archive.length ? archive.length : offset;
			}
		};
		SevenZip.openInArchive(null, inStream);
	}
}
