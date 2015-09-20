package net.sf.sevenzipjbinding.junit;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.sevenzipjbinding.junit.badarchive.GarbageArchiveFileTest;
import net.sf.sevenzipjbinding.junit.bug.OpenMultipartCabWithNonVolumedCallbackTest;
import net.sf.sevenzipjbinding.junit.bug.RarPasswordToLongCrash;
import net.sf.sevenzipjbinding.junit.bug.WrongCRCGetterInSimpleInterface;
import net.sf.sevenzipjbinding.junit.compression.CompressExceptionGetConnectedArchiveTest;
import net.sf.sevenzipjbinding.junit.compression.CompressExceptionGetItemInformationTest;
import net.sf.sevenzipjbinding.junit.compression.CompressFeatureSetLevel;
import net.sf.sevenzipjbinding.junit.compression.CompressFeatureSetSolid;
import net.sf.sevenzipjbinding.junit.compression.CompressFeatureSetThreadCount;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFile7zTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileGZipTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileTarTest;
import net.sf.sevenzipjbinding.junit.compression.CompressGenericSingleFileZipTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFile7zTest;
import net.sf.sevenzipjbinding.junit.compression.CompressMultipleFileTarTest;
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
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesGenericZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesNonGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesNonGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateMultipleFilesNonGenericZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileGenericBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileGenericGZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileGenericZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGeneric7zTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericBZip2Test;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericGZipTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericTarTest;
import net.sf.sevenzipjbinding.junit.compression.UpdateSingleFileNonGenericZipTest;
import net.sf.sevenzipjbinding.junit.encoding.UnicodeFilenamesInArchive.UnicodeFilenamesInArchive7z;
import net.sf.sevenzipjbinding.junit.encoding.UnicodeFilenamesInArchive.UnicodeFilenamesInArchiveZip;
import net.sf.sevenzipjbinding.junit.initialization.InitializationDoesNotVerifyArtifactsTest;
import net.sf.sevenzipjbinding.junit.initialization.StandardInitializationTest;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth0MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth0MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth0MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth0MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth1MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth1MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth1MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth1MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth2MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth2MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth2MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width1Depth2MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth0MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth0MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth0MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth0MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth1MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth1MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth1MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth1MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth2MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth2MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth2MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width2Depth2MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth0MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth0MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth0MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth0MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth1MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth1MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth1MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth1MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth2MtWidth0;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth2MtWidth1;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth2MtWidth2;
import net.sf.sevenzipjbinding.junit.jbindingtools.ExceptionHandlingTest.Width3Depth2MtWidth3;
import net.sf.sevenzipjbinding.junit.jbindingtools.JBindingTest;
import net.sf.sevenzipjbinding.junit.jnitools.JNIToolsTest;
import net.sf.sevenzipjbinding.junit.jnitools.ParamSpecTest;
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
import net.sf.sevenzipjbinding.junit.multiplefiles.ExtractMultipleFileWimTest;
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
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength1;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength100;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength2;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength3;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength4;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength5;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength6;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength7;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength8;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithBufferLength9;
import net.sf.sevenzipjbinding.junit.tools.ByteArrayStreamTest.ByteArrayStreamTestWithEmptyBuffer;
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
 * @since 4.65-1
 */
public class AllTestSuite extends TestSuite {
    static Class<?>[] commonTests = { //
    /*    */JUnitInitializationTest.class, //
            ArchiveFormatTest.class, //
            DeclareThrowsSevenZipExceptionTest.class, //
            PTest.class, //
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
            WrongCRCGetterInSimpleInterface.class
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
            ExtractSingleFileWimTest.class, //
            ExtractSingleFileXarTest.class, //
            ExtractSingleFileZipPassCallbackTest.class, //
            ExtractSingleFileZipPassTest.class, //
            ExtractSingleFileZipTest.class, //
            ExtractSingleFileZTest.class, //
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
            UpdateAddRemoveItemsTest.class, //
            UpdateAlterItemsTest.class, //
            CompressWithErrorTest.class, //
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
            ByteArrayStreamTestWithEmptyBuffer.class, //
            ByteArrayStreamTestWithBufferLength1.class, //
            ByteArrayStreamTestWithBufferLength2.class, //
            ByteArrayStreamTestWithBufferLength3.class, //
            ByteArrayStreamTestWithBufferLength4.class, //
            ByteArrayStreamTestWithBufferLength5.class, //
            ByteArrayStreamTestWithBufferLength6.class, //
            ByteArrayStreamTestWithBufferLength7.class, //
            ByteArrayStreamTestWithBufferLength8.class, //
            ByteArrayStreamTestWithBufferLength9.class, //
            ByteArrayStreamTestWithBufferLength100.class, //
            JBindingTest.class, //
            Width1Depth0MtWidth0.class, //
            Width1Depth1MtWidth0.class, //
            Width1Depth2MtWidth0.class, //
            Width2Depth0MtWidth0.class, //
            Width2Depth1MtWidth0.class, //
            Width2Depth2MtWidth0.class, //
            Width3Depth0MtWidth0.class, //
            Width3Depth1MtWidth0.class, //
            Width3Depth2MtWidth0.class, //
            Width1Depth0MtWidth1.class, //
            Width1Depth1MtWidth1.class, //
            Width1Depth2MtWidth1.class, //
            Width2Depth0MtWidth1.class, //
            Width2Depth1MtWidth1.class, //
            Width2Depth2MtWidth1.class, //
            Width3Depth0MtWidth1.class, //
            Width3Depth1MtWidth1.class, //
            Width3Depth2MtWidth1.class, //
            Width1Depth0MtWidth2.class, //
            Width1Depth1MtWidth2.class, //
            Width1Depth2MtWidth2.class, //
            Width2Depth0MtWidth2.class, //
            Width2Depth1MtWidth2.class, //
            Width2Depth2MtWidth2.class, //
            Width3Depth0MtWidth2.class, //
            Width3Depth1MtWidth2.class, //
            Width3Depth2MtWidth2.class, //
            Width1Depth0MtWidth3.class, //
            Width1Depth1MtWidth3.class, //
            Width1Depth2MtWidth3.class, //
            Width2Depth0MtWidth3.class, //
            Width2Depth1MtWidth3.class, //
            Width2Depth2MtWidth3.class, //
            Width3Depth0MtWidth3.class, //
            Width3Depth1MtWidth3.class, //
            Width3Depth2MtWidth3.class, //
            JNIToolsTest.class, //
            ParamSpecTest.class, //
    };
    static Class<?>[] compressionTests = { //
    /*    */CompressExceptionGetItemInformationTest.CompressException7zTest.class, //
            CompressExceptionGetItemInformationTest.CompressExceptionBZip2Test.class, //
            CompressExceptionGetItemInformationTest.CompressExceptionGZipTest.class, //
            CompressExceptionGetItemInformationTest.CompressExceptionTarTest.class, //
            CompressExceptionGetItemInformationTest.CompressExceptionZipTest.class, //
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

            CompressFeatureSetLevel.CompressionFeatureSetLevelBZip2.class, //
            CompressFeatureSetLevel.CompressionFeatureSetLevelGZip.class, //
            CompressFeatureSetLevel.CompressionFeatureSetLevelSevenZip.class, //
            CompressFeatureSetLevel.CompressionFeatureSetLevelZip.class, //
            CompressFeatureSetSolid.CompressionFeatureSetSolidSevenZip.class, //
            CompressFeatureSetThreadCount.CompressionFeatureSetLevelSevenZip.class, //

            CompressGenericSingleFile7zTest.class, //
            CompressGenericSingleFileBZip2Test.class, //
            CompressGenericSingleFileGZipTest.class, //
            CompressGenericSingleFileTarTest.class, //
            CompressGenericSingleFileZipTest.class, //
            CompressMultipleFile7zTest.class, //
            CompressMultipleFileTarTest.class, //
            CompressMultipleFileZipTest.class, //
            CompressNonGenericSingleFile7zTest.class, //
            CompressNonGenericSingleFileBZip2Test.class, //
            CompressNonGenericSingleFileGZipTest.class, //
            CompressNonGenericSingleFileTarTest.class, //
            CompressNonGenericSingleFileZipTest.class, //

            UpdateMultipleFilesGeneric7zTest.class, //
            UpdateMultipleFilesGenericTarTest.class, //
            UpdateMultipleFilesGenericZipTest.class, //
            UpdateMultipleFilesNonGeneric7zTest.class, //
            UpdateMultipleFilesNonGenericTarTest.class, //
            UpdateMultipleFilesNonGenericZipTest.class, //
            UpdateSingleFileGeneric7zTest.class, //
            UpdateSingleFileGenericBZip2Test.class, //
            UpdateSingleFileGenericGZipTest.class, //
            UpdateSingleFileGenericTarTest.class, //
            UpdateSingleFileGenericZipTest.class, //
            UpdateSingleFileNonGeneric7zTest.class, //
            UpdateSingleFileNonGenericBZip2Test.class, //
            UpdateSingleFileNonGenericGZipTest.class, //
            UpdateSingleFileNonGenericTarTest.class, //
            UpdateSingleFileNonGenericZipTest.class, //

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
