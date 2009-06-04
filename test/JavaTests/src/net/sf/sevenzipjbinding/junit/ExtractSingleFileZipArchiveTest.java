package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileZipArchiveTest extends ExtractSingleFileAbstractArchiveTest {

	public ExtractSingleFileZipArchiveTest() {
		super(ArchiveFormat.ZIP, 0, 5, 9);
	}

}
