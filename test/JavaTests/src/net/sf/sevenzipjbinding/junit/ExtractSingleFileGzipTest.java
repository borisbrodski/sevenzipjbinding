package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileGzipTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileGzipTest() {
		super(ArchiveFormat.GZIP, "gz", 1, 5, 9);
	}

}
