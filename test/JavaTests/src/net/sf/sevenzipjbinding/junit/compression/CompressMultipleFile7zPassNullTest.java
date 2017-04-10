package net.sf.sevenzipjbinding.junit.compression;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class CompressMultipleFile7zPassNullTest extends CompressMultipleFileAbstractTest {

    @Before
    public void configureEncryption() {
        setUseEncryption(true);
        setUseEncryptionNoPassword(true);
        setUseHeaderEncryption(false);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.SEVEN_ZIP;
    }

}
