package net.sf.sevenzipjbinding.junit.compression;

import org.junit.Before;

import net.sf.sevenzipjbinding.ArchiveFormat;

public class CompressMultipleFileZipPassTest extends CompressMultipleFileAbstractTest {

    @Before
    public void configureEncryption() {
        setUseEncryption(true);
        setUseEncryptionNoPassword(false);
        setUseHeaderEncryption(false);
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.ZIP;
    }

}
