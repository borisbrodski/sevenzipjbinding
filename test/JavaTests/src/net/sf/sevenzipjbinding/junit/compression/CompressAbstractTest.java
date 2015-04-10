package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;

/**
 * Common class for all compress tests besides standalone tests like {@link StandaloneCompressSevenZipTest}.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class CompressAbstractTest extends JUnitNativeTestBase {

    protected abstract ArchiveFormat getArchiveFormat();


}
