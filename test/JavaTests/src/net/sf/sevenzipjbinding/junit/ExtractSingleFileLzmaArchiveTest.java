package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileLzmaArchiveTest extends ExtractSingleFileAbstractArchiveTest {

	public ExtractSingleFileLzmaArchiveTest() {
		super(ArchiveFormat.LZMA, 1, 5, 9);
	}

}
