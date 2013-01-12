package net.sf.sevenzipjbinding.junit.multiplefiles;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class ExtractMultipleFileWimTest extends ExtractMultipleFileAbstractTest {

	public ExtractMultipleFileWimTest() {
        super(ArchiveFormat.WIM, 0, 1, 2);
	}
}
