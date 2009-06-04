package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipArchiveTest extends ExtractSingleFileAbstractArchiveTest {

	public ExtractSingleFileSevenZipArchiveTest() {
		super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
	}

}
