package net.sf.sevenzipjbinding.junit.compression;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutUpdateCallbackBase;

/**
 * Base class for all update single file tests using non-generic update classback.
 *
 * @author Boris Brodski
 * @version 9.13-2.00
 */
public abstract class UpdateSingleFileGenericAbstractTest extends UpdateSingleFileAbstractTest {
    @Override
    protected IOutUpdateCallbackBase getOutUpdateCallbackBase(IInArchive inArchive, ChangeLog changeLog) {
        return new ArchiveUpdateCallbackGeneric(inArchive, changeLog);
    }
}
