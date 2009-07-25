package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import java.io.RandomAccessFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import org.junit.Test;

public class GetNumberOfItemInArchive {
	@Test
	public void snippetRunner() throws Exception {
		assertEquals(3, getNumberOfItemsInArchive("testdata/snippets/simple.zip"));
	}

	/* BEGIN_SNIPPET(GetNumberOfItemsInArchive) */
	private int getNumberOfItemsInArchive(String archiveFile) throws Exception {
		ISevenZipInArchive archive;

		archive = SevenZip.openInArchive(ArchiveFormat.ZIP, // null - autodetect
				new RandomAccessFileInStream(//
						new RandomAccessFile(archiveFile, "r")));

		return archive.getNumberOfItems();
	}
	/* END_SNIPPET */
}
