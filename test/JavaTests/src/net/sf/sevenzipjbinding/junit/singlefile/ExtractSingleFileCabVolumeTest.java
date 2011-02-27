package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileCabVolumeTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileCabVolumeTest() {
		super(ArchiveFormat.CAB, "disk1.cab", 0, 1, 2);

		usingVolumes(true);
	}

}
