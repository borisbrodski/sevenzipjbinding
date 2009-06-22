package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public abstract class ExtractSingleFileAbstractVolumeTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileAbstractVolumeTest(ArchiveFormat archiveFormat, int compression1, int compression2,
			int compression3) {
		super(archiveFormat, compression1, compression2, compression3);
		init();
	}

	public ExtractSingleFileAbstractVolumeTest(ArchiveFormat archiveFormat, String extention, int compression1,
			int compression2, int compression3) {
		super(archiveFormat, extention, compression1, compression2, compression3);
		init();
	}

	private void init() {
		usingVolumes(true);
		setVolumedArchivePrefix("vol-");
		setVolumeArchivePostfix(".001");
	}
}
