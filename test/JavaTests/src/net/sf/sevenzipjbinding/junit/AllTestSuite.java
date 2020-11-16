package net.sf.sevenzipjbinding.junit;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.sevenzipjbinding.junit.badarchive.GarbageArchiveFileTest;
import net.sf.sevenzipjbinding.junit.bug.CallMethodsOnClosedInStreamTest;
import net.sf.sevenzipjbinding.junit.bug.CallMethodsOnClosedOutStreamTest;
import net.sf.sevenzipjbinding.junit.bug.OpenMultipartCabWithNonVolumedCallbackTest;
import net.sf.sevenzipjbinding.junit.bug.RarPasswordToLongCrash;
import net.sf.sevenzipjbinding.junit.bug.SevenZipInTar;
import net.sf.sevenzipjbinding.junit.bug.Ticket18NullAsPassword;
import net.sf.sevenzipjbinding.junit.bug.WrongCRCGetterInSimpleInterface;
import net.sf.sevenzipjbinding.junit.common.ArchiveFormatTest;
import net.sf.sevenzipjbinding.junit.common.DeclareThrowsSevenZipExceptionTest;
import net.sf.sevenzipjbinding.junit.common.JUnitInitializationTest;
import net.sf.sevenzipjbinding.junit.common.TestBaseTest;
import net.sf.sevenzipjbinding.junit.compression.CompressExceptionGetConnectedArchiveTest;
import net.sf.sevenzipjbinding.junit.compression.CompressExceptionGetItemInformationTest;
import net.sf.sevenzipjbinding.junit.compression.CompressFeatureSetLevel;
import net.sf.sevenzipjbinding.junit.compression.CompressFeatureSetLevelRatioImpact;
import net.sf.sevenzipjbinding.junit.compression.CompressFeatureSetSolid;
import net.sf.sevenzipjbinding.junit.compression.CompressFeatureSetThreadCount;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFile7zPassHeaderTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFile7zPassNullTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFile7zPassTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFile7zTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileGZipTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileTarTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileZipPassNullTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileZipPassTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileZipTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFile7zPassHeaderTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFile7zPassNullTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFile7zPassTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFile7zTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFileTarTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFileZipPassNullTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFileZipPassTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFileZipTest;
import net.sf.sevenzipjbinding.junit.compression.CompressNonGenericSingleFile7zTest;
import net.sf.sevenzipjbinding.junit.compression.CompressNonGenericSingleFileBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.CompressNonGenericSingleFileGZipTest;
import net.sf.sevenzipjbinding.junit.compression.CompressNonGenericSingleFileTarTest;
import net.sf.sevenzipjbinding.junit.compression.CompressNonGenericSingleFileZipTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneCompressBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.StandaloneCompressGZipTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneCompressSevenZipTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneCompressTarTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneCompressZipTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateArchiveAddTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateArchiveRemoveTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateArchiveUpdateContentTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateArchiveUpdatePropertiesTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateNonGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateNonGenericBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateNonGenericGZipTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateNonGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.StandaloneUpdateNonGenericZipTest;
import net.sf.sevenzipjbinding.junit.compression.TraceCompressionTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGeneric7zPassHeaderTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGeneric7zPassNullTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGeneric7zPassTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGenericZipPassNullTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGenericZipPassTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGenericZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesNonGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesNonGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesNonGenericZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileGenericTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericGZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericZipTest;
import net.sf.sevenzipjbinding.junit.encoding.UnicodeFilenamesInArchive;
import net.sf.sevenzipjbinding.junit.initialization.InitializationDoesNotVerifyArtifactsTest;
import net.sf.sevenzipjbinding.junit.initialization.StandardInitializationTest;
import net.sf.sevenzipjbinding.junit.initialization.VersionTest;
import net.sf.sevenzipjbinding.junit.jbindingtools.CHeadCacheInStreamTest;
import net.sf.sevenzipjbinding.junit.jbindingtools.EnumTest;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest;
import net.sf.sevenzipjbinding.junit.jbindingtools.JBindingTest;
import net.sf.sevenzipjbinding.junit.jnitools.JNIToolsTest;
import net.sf.sevenzipjbinding.junit.jnitools.ParamSpecTest;
import net.sf.sevenzipjbinding.junit.misc.ArchiveWithTwoPasswordsTest;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength1;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength100;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength2;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength3;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength4;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength5;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength6;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength7;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength8;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength9;
import net.sf.sevenzipjbinding.junit.misc.ByteArrayStreamTest.ByteArrayStreamTestWithEmptyBuffer;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.NoReadLimitSeekCur;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.NoReadLimitSeekEnd;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.NoReadLimitSeekSet;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadMaxThreeBytesSeekCur;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadMaxThreeBytesSeekEnd;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadMaxThreeBytesSeekSet;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadMaxTwoBytesSeekCur;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadMaxTwoBytesSeekEnd;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadMaxTwoBytesSeekSet;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadSingleBytesSeekCur;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadSingleBytesSeekEnd;
import net.sf.sevenzipjbinding.junit.misc.VolumedArchiveInStreamTest.ReadSingleBytesSeekSet;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileArjTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCabTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCabVolumeTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCabVolumeWithoutVolumedTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileCpioTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileDebTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileFatTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileIsoTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileLzhTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileNtfsTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRar5HeaderPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRar5PassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRar5Test;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRar5VolumeHeaderPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRar5VolumePassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileRar5VolumeTest;
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
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileWimTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileXarTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileZipPassTest;
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileZipTest;
import net.sf.sevenzipjbinding.junit.performance.HeadCacheOnAutodetectionTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileArjTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileBzip2Test;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileCabTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileCabVolumeTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileChmTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileCpioTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileDebTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileFatTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileGzipTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileIsoTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileLzhTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileLzmaTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileNsisSolidTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileNsisTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileNtfsTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5HeaderPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5HeaderPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5PassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5PassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5Test;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5VolumeHeaderPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5VolumePassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5VolumePassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileRar5VolumeTest;
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
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileWimTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileXarTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZipPassCallbackTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZipPassTest;
import net.sf.sevenzipjbinding.junit.singlefile.ExtractSingleFileZipTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressGenericTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressMessageTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressNonGeneric7zTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressNonGenericBZip2Test;
import net.sf.sevenzipjbinding.junit.snippets.CompressNonGenericGZipTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressNonGenericTarTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressNonGenericZipTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressWithErrorTest;
import net.sf.sevenzipjbinding.junit.snippets.CompressWithPasswordTest;
import net.sf.sevenzipjbinding.junit.snippets.ExtractItemsTest;
import net.sf.sevenzipjbinding.junit.snippets.FirstStepsSimpleSnippets;
import net.sf.sevenzipjbinding.junit.snippets.GetNumberOfItemInArchive;
import net.sf.sevenzipjbinding.junit.snippets.ListItemsTest;
import net.sf.sevenzipjbinding.junit.snippets.OpenMultipartArchive7zTest;
import net.sf.sevenzipjbinding.junit.snippets.OpenMultipartArchiveRarTest;
import net.sf.sevenzipjbinding.junit.snippets.PrintCountOfItemsTest;
import net.sf.sevenzipjbinding.junit.snippets.SevenZipJBindingInitCheckTest;
import net.sf.sevenzipjbinding.junit.snippets.UpdateAddRemoveItemsTest;
import net.sf.sevenzipjbinding.junit.snippets.UpdateAlterItemsTest;
import net.sf.sevenzipjbinding.junit.tools.TypeVariableResolverTest;


/**
 * Suite builder for all JUnit tests.
 *
 * @author Boris Brodski
 * @since 4.65-1
 */
public class AllTestSuite extends TestSuite {
    static Class<?>[] commonTests = { //
            ArchiveFormatTest.class, //
            CHeadCacheInStreamTest.class, //
            DeclareThrowsSevenZipExceptionTest.class, //
            ExceptionHandlingTest.class, //
            HeadCacheOnAutodetectionTest.class, //
            JUnitInitializationTest.class, //
            TestBaseTest.class, //
            VersionTest.class, //
    };

    static Class<?>[] badArchiveTests = { //
    /*    */GarbageArchiveFileTest.class //
    };

    static Class<?>[] encodingArchiveTests = { //
            /*    */UnicodeFilenamesInArchive.class, //
    };

    static Class<?>[] bugArchiveTests = { //
    /*    */RarPasswordToLongCrash.class, //
            WrongCRCGetterInSimpleInterface.class, //
            Ticket18NullAsPassword.class, //
            SevenZipInTar.class, //
            CallMethodsOnClosedInStreamTest.class, //
            CallMethodsOnClosedOutStreamTest.class, //
    };

    static Class<?>[] multipleFilesTests = { //
    /*    */ExtractMultipleFileArjTest.class, //
            ExtractMultipleFileCabTest.class, //
            ExtractMultipleFileCabVolumeTest.class, //
            ExtractMultipleFileCabVolumeWithoutVolumedTest.class, //
            ExtractMultipleFileCpioTest.class, //
            ExtractMultipleFileDebTest.class, //
            ExtractMultipleFileFatTest.class, //
            ExtractMultipleFileIsoTest.class, //
            ExtractMultipleFileLzhTest.class, //
            ExtractMultipleFileNtfsTest.class, //
            ExtractMultipleFileRar5HeaderPassTest.class, //
            ExtractMultipleFileRar5PassTest.class, //
            ExtractMultipleFileRar5Test.class, //
            ExtractMultipleFileRar5VolumeHeaderPassTest.class, //
            ExtractMultipleFileRar5VolumePassTest.class, //
            ExtractMultipleFileRar5VolumeTest.class, //
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
            ExtractMultipleFileWimTest.class, //
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
            ExtractSingleFileFatTest.class, //
            ExtractSingleFileGzipTest.class, //
            ExtractSingleFileIsoTest.class, //
            ExtractSingleFileLzhTest.class, //
            ExtractSingleFileLzmaTest.class, //
            ExtractSingleFileNsisSolidTest.class, //
            ExtractSingleFileNsisTest.class, //
            ExtractSingleFileNtfsTest.class, //
            ExtractSingleFileRar5HeaderPassCallbackTest.class, //
            ExtractSingleFileRar5HeaderPassTest.class, //
            ExtractSingleFileRar5PassCallbackTest.class, //
            ExtractSingleFileRar5PassTest.class, //
            ExtractSingleFileRar5Test.class, //
            ExtractSingleFileRar5VolumeHeaderPassCallbackTest.class, //
            ExtractSingleFileRar5VolumePassCallbackTest.class, //
            ExtractSingleFileRar5VolumePassTest.class, //
            ExtractSingleFileRar5VolumeTest.class, //
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
            ExtractSingleFileWimTest.class, //
            ExtractSingleFileXarTest.class, //
            ExtractSingleFileZTest.class, //
            ExtractSingleFileZipPassCallbackTest.class, //
            ExtractSingleFileZipPassTest.class, //
            ExtractSingleFileZipTest.class, //
            OpenMultipartCabWithNonVolumedCallbackTest.class, // // TODO extract to a special group
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
            CompressMessageTest.class, //
            CompressNonGenericZipTest.class, //
            CompressNonGeneric7zTest.class, //
            CompressGenericTest.class, //
            CompressNonGenericBZip2Test.class, //
            CompressNonGenericGZipTest.class, //
            CompressNonGenericTarTest.class, //
            CompressWithErrorTest.class, //
            CompressWithPasswordTest.class, //
            UpdateAddRemoveItemsTest.class, //
            UpdateAlterItemsTest.class, //
    };

    static Class<?>[] toolsTests = { //
            ByteArrayStreamTestWithBufferLength1.class, //
            ByteArrayStreamTestWithBufferLength100.class, //
            ByteArrayStreamTestWithBufferLength2.class, //
            ByteArrayStreamTestWithBufferLength3.class, //
            ByteArrayStreamTestWithBufferLength4.class, //
            ByteArrayStreamTestWithBufferLength5.class, //
            ByteArrayStreamTestWithBufferLength6.class, //
            ByteArrayStreamTestWithBufferLength7.class, //
            ByteArrayStreamTestWithBufferLength8.class, //
            ByteArrayStreamTestWithBufferLength9.class, //
            ByteArrayStreamTestWithEmptyBuffer.class, //
            EnumTest.class, //
            JBindingTest.class, //
            JNIToolsTest.class, //
            NoReadLimitSeekCur.class, //
            NoReadLimitSeekEnd.class, //
            NoReadLimitSeekSet.class, //
            ParamSpecTest.class, //
            ReadMaxThreeBytesSeekCur.class, //
            ReadMaxThreeBytesSeekEnd.class, //
            ReadMaxThreeBytesSeekSet.class, //
            ReadMaxTwoBytesSeekCur.class, //
            ReadMaxTwoBytesSeekEnd.class, //
            ReadMaxTwoBytesSeekSet.class, //
            ReadSingleBytesSeekCur.class, //
            ReadSingleBytesSeekEnd.class, //
            ReadSingleBytesSeekSet.class, //
            TypeVariableResolverTest.class, //
    };
    static Class<?>[] compressionTests = { //
            CompressExceptionGetItemInformationTest.class, //
            CompressExceptionGetConnectedArchiveTest.class, //

            TraceCompressionTest.class, //
            StandaloneCompressZipTest.class, //
            StandaloneCompressBZip2Test.class, //
            StandaloneCompressGZipTest.class, //
            StandaloneCompressSevenZipTest.class, //
            StandaloneCompressTarTest.class, //
            StandaloneUpdateArchiveAddTest.class, //
            StandaloneUpdateArchiveRemoveTest.class, //
            StandaloneUpdateArchiveUpdateContentTest.class, //
            StandaloneUpdateArchiveUpdatePropertiesTest.class, //
            StandaloneUpdateNonGeneric7zTest.class, //
            StandaloneUpdateNonGenericTarTest.class, //
            StandaloneUpdateNonGenericZipTest.class, //
            StandaloneUpdateNonGenericBZip2Test.class, //
            StandaloneUpdateNonGenericGZipTest.class, //

            CompressFeatureSetLevel.class, //
            CompressFeatureSetLevelRatioImpact.class, //
            CompressFeatureSetSolid.class, //
            CompressFeatureSetThreadCount.class, //

            CompressGenericSingleFile7zPassHeaderTest.class, //
            CompressGenericSingleFile7zPassNullTest.class, //
            CompressGenericSingleFile7zPassTest.class, //
            CompressGenericSingleFile7zTest.class, //
            CompressGenericSingleFileBZip2Test.class, //
            CompressGenericSingleFileGZipTest.class, //
            CompressGenericSingleFileTarTest.class, //
            CompressGenericSingleFileZipPassNullTest.class, //
            CompressGenericSingleFileZipPassTest.class, //
            CompressGenericSingleFileZipTest.class, //
            CompressMultipleFile7zPassHeaderTest.class, //
            CompressMultipleFile7zPassNullTest.class, //
            CompressMultipleFile7zPassTest.class, //
            CompressMultipleFile7zTest.class, //
            CompressMultipleFileZipPassNullTest.class, //
            CompressMultipleFileZipPassTest.class, //

            CompressMultipleFileTarTest.class, //
            CompressMultipleFileZipTest.class, //
            CompressNonGenericSingleFile7zTest.class, //
            CompressNonGenericSingleFileBZip2Test.class, //
            CompressNonGenericSingleFileGZipTest.class, //
            CompressNonGenericSingleFileTarTest.class, //
            CompressNonGenericSingleFileZipTest.class, //

            UpdateMultipleFilesGeneric7zPassHeaderTest.class, //
            UpdateMultipleFilesGeneric7zPassNullTest.class, //
            UpdateMultipleFilesGeneric7zPassTest.class, //
            UpdateMultipleFilesGeneric7zTest.class, //
            UpdateMultipleFilesGenericTarTest.class, //
            UpdateMultipleFilesGenericZipPassNullTest.class, //
            UpdateMultipleFilesGenericZipPassTest.class, //
            UpdateMultipleFilesGenericZipTest.class, //
            UpdateMultipleFilesNonGeneric7zTest.class, //
            UpdateMultipleFilesNonGenericTarTest.class, //
            UpdateMultipleFilesNonGenericZipTest.class, //
            UpdateSingleFileGenericTest.class, //
            UpdateSingleFileNonGeneric7zTest.class, //
            UpdateSingleFileNonGenericBZip2Test.class, //
            UpdateSingleFileNonGenericGZipTest.class, //
            UpdateSingleFileNonGenericTarTest.class, //
            UpdateSingleFileNonGenericZipTest.class, //

    };
    static Class<?>[] miscTests = { //
            ArchiveWithTwoPasswordsTest.class, //
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
        tests.put("Compression tests", compressionTests);
        tests.put("Misc tests", miscTests);
    }

    public static Test suite() throws Exception {
        setSystemPropretyIfNotAlready(TestConfiguration.TEST_PARAM__TRACE, "false");

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

    static void setSystemPropretyIfNotAlready(String name, String value) {
        if (System.getProperty(name) == null) {
            System.setProperty(name, value);
        }
    }
}
