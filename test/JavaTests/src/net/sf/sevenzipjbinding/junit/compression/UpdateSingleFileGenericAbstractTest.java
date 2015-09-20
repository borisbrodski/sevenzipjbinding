package net.sf.sevenzipjbinding.junit.compression;

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
public abstract class UpdateSingleFileGenericAbstractTest extends UpdateSingleFileAbstractTest<IOutItemAllFormats> {

    @Override
    protected IOutCreateCallback<IOutItemAllFormats> getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog)
            throws SevenZipException {
        return new ArchiveUpdateCallbackGeneric(inArchive, changeLog);
    }
}
