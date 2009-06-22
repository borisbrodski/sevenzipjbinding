package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileSevenZipVolumeTest extends ExtractSingleFileAbstractVolumeTest {

	public ExtractSingleFileSevenZipVolumeTest() {
		super(ArchiveFormat.SEVEN_ZIP, 0, 5, 9);
	}

}
