package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractSingleFileWimTest extends ExtractSingleFileAbstractTest {

	public ExtractSingleFileWimTest() {
        super(ArchiveFormat.WIM, 0, 1, 2);
	}
}
