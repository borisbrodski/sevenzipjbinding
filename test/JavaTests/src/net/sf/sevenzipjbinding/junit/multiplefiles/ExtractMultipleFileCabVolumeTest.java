package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileCabVolumeTest extends ExtractMultipleFileAbstractTest {

	public ExtractMultipleFileCabVolumeTest() {
		super(ArchiveFormat.CAB, "disk1.cab", 0, 1, 1);

		usingVolumes(true);
	}

}
