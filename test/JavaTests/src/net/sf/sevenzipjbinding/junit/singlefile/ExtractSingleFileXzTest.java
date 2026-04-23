package net.sf.sevenzipjbinding.junit.singlefile;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * Test extraction of XZ compressed files.
 * 
 * XZ is a lossless data compression algorithm and file format that uses the LZMA2 
 * compression algorithm. This test verifies that 7-Zip-JBinding can extract XZ archives.
 * 
 * @author Boris Brodski
 * @since TBD
 */
public class ExtractSingleFileXzTest extends ExtractSingleFileAbstractTest {

    public ExtractSingleFileXzTest() {
        super(ArchiveFormat.XZ, 1, 5, 9);
    }

}
