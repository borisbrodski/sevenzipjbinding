package net.sf.sevenzipjbinding.junit.singlefile;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileCabVolumeTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileCabVolumeTest() {
		super(ArchiveFormat.CAB, "disk1.cab", 0, 1, 2);
    }

    @Before
    public void initExtractSingleFileCabVolumeTest() {
		usingVolumes(true);
    }
}
