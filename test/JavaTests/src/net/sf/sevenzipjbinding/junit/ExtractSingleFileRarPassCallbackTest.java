package net.sf.sevenzipjbinding.junit;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileRarPassCallbackTest extends ExtractSingleFileAbstractPassTest {

	public ExtractSingleFileRarPassCallbackTest() {
		super(ArchiveFormat.RAR, 0, 2, 5);
		usingPasswordCallback();
	}

}
