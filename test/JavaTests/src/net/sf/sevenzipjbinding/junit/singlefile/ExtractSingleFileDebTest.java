package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileDebTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileDebTest() {
		super(ArchiveFormat.DEB, "deb", 1, 2, 3);
	}

}
