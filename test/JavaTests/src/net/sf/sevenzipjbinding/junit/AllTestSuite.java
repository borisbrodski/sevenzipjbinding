package net.sf.sevenzipjbinding.junit;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.sevenzipjbinding.junit.badarchive.GarbageArchiveFileTest;
import net.sf.sevenzipjbinding.junit.bug.RarPasswordToLongCrash;
import net.sf.sevenzipjbinding.junit.encoding.UnicodeFilenamesInArchive.UnicodeFilenamesInArchive7z;
import net.sf.sevenzipjbinding.junit.encoding.UnicodeFilenamesInArchive.UnicodeFilenamesInArchiveZip;
import net.sf.sevenzipjbinding.junit.initialization.InitializationDoesNotVerifyArtifactsTest;
import net.sf.sevenzipjbinding.junit.initialization.StandardInitializationTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileArjTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCabTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCabVolumeTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCabVolumeWithoutVolumedTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCpioTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileDebTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileIsoTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileLzhTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRarHeaderPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRarPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRarTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRarVolumeHeaderPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRarVolumePassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRarVolumeTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileSevenZipHeaderPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileSevenZipPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileSevenZipTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileSevenZipVolumeHeaderPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileSevenZipVolumePassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileSevenZipVolumeTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileTarTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileUdfTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileXarTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileZipPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileZipTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileArjTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileBzip2Test;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileCabTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileCabVolumeTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileChmTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileCpioTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileDebTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileGzipTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileIsoTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileLzhTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileLzmaTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileNsisSolidTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileNsisTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarHeaderPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarHeaderPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarVolumeHeaderPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarVolumePassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarVolumePassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRarVolumeTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRpmTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipHeaderPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipHeaderPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipVolumeHeaderPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipVolumeHeaderPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipVolumePassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipVolumePassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileSevenZipVolumeTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileTarTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileUdfTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileXarTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZipPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZipPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZipTest;
import net.sf.sevenzipjbinding.junit.snippets.ExtractItemsTest;
import net.sf.sevenzipjbinding.junit.snippets.FirstStepsSimpleSnippets;
import net.sf.sevenzipjbinding.junit.snippets.GetNumberOfItemInArchive;
import net.sf.sevenzipjbinding.junit.snippets.ListItemsTest;
import net.sf.sevenzipjbinding.junit.snippets.OpenMultipartArchive7zTest;
import net.sf.sevenzipjbinding.junit.snippets.OpenMultipartArchiveRarTest;
import net.sf.sevenzipjbinding.junit.snippets.PrintCountOfItemsTest;
import net.sf.sevenzipjbinding.junit.snippets.SevenZipJBindingInitCheckTest;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.NoReadLimitSeekCur;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.NoReadLimitSeekEnd;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.NoReadLimitSeekSet;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadMaxThreeBytesSeekCur;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadMaxThreeBytesSeekEnd;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadMaxThreeBytesSeekSet;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadMaxTwoBytesSeekCur;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadMaxTwoBytesSeekEnd;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadMaxTwoBytesSeekSet;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadSingleBytesSeekCur;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadSingleBytesSeekEnd;
import net.sf.sevenzipjbinding.junit.tools.VolumedArchiveInStreamTest.ReadSingleBytesSeekSet;

/**
 * Suite builder for all JUnit tests.
 *
 * @author Boris Brodski
 * @version 4.65-1
 */
public class AllTestSuite extends TestSuite {
    static Class<?>[] commonTests = { //
    /*    */JUnitInitializationTest.class //
    };
    static Class<?>[] badArchiveTests = { //
    /*    */GarbageArchiveFileTest.class //
    };

    static Class<?>[] encodingArchiveTests = { //
    /*    */UnicodeFilenamesInArchive7z.class, //
            UnicodeFilenamesInArchiveZip.class, //
    };

    static Class<?>[] bugArchiveTests = { //
    /*    */RarPasswordToLongCrash.class, //
    };

    static Class<?>[] multipleFilesTests = { //
    /*    */ExtractMultipleFileArjTest.class, //
            ExtractMultipleFileCabTest.class, //
            ExtractMultipleFileCabVolumeTest.class, //
            ExtractMultipleFileCabVolumeWithoutVolumedTest.class, //
            ExtractMultipleFileCpioTest.class, //
            ExtractMultipleFileDebTest.class, //
            ExtractMultipleFileIsoTest.class, //
            ExtractMultipleFileLzhTest.class, //
            ExtractMultipleFileRarHeaderPassTest.class, //
            ExtractMultipleFileRarPassTest.class, //
            ExtractMultipleFileRarTest.class, //
            ExtractMultipleFileRarVolumeHeaderPassTest.class, //
            ExtractMultipleFileRarVolumePassTest.class, //
            ExtractMultipleFileRarVolumeTest.class, //
            ExtractMultipleFileSevenZipHeaderPassTest.class, //
            ExtractMultipleFileSevenZipPassTest.class, //
            ExtractMultipleFileSevenZipTest.class, //
            ExtractMultipleFileSevenZipVolumeHeaderPassTest.class, //
            ExtractMultipleFileSevenZipVolumePassTest.class, //
            ExtractMultipleFileSevenZipVolumeTest.class, //
            ExtractMultipleFileTarTest.class, //
            ExtractMultipleFileUdfTest.class, //
            ExtractMultipleFileXarTest.class, //
            ExtractMultipleFileZipPassTest.class, //
            ExtractMultipleFileZipTest.class, //
    };
    static Class<?>[] singleFileTests = { //
    /*    */ExtractSingleFileArjTest.class, //
            ExtractSingleFileBzip2Test.class, //
            ExtractSingleFileCabTest.class, //
            ExtractSingleFileCabVolumeTest.class, //
            ExtractSingleFileChmTest.class, //
            ExtractSingleFileCpioTest.class, //
            ExtractSingleFileDebTest.class, //
            ExtractSingleFileGzipTest.class, //
            ExtractSingleFileIsoTest.class, //
            ExtractSingleFileLzhTest.class, //
            ExtractSingleFileLzmaTest.class, //
            ExtractSingleFileNsisSolidTest.class, //
            ExtractSingleFileNsisTest.class, //
            ExtractSingleFileRarHeaderPassCallbackTest.class, //
            ExtractSingleFileRarHeaderPassTest.class, //
            ExtractSingleFileRarPassCallbackTest.class, //
            ExtractSingleFileRarPassTest.class, //
            ExtractSingleFileRarTest.class, //
            ExtractSingleFileRarVolumeHeaderPassCallbackTest.class, //
            ExtractSingleFileRarVolumePassCallbackTest.class, //
            ExtractSingleFileRarVolumePassTest.class, //
            ExtractSingleFileRarVolumeTest.class, //
            ExtractSingleFileRpmTest.class, //
            ExtractSingleFileSevenZipHeaderPassCallbackTest.class, //
            ExtractSingleFileSevenZipHeaderPassTest.class, //
            ExtractSingleFileSevenZipPassCallbackTest.class, //
            ExtractSingleFileSevenZipPassTest.class, //
            ExtractSingleFileSevenZipTest.class, //
            ExtractSingleFileSevenZipVolumeHeaderPassCallbackTest.class, //
            ExtractSingleFileSevenZipVolumeHeaderPassTest.class, //
            ExtractSingleFileSevenZipVolumePassCallbackTest.class, //
            ExtractSingleFileSevenZipVolumePassTest.class, //
            ExtractSingleFileSevenZipVolumeTest.class, //
            ExtractSingleFileTarTest.class, //
            ExtractSingleFileUdfTest.class, //
            ExtractSingleFileXarTest.class, //
            ExtractSingleFileZipPassCallbackTest.class, //
            ExtractSingleFileZipPassTest.class, //
            ExtractSingleFileZipTest.class, //
            ExtractSingleFileZTest.class, //
    };
    static Class<?>[] snippetsTests = { //
    /*    */GetNumberOfItemInArchive.class, //
            ExtractItemsTest.class, //
            FirstStepsSimpleSnippets.class, //
            ListItemsTest.class, //
            OpenMultipartArchive7zTest.class, //
            OpenMultipartArchiveRarTest.class, //
            PrintCountOfItemsTest.class, //
            SevenZipJBindingInitCheckTest.class, //
    };

    static Class<?>[] toolsTests = { //
    /*    */NoReadLimitSeekSet.class, //
            NoReadLimitSeekCur.class, //
            NoReadLimitSeekEnd.class, //
            ReadSingleBytesSeekSet.class, //
            ReadSingleBytesSeekCur.class, //
            ReadSingleBytesSeekEnd.class, //
            ReadMaxTwoBytesSeekSet.class, //
            ReadMaxTwoBytesSeekCur.class, //
            ReadMaxTwoBytesSeekEnd.class, //
            ReadMaxThreeBytesSeekSet.class, //
            ReadMaxThreeBytesSeekCur.class, //
            ReadMaxThreeBytesSeekEnd.class, //
    };
    static Class<?>[] initStdTests = { //
    /*    */StandardInitializationTest.class, //
    };
    static Class<?>[] initVerifyTests = { //
    /*    */InitializationDoesNotVerifyArtifactsTest.class, //
    };
    static SortedMap<String, Class<?>[]> tests = new TreeMap<String, Class<?>[]>();

    static {
        tests.put("Common tests", commonTests);
        tests.put("Init tests (Std)", initStdTests);
        tests.put("Init tests (Verify)", initVerifyTests);
        tests.put("Tools tests", toolsTests);
        tests.put("Snippets tests", snippetsTests);
        tests.put("Encoding tests", encodingArchiveTests);
        tests.put("Bug report tests", bugArchiveTests);
        tests.put("Single file tests", singleFileTests);
        tests.put("Multiple files tests", multipleFilesTests);
        tests.put("Bad archive tests", badArchiveTests);
    }

    public static Test suite() throws Exception {
        String singleBundle = System.getProperty("SINGLEBUNDLE");
        if (singleBundle != null) {
            singleBundle = singleBundle.replaceAll("[-0-9]|  +", "").trim();
            Class<?>[] classes = tests.get(singleBundle);
            if (classes == null) {
                throw new Exception("No tests found for test bundle: '" + singleBundle + "'");
            }
            TestSuite testSuite = new TestSuite(singleBundle);
            for (Class<?> testClass : classes) {
                testSuite.addTest(new JUnit4TestAdapter(testClass));
            }
            return testSuite;
        }

        TestSuite allTestSuite = new TestSuite("All tests");
        for (String testBundle : tests.keySet()) {
            TestSuite testSuite = new TestSuite(testBundle);
            for (Class<?> testClass : tests.get(testBundle)) {
                testSuite.addTest(new JUnit4TestAdapter(testClass));
            }
            allTestSuite.addTest(testSuite);
        }

        return allTestSuite;
    }
}
