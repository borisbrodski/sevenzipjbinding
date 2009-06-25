PROJECT(SevenZipJBindingCPP)

INCLUDE(../cmake/FindJNIExtended.cmake)

GET_PROPERTY(SEVENZIP_JBINDING_JAR  GLOBAL PROPERTY SEVENZIP_JBINDING_JAR) 

IF (CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")
    SET(USE_MINGW 1)
    SET(USE_CYGWIN 1)
    MESSAGE("Compiling on cygwin using -mnocyg (MinGW mode)")
ELSE(CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")
    MESSAGE("Compiling on posix system")
ENDIF(CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")

IF(JAVA_INCLUDE_PATH) 
    MESSAGE(STATUS "Java found" ) 
ELSE(JAVA_INCLUDE_PATH) 
    MESSAGE(WARNING " Java jni.h not found" ) 
ENDIF(JAVA_INCLUDE_PATH) 

IF(HAVE_WCHAR_H)
  OPTION(HAVE_UNICODE_WCHAR "Enable Unicode Support" YES)
  MARK_AS_ADVANCED(HAVE_UNICODE_WCHAR)
ELSE(HAVE_WCHAR_H)
  SET(HAVE_UNICODE_WCHAR 0)
ENDIF(HAVE_WCHAR_H)

SET(HAVE_PTHREADS 0)
FIND_PACKAGE(Threads)
IF(WIN32 OR CMAKE_USE_PTHREADS_INIT)
  SET(HAVE_PTHREADS 1)
ENDIF(WIN32 OR CMAKE_USE_PTHREADS_INIT)


SET(P7ZIP_SRC ../p7zip)

include_directories(/usr/include)
include_directories(/usr/include/c++/4.3)
include_directories(.)
include_directories(${JAVA_INCLUDE_PATH})
include_directories(${JAVA_INCLUDE_PATH2})
IF(NOT USE_CYGWIN)
    include_directories(${P7ZIP_SRC}/CPP/myWindows)
ENDIF(NOT USE_CYGWIN)
include_directories(${P7ZIP_SRC}/CPP/)
IF(NOT USE_CYGWIN)
    include_directories(${P7ZIP_SRC}/CPP/include_windows)
ENDIF(NOT USE_CYGWIN)
include_directories(${PROJECT_BINARY_DIR}/javah)

IF(USE_CYGWIN)
# TODO Use properly way to set this gcc flag
add_definitions(-mno-cygwin)
ENDIF(USE_CYGWIN)

IF(USE_MINGW)
add_definitions(-DMINGW)
ENDIF(USE_MINGW)

add_definitions(
    -g3
#    -O
    -D_FILE_OFFSET_BITS=64
    -D_LARGEFILE_SOURCE
    -DNDEBUG
    -D_REENTRANT
    -DENV_UNIX
    -DCOMPRESS_MT
    -DCOMPRESS_BZIP2_MT
    -DCOMPRESS_MF_MT
    -DBREAK_HANDLER
    -DBENCH_MT
#    -DTRACE_OBJECTS_ON
#    -DTRACE_ON
)


IF(USE_MINGW)
SET(P7ZIP_SOURCE_FILES)
ELSE(USE_MINGW)
SET(P7ZIP_SOURCE_FILES
    ${P7ZIP_SRC}/CPP/myWindows/myGetTickCount.cpp
    ${P7ZIP_SRC}/CPP/myWindows/wine_date_and_time.cpp
    ${P7ZIP_SRC}/CPP/myWindows/myAddExeFlag.cpp
    ${P7ZIP_SRC}/CPP/myWindows/mySplitCommandLine.cpp
    
    ${P7ZIP_SRC}/CPP/Common/CommandLineParser.cpp
    ${P7ZIP_SRC}/CPP/Common/CRC.cpp
    ${P7ZIP_SRC}/CPP/Common/IntToString.cpp
    ${P7ZIP_SRC}/CPP/Common/ListFileUtils.cpp
    ${P7ZIP_SRC}/CPP/Common/StdInStream.cpp
    ${P7ZIP_SRC}/CPP/Common/StdOutStream.cpp
    ${P7ZIP_SRC}/CPP/Common/MyString.cpp
    ${P7ZIP_SRC}/CPP/Common/MyWindows.cpp
    ${P7ZIP_SRC}/CPP/Common/StringConvert.cpp
    ${P7ZIP_SRC}/CPP/Common/StringToInt.cpp
    ${P7ZIP_SRC}/CPP/Common/UTFConvert.cpp
    ${P7ZIP_SRC}/CPP/Common/MyVector.cpp
    ${P7ZIP_SRC}/CPP/Common/Wildcard.cpp
    
    ${P7ZIP_SRC}/CPP/Windows/Error.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileDir.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileFind.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileIO.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileName.cpp
    ${P7ZIP_SRC}/CPP/Windows/PropVariant.cpp
    ${P7ZIP_SRC}/CPP/Windows/PropVariantConversions.cpp
    ${P7ZIP_SRC}/CPP/Windows/Synchronization.cpp
    ${P7ZIP_SRC}/CPP/Windows/System.cpp
    ${P7ZIP_SRC}/CPP/Windows/Time.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Common/CreateCoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/FilePathAutoRename.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/FileStreams.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/FilterCoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/InBuffer.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/InOutTempBuffer.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/LimitedStreams.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/LockedStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/MemBlocks.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/MethodId.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/MethodProps.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/OffsetStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/OutBuffer.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/OutMemStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/ProgressMt.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/ProgressUtils.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/StreamBinder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/StreamObjects.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/StreamUtils.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/VirtThread.cpp
    
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/ArchiveCommandLine.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/ArchiveExtractCallback.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/ArchiveOpenCallback.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/DefaultName.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/EnumDirItems.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/Extract.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/ExtractingFilePath.cpp
    ${P7ZIP_SRC}/CPP/7zip/UI/Common/LoadCodecs.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/OpenArchive.cpp
    ${P7ZIP_SRC}/CPP/7zip/UI/Common/PropIDUtils.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/SetProperties.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/SortUtils.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/TempFiles.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/Update.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/UpdateAction.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/UpdateCallback.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/UpdatePair.cpp
    #${P7ZIP_SRC}/CPP/7zip/UI/Common/UpdateProduce.cpp
    ${P7ZIP_SRC}/CPP/7zip/UI/Console/ConsoleClose.cpp    
    ${P7ZIP_SRC}/CPP/7zip/Archive/ZHandler.cpp
    #${P7ZIP_SRC}/CPP/7zip/Archive/ArchiveExports.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/CoderMixer2.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/CoderMixer2MT.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/CrossThreadProgress.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/DummyOutStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/FindSignature.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/HandlerOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/InStreamWithCRC.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/ItemNameUtils.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/MultiStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/OutStreamWithCRC.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Common/ParseProperties.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zCompressionMode.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zDecode.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zEncode.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zExtract.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zFolderInStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zFolderOutStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zHandlerOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zHeader.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zProperties.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zSpecStream.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zUpdate.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/7z/7zRegister.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/BZip2/BZip2Handler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/BZip2/BZip2HandlerOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/BZip2/BZip2Update.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/BZip2/bz2Register.cpp

# TODO Need VolumeCallback   
#    ${P7ZIP_SRC}/CPP/7zip/Archive/Cab/CabBlockInStream.cpp
#    ${P7ZIP_SRC}/CPP/7zip/Archive/Cab/CabHandler.cpp
#    ${P7ZIP_SRC}/CPP/7zip/Archive/Cab/CabHeader.cpp
#    ${P7ZIP_SRC}/CPP/7zip/Archive/Cab/CabIn.cpp
#    ${P7ZIP_SRC}/CPP/7zip/Archive/Cab/CabRegister.cpp

# Chm

# Com ??

    ${P7ZIP_SRC}/CPP/7zip/Archive/ArjHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ArjDecoder1.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ArjDecoder2.cpp

    ${P7ZIP_SRC}/CPP/7zip/Archive/Cpio/CpioHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Cpio/CpioHeader.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Cpio/CpioIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Cpio/CpioRegister.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/GZip/GZipHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/GZip/GZipHandlerOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/GZip/GZipHeader.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/GZip/GZipIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/GZip/GZipOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/GZip/GZipRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/GZip/GZipUpdate.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/Hfs/HfsHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Hfs/HfsIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Hfs/HfsRegister.cpp

    ${P7ZIP_SRC}/CPP/7zip/Archive/Iso/IsoHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Iso/IsoHeader.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Iso/IsoIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Iso/IsoRegister.cpp

    ${P7ZIP_SRC}/CPP/7zip/Archive/Lzma/LzmaArcRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Lzma/LzmaFiltersDecode.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Lzma/LzmaHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Lzma/LzmaIn.cpp

# Nsis

    ${P7ZIP_SRC}/CPP/7zip/Archive/Rar/RarHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Rar/RarHeader.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Rar/RarIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Rar/RarItem.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Rar/RarRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Rar/RarVolumeInStream.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/Split/SplitHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Split/SplitRegister.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/Tar/TarHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Tar/TarHandlerOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Tar/TarHeader.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Tar/TarIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Tar/TarOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Tar/TarRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Tar/TarUpdate.cpp

# Udf

# Wim
    
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipAddCommon.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipHandler.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipHandlerOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipHeader.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipIn.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipItem.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipOut.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipUpdate.cpp
    ${P7ZIP_SRC}/CPP/7zip/Archive/Zip/ZipRegister.cpp
    
    ${P7ZIP_SRC}/CPP/7zip/Compress/Bcj2Coder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/Bcj2Register.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BcjCoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BcjRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BitlDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BranchCoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BranchMisc.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BranchRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ByteSwap.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ByteSwapRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BZip2Crc.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BZip2Decoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BZip2Encoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/BZip2Register.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/CopyCoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/CopyRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/Deflate64Register.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/DeflateDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/DeflateEncoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/DeflateRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ImplodeDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ImplodeHuffmanDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/LzmaDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/LzmaEncoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/LzmaRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/LzOutWindow.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/Lzx86Converter.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/LzxDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/PpmdDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/PpmdEncoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/PpmdRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/QuantumDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/Rar1Decoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/Rar2Decoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/Rar3Decoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/Rar3Vm.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/RarCodecsRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ShrinkDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/ZDecoder.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/LZMA_Alone/LzmaBench.cpp
    ${P7ZIP_SRC}/CPP/7zip/Compress/LZMA_Alone/LzmaBenchCon.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/7zAes.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/7zAesRegister.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/HmacSha1.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/MyAes.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/Pbkdf2HmacSha1.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/RandGen.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/Rar20Crypto.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/RarAes.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/Sha1.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/WzAes.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/ZipCrypto.cpp
    ${P7ZIP_SRC}/CPP/7zip/Crypto/ZipStrong.cpp
    ${P7ZIP_SRC}/C/Bra.c
    ${P7ZIP_SRC}/C/Bra86.c
    ${P7ZIP_SRC}/C/BraIA64.c
    ${P7ZIP_SRC}/C/BwtSort.c
    ${P7ZIP_SRC}/C/HuffEnc.c
    ${P7ZIP_SRC}/C/LzFind.c
    ${P7ZIP_SRC}/C/LzFindMt.c
    ${P7ZIP_SRC}/C/LzmaDec.c
    ${P7ZIP_SRC}/C/LzmaEnc.c
    ${P7ZIP_SRC}/C/Sort.c
    ${P7ZIP_SRC}/C/Threads.c
    ${P7ZIP_SRC}/C/Aes.c
    ${P7ZIP_SRC}/C/Sha256.c
    ${P7ZIP_SRC}/C/7zCrc.c
)
ENDIF(USE_MINGW)

IF(USE_MINGW)
SET(JBINDING_CPP_FILES
    Debug.cpp
)
ELSE(USE_MINGW)
SET(JBINDING_CPP_FILES
    Debug.cpp
#    idd_def.cpp
    JNITools.cpp
    JNICallState.cpp
    SevenZipException.cpp
    SevenZipJBinding.cpp
    UniversalArchiveOpenCallback.cpp
    JavaToCPP/JavaToCPPInArchiveImpl.cpp
    JavaToCPP/JavaToCPPSevenZip.cpp
    CPPToJava/CPPToJavaArchiveExtractCallback.cpp
    CPPToJava/CPPToJavaArchiveOpenCallback.cpp
    CPPToJava/CPPToJavaArchiveOpenVolumeCallback.cpp
    CPPToJava/CPPToJavaCryptoGetTextPassword.cpp
    CPPToJava/CPPToJavaInStream.cpp
    CPPToJava/CPPToJavaProgress.cpp
    CPPToJava/CPPToJavaSequentialInStream.cpp
    CPPToJava/CPPToJavaSequentialOutStream.cpp
)
ENDIF(USE_MINGW)

SET(JBINDING_JAVAH_H_FILES
    JavaToCPP/Java/net_sf_sevenzipjbinding_impl_InArchiveImpl.h
    JavaToCPP/Java/net_sf_sevenzipjbinding_SevenZip.h
)

#SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fvisibility=hidden -fvisibility-inlines-hidden")

SET(CMAKE_VERBOSE_MAKEFILE OFF)
IF(CMAKE_COMPILER_IS_GNUCC)
    SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fmessage-length=0")
ENDIF(CMAKE_COMPILER_IS_GNUCC)
IF(CMAKE_COMPILER_IS_GNUCXX)
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fmessage-length=0")
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

#ADD_EXECUTABLE(Client7z Client7z.cpp ${P7ZIP_SOURCE_FILES})
#TARGET_LINK_LIBRARIES(Client7z dl)

#<exec executable="${javah}">                                                                                 
#                        <arg value="-jni" />                                                                                 
#                        <arg value="-d" />                                                                                   
#                        <arg path="${workspace}/${sevenzip.javaheaders}" />                                                  
#                        <arg value="-force" />                                                                               
#                        <arg value="-classpath" />                                                                           
#                        <arg path="${basedir}/bin" />                                                                        
#                        <arg value="net.sf.sevenzip.SevenZip" />                                                             
#                        <arg value="net.sf.sevenzip.impl.InArchiveImpl" />                                                   
#                </exec>
SET(JAVAH_OUTPUT_DIR ${PROJECT_BINARY_DIR}/javah)
FILE(MAKE_DIRECTORY ${JAVAH_OUTPUT_DIR})
SET(JAVAH_JAVA_CLASS_LIST
    net.sf.sevenzipjbinding.SevenZip
    net.sf.sevenzipjbinding.impl.InArchiveImpl
)

add_custom_command(OUTPUT ${JBINDING_JAVAH_H_FILES}
                   COMMAND ${JAVA_JAVAH} -jni -d ${JAVAH_OUTPUT_DIR} ${JAVAH_JAVA_CLASS_LIST} 
                   DEPENDS ${SEVENZIP_JBINDING_JAR}
                   WORKING_DIRECTORY ${CMAKE_BINARY_DIR}/jbinding-java/bin
                   COMMENT "javah: generating h-files from java classes ${JAVAH_JAVA_CLASS_LIST}"
)

add_library(7-Zip-JBinding SHARED ${JBINDING_CPP_FILES} ${P7ZIP_SOURCE_FILES} ${JBINDING_JAVAH_H_FILES})

#GET_TARGET_PROPERTY(SEVENZIP_JBINDING_LIB 7-Zip-JBinding LOCATION)
#SET_PROPERTY(GLOBAL PROPERTY SEVENZIP_JBINDING_LIB ${SEVENZIP_JBINDING_LIB}) 

TARGET_LINK_LIBRARIES(7-Zip-JBinding dl c)

IF(USE_CYGWIN)
    SET_TARGET_PROPERTIES(7-Zip-JBinding PROPERTIES LINK_FLAGS "-mno-cygwin") # -lpthread -lstdc++ -lws2_32") #-Wl,--add-stdcall-alias
ENDIF(USE_CYGWIN)

IF(HAVE_PTHREADS) 
  TARGET_LINK_LIBRARIES(7-Zip-JBinding ${CMAKE_THREAD_LIBS_INIT}) 
ENDIF(HAVE_PTHREADS) 

