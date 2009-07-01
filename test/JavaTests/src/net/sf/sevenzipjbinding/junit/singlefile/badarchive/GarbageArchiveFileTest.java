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
	private final Random random = new Random(this.getClass().getCanonicalName().hashCode());

	@Test
	public void openBadArchive() throws Throwable {
		for (int i = 0; i < 10000; i++) {

			try {
				doOpenBadArchive(i == 959);
				fail("Expected exception wasn't thrown. Iteration: " + i);
			} catch (SevenZipException e) {
				assert true;
			} catch (Throwable throwable) {
				System.out.println("Iteration: " + i);
				throw throwable;
			}
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
				if (offset >= archive.length) {
					return 0;
				}
				data[0] = archive[offset++];
				return 1;
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
