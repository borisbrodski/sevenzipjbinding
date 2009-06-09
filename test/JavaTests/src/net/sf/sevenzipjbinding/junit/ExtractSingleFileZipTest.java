package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileZipTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileZipTest() {
		super(ArchiveFormat.ZIP, 0, 5, 9);
	}

}
