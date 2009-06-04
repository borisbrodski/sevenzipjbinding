package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarArchiveTest extends ExtractSingleFileAbstractArchiveTest {

	public ExtractSingleFileRarArchiveTest() {
		super(ArchiveFormat.RAR, 0, 2, 5);
	}

}
