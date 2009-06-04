package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileGzipArchiveTest extends ExtractSingleFileAbstractArchiveTest {

	public ExtractSingleFileGzipArchiveTest() {
		super(ArchiveFormat.GZIP, "gz", 1, 5, 9);
	}

}
