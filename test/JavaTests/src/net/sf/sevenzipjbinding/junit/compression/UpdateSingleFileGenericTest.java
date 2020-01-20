package net.sf.sevenzipjbinding.junit.compression;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * Base class for all update single file tests using non-generic update classback.
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class UpdateSingleFileGenericTest extends UpdateSingleFileAbstractTest<IOutItemAllFormats> {
    private final ArchiveFormat archiveFormat;
    @Override
    protected IOutCreateCallback<IOutItemAllFormats> getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog)
            throws SevenZipException {
        return new ArchiveUpdateCallbackGeneric(inArchive, changeLog);
    }

    @Parameters
    public static Collection<ArchiveFormat> getParameters() {
        return Arrays.asList(//
                ArchiveFormat.SEVEN_ZIP, //
                ArchiveFormat.BZIP2, //
                ArchiveFormat.GZIP, //
                ArchiveFormat.TAR, //
                ArchiveFormat.ZIP);
    }

    public UpdateSingleFileGenericTest(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    @Override
    protected ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }
}
