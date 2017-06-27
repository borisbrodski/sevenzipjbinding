package net.sf.sevenzipjbinding.junit.multiplefiles;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class ExtractMultipleFileCabVolumeWithoutVolumedTest extends JUnitNativeTestBase<VoidContext> {

	@Test(expected = SevenZipException.class)
	public void testWithAutodetect() throws Exception {
		test(true);
	}

	@Test(expected = SevenZipException.class)
	public void testWithoutAutodetect() throws Exception {
		test(false);
	}

	public void test(boolean useAutodetect) throws Exception {
		RandomAccessFile randomAccessFile = null;
		IInArchive inArchive = null;
		boolean ok = false;
		try {
			randomAccessFile = new RandomAccessFile("testdata/multiple-files/cab/vol-archive1.zip.0.disk1.cab", "r");
			inArchive = SevenZip.openInArchive(useAutodetect ? null : ArchiveFormat.CAB, new RandomAccessFileInStream(
					randomAccessFile));

			// Getting simple interface of the archive inArchive
			ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

			for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
				if (!item.isFolder()) {
					ExtractOperationResult result;
					result = item.extractSlow(new ISequentialOutStream() {
						public int write(byte[] data) throws SevenZipException {
							return data.length; // Return amount of consumed data
						}
					});
					assertTrue("Error extracting item: " + result, result == ExtractOperationResult.OK);
				}
			}
			ok = true;
		} finally {
			if (inArchive != null) {
				try {
					inArchive.close();
				} catch (SevenZipException e) {
					if (ok) {
						fail("Error closing archive: " + e);
					}
				}
			}
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					if (ok) {
						fail("Error closing file: " + e);
					}
				}
			}
		}
	}
}
