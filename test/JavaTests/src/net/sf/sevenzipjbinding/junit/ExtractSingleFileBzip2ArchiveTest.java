package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileBzip2ArchiveTest extends ExtractSingleFileAbstractArchiveTest {

	public ExtractSingleFileBzip2ArchiveTest() {
		super(ArchiveFormat.BZIP2, "bz2", 1, 5, 9);
	}

}
