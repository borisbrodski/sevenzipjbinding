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

    protected final void testSingleOrMultithreaded(boolean multithreaded, final RunnableThrowsException test)
            throws Exception {
        if (multithreaded) {
            runMultithreaded(new RunnableThrowsException() {
                public void run() throws Exception {
                    testSingleOrMultithreaded(false, test);
                }
            }, null);
        } else {

            for (int i = 0; i < SINGLE_TEST_REPEAT_COUNT; i++) {
                test.run();
            }
        }
    }

}
