PROJECT(SevenZipJBindingCPP)

GET_PROPERTY(SEVENZIPJBINDING_JAR  GLOBAL PROPERTY SEVENZIPJBINDING_JAR) 
GET_PROPERTY(MINGW                  GLOBAL PROPERTY MINGW) 
GET_PROPERTY(WINDOWS                GLOBAL PROPERTY WINDOWS) 

IF(NOT WINDOWS)
    SET(HAVE_PTHREADS No)
    FIND_PACKAGE(Threads)
    IF(CMAKE_USE_PTHREADS_INIT)
        SET(HAVE_PTHREADS Yes)
    ENDIF()
ENDIF()

IF (CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")
    MESSAGE(FATAL_ERROR "Cygwin build support using -mnocyg (MinGW mode) was cancelled.")
ELSE()
    IF(CMAKE_SYSTEM_NAME MATCHES "Windows.*")
        SET(MINGW 1)
        MESSAGE("-- Compiling on windows (MinGW)")
    ELSE()
        MESSAGE("-- Compiling on posix system: ${CMAKE_SYSTEM_NAME}")
    ENDIF()
ENDIF()

IF(RUNTIME_LIB)
    INCLUDE(../cmake/FindRuntimeLibrary.cmake)
ENDIF()


IF(HAVE_WCHAR_H)
  OPTION(HAVE_UNICODE_WCHAR "Enable Unicode Support" YES)
  MARK_AS_ADVANCED(HAVE_UNICODE_WCHAR)
ELSE(HAVE_WCHAR_H)
  SET(HAVE_UNICODE_WCHAR 0)
ENDIF(HAVE_WCHAR_H)

SET(P7ZIP_SRC ../p7zip)
SET(_7ZIP_SRC ../7zip)

SET(JBINDING_PLATFORM_CPP_FILES)
IF(MINGW)
    add_definitions(-include PlatformMinGW/StdAfx.h)
    include_directories(PlatformMinGW/)
    IF(MINGW64)
        set_source_files_properties(PlatformMinGW/MyUser32.cpp PROPERTIES COMPILE_FLAGS -DNO_MY_STDAFX)
        SET(JBINDING_PLATFORM_CPP_FILES
            #PlatformMinGW/MyWindows.cpp
            #PlatformMinGW/MyUser32.cpp
        )
    ENDIF(MINGW64)
ELSE(MINGW)
    ###include_directories(PlatformUnix/)
ENDIF(MINGW)

###include_directories(/usr/include)
###include_directories(/usr/include/c++/4.3)
include_directories(${JAVA_INCLUDE_PATH})
include_directories(${JAVA_INCLUDE_PATH2})

IF(MINGW)
    include_directories(${_7ZIP_SRC})
    include_directories(${_7ZIP_SRC}/CPP)
    include_directories(${_7ZIP_SRC}/C)
ELSE(MINGW)
    include_directories(${P7ZIP_SRC})
    include_directories(${P7ZIP_SRC}/CPP)
    include_directories(${P7ZIP_SRC}/C)
    include_directories(${P7ZIP_SRC}/CPP/myWindows)
    include_directories(${P7ZIP_SRC}/CPP/include_windows)
ENDIF(MINGW)
include_directories(${PROJECT_BINARY_DIR}/javah)
include_directories(.)

IF(MINGW32)
    add_definitions(-DMINGW -mthreads -D_JNI_IMPLEMENTATION_)
ENDIF()
IF(MINGW64)
    add_definitions(-DMINGW -mthreads -D_JNI_IMPLEMENTATION_ -D_OLEAUT32_ -D_USER32_)
ENDIF()

IF(CMAKE_BUILD_TYPE STREQUAL "Debug")
    MESSAGE("---------- DEBUG MODE ---------------")
    add_definitions(
        -DTRACE_OBJECTS_ON
        -D_DEBUG
        -ggdb3 -O0
    )
ELSE()
    add_definitions(
        -DNDEBUG
    )

    IF(APPLE)
        add_definitions( -O )
    ELSE()
        add_definitions( -O2 )
    ENDIF()
ENDIF()

add_definitions(
#    -DTRACE_ON
    -fPIC
    -fexceptions
)

IF(MINGW)
    add_definitions(
        -D_FILE_OFFSET_BITS=64
        -D_LARGEFILE_SOURCE
        -D_REENTRANT
        #-DENV_UNIX
        -DUNICODE      # since 9.13
        -D_UNICODE     # since 9.13
        -DBREAK_HANDLER
        -DBENCH_MT
    )
ELSE(MINGW)
#  -s 
    add_definitions(
        -pipe
        -D_FILE_OFFSET_BITS=64
        -D_LARGEFILE_SOURCE
        -D_REENTRANT
        -DENV_UNIX
        -D_7ZIP_LARGE_PAGES
        -DENV_HAVE_GCCVISIBILITYPATCH
        #-fvisibility=hidden
        #-fvisibility-inlines-hidden
        
        -DBREAK_HANDLER
        -DUNICODE
        -D_UNICODE
        -DUNIX_USE_WIN_FILE
    )
ENDIF(MINGW)

IF(MINGW)
    SET(SEVEN_ZIP_SRC "${_7ZIP_SRC}")
    SET(SEVEN_ZIP_PLATFORM_SOURCE_FILES
        ${SEVEN_ZIP_SRC}/C/AesOpt.c
        ${SEVEN_ZIP_SRC}/CPP/Windows/FileLink.cpp
        ${SEVEN_ZIP_SRC}/CPP/Common/NewHandler.cpp
    )
ELSE(MINGW)
    SET(SEVEN_ZIP_SRC "${P7ZIP_SRC}")
    SET(SEVEN_ZIP_PLATFORM_SOURCE_FILES
        ${SEVEN_ZIP_SRC}/CPP/myWindows/wine_date_and_time.cpp
    )
ENDIF(MINGW)

SET(SEVEN_ZIP_SOURCE_FILES
    ${SEVEN_ZIP_SRC}/CPP/Common/CommandLineParser.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/CRC.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/IntToString.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/ListFileUtils.cpp
    #${SEVEN_ZIP_SRC}/CPP/Common/StdInStream.cpp TODO check on Linux, remove it not needed
    ${SEVEN_ZIP_SRC}/CPP/Common/MyString.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/MyWindows.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/StringConvert.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/StringToInt.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/UTFConvert.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/MyVector.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/MyMap.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/MyXml.cpp
    ${SEVEN_ZIP_SRC}/CPP/Common/Wildcard.cpp

    ${SEVEN_ZIP_SRC}/CPP/Windows/FileDir.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/FileFind.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/FileIO.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/FileName.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/PropVariant.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/PropVariantConv.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/PropVariantUtils.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/Synchronization.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/System.cpp
    ${SEVEN_ZIP_SRC}/CPP/Windows/TimeUtils.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/CreateCoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/CWrappers.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/FilePathAutoRename.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/FileStreams.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/FilterCoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/InBuffer.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/InOutTempBuffer.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/LimitedStreams.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/MemBlocks.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/MethodId.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/MethodProps.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/OffsetStream.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/OutBuffer.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/OutMemStream.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/ProgressMt.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/ProgressUtils.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/PropId.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/StreamBinder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/StreamObjects.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/StreamUtils.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/UniqBlocks.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Common/VirtThread.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/UI/Common/LoadCodecs.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/UI/Common/PropIDUtils.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/UI/Console/ConsoleClose.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/ZHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Bz2Handler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/DeflateProps.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/GzHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/FatHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/HandlerCont.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/HfsHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/LzmaHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/NtfsHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/PpmdHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/SplitHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/XzHandler.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/CoderMixer2.cpp
    #${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/CrossThreadProgress.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/DummyOutStream.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/FindSignature.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/HandlerOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/InStreamWithCRC.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/ItemNameUtils.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/MultiStream.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/OutStreamWithCRC.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/OutStreamWithSha1.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Common/ParseProperties.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zCompressionMode.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zDecode.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zEncode.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zExtract.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zFolderInStream.cpp
    #${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zFolderOutStream.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zHandlerOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zHeader.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zIn.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zProperties.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zSpecStream.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zUpdate.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/7z/7zRegister.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Cab/CabBlockInStream.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Cab/CabHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Cab/CabHeader.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Cab/CabIn.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Cab/CabRegister.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/LzhHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/LzhDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/LzmsDecoder.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Chm/ChmHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Chm/ChmIn.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Udf/UdfHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Udf/UdfIn.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Wim/WimHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Wim/WimHandlerOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Wim/WimIn.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Wim/WimRegister.cpp

    # Com ??


    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/ArHandler.cpp
    
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/RpmHandler.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/XarHandler.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/ArjHandler.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/CpioHandler.cpp


    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Iso/IsoHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Iso/IsoHeader.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Iso/IsoIn.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Iso/IsoRegister.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Nsis/NsisDecode.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Nsis/NsisHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Nsis/NsisIn.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Nsis/NsisRegister.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Rar/RarHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Rar/Rar5Handler.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Tar/TarHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Tar/TarHandlerOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Tar/TarHeader.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Tar/TarIn.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Tar/TarOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Tar/TarRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Tar/TarUpdate.cpp

    # Udf

    # Wim

    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipAddCommon.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipHandler.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipHandlerOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipIn.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipItem.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipOut.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipUpdate.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Archive/Zip/ZipRegister.cpp

    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Bcj2Coder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Bcj2Register.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BcjCoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BcjRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BitlDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BranchMisc.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BranchRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/ByteSwap.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BZip2Crc.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BZip2Decoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BZip2Encoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/BZip2Register.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/CopyCoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/CopyRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Deflate64Register.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/DeflateDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/DeflateEncoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/DeflateRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/DeltaFilter.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Lzma2Decoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Lzma2Encoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Lzma2Register.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/ImplodeDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/ImplodeHuffmanDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/LzmaDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/LzmaEncoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/LzmaRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/LzOutWindow.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/LzxDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/PpmdDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/PpmdEncoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/PpmdRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/PpmdZip.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/QuantumDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Rar1Decoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Rar2Decoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Rar3Decoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Rar3Vm.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/Rar5Decoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/RarCodecsRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/ShrinkDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/ZDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/XpressDecoder.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Compress/ZlibDecoder.cpp


    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/7zAes.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/7zAesRegister.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/HmacSha1.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/HmacSha256.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/MyAes.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/Pbkdf2HmacSha1.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/RandGen.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/Rar20Crypto.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/Rar5Aes.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/RarAes.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/WzAes.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/ZipCrypto.cpp
    ${SEVEN_ZIP_SRC}/CPP/7zip/Crypto/ZipStrong.cpp
    ${SEVEN_ZIP_SRC}/C/Bcj2.c
    ${SEVEN_ZIP_SRC}/C/Bcj2Enc.c
    ${SEVEN_ZIP_SRC}/C/Blake2s.c
    ${SEVEN_ZIP_SRC}/C/Bra.c
    ${SEVEN_ZIP_SRC}/C/Bra86.c
    ${SEVEN_ZIP_SRC}/C/BraIA64.c
    ${SEVEN_ZIP_SRC}/C/BwtSort.c
    ${SEVEN_ZIP_SRC}/C/CpuArch.c
    ${SEVEN_ZIP_SRC}/C/HuffEnc.c
    ${SEVEN_ZIP_SRC}/C/LzFind.c
    ${SEVEN_ZIP_SRC}/C/LzFindMt.c
    ${SEVEN_ZIP_SRC}/C/LzmaDec.c
    ${SEVEN_ZIP_SRC}/C/LzmaEnc.c
    ${SEVEN_ZIP_SRC}/C/Sort.c
    ${SEVEN_ZIP_SRC}/C/Sha1.c
    ${SEVEN_ZIP_SRC}/C/Threads.c
    ${SEVEN_ZIP_SRC}/C/Aes.c
    ${SEVEN_ZIP_SRC}/C/Alloc.c
    ${SEVEN_ZIP_SRC}/C/Sha256.c
    ${SEVEN_ZIP_SRC}/C/7zCrc.c
    ${SEVEN_ZIP_SRC}/C/7zCrcOpt.c

    ${SEVEN_ZIP_SRC}/C/7zStream.c
    ${SEVEN_ZIP_SRC}/C/Delta.c
    ${SEVEN_ZIP_SRC}/C/Lzma2Dec.c
    ${SEVEN_ZIP_SRC}/C/Lzma2Enc.c
    ${SEVEN_ZIP_SRC}/C/MtCoder.c
    ${SEVEN_ZIP_SRC}/C/Ppmd7.c
    ${SEVEN_ZIP_SRC}/C/Ppmd7Dec.c
    ${SEVEN_ZIP_SRC}/C/Ppmd7Enc.c
    ${SEVEN_ZIP_SRC}/C/Ppmd8.c
    ${SEVEN_ZIP_SRC}/C/Ppmd8Dec.c
    ${SEVEN_ZIP_SRC}/C/Ppmd8Enc.c
    ${SEVEN_ZIP_SRC}/C/Xz.c
    ${SEVEN_ZIP_SRC}/C/XzCrc64.c
    ${SEVEN_ZIP_SRC}/C/XzCrc64Opt.c
    ${SEVEN_ZIP_SRC}/C/XzDec.c
    ${SEVEN_ZIP_SRC}/C/XzEnc.c
    ${SEVEN_ZIP_SRC}/C/XzIn.c
)

SET(JBINDING_CPP_FILES
    Debug.cpp
#    idd_def.cpp
    JNITools.cpp
    JavaStaticInfo.cpp
    SevenZipJBinding.cpp
    UniversalArchiveOpenCallback.cpp
    UserTrace.cpp
    CodecTools.cpp
    JBindingTools.cpp
    CHeadCacheInStream.cpp
    JavaToCPP/JavaToCPPSevenZip.cpp
    JavaToCPP/JavaToCPPInArchiveImpl.cpp
    JavaToCPP/JavaToCPPOutArchiveImpl.cpp
    CPPToJava/CPPToJavaArchiveExtractCallback.cpp
    CPPToJava/CPPToJavaArchiveOpenCallback.cpp
    CPPToJava/CPPToJavaArchiveOpenVolumeCallback.cpp
    CPPToJava/CPPToJavaCryptoGetTextPassword.cpp
    CPPToJava/CPPToJavaInStream.cpp
    CPPToJava/CPPToJavaOutStream.cpp
    CPPToJava/CPPToJavaProgress.cpp
    CPPToJava/CPPToJavaSequentialInStream.cpp
    CPPToJava/CPPToJavaSequentialOutStream.cpp
    CPPToJava/CPPToJavaArchiveUpdateCallback.cpp

    ../test/CTests/JniToolsTest.cpp
    ../test/CTests/JBindingTest.cpp
    ../test/CTests/EnumTest.cpp
    ../test/CTests/CHeadCacheInStreamTest.cpp
)

SET(JBINDING_JAVAH_H_FILES
    ${PROJECT_SOURCE_DIR}/JavaToCPP/Java/net_sf_sevenzipjbinding_impl_InArchiveImpl.h
    ${PROJECT_SOURCE_DIR}/JavaToCPP/Java/net_sf_sevenzipjbinding_SevenZip.h
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
    net.sf.sevenzipjbinding.impl.OutArchiveImpl
)


add_custom_command(OUTPUT ${JBINDING_JAVAH_H_FILES}
                   COMMAND ${JAVA_HEADER_COMPILE} -jni -d . -classpath ${SEVENZIPJBINDING_JAR} ${JAVAH_JAVA_CLASS_LIST}  
                   DEPENDS ${SEVENZIPJBINDING_JAR}
                   WORKING_DIRECTORY ${JAVAH_OUTPUT_DIR}
                   COMMENT "javah: generating h-files from java classes ${JAVAH_JAVA_CLASS_LIST}"
)

add_library(7-Zip-JBinding
            SHARED
            ${JBINDING_CPP_FILES}
            ${JBINDING_PLATFORM_CPP_FILES}
            ${SEVEN_ZIP_SOURCE_FILES}
            ${SEVEN_ZIP_PLATFORM_SOURCE_FILES}
            ${JBINDING_JAVAH_H_FILES})

#GET_TARGET_PROPERTY(SEVENZIP_JBINDING_LIB 7-Zip-JBinding LOCATION)
#SET_PROPERTY(GLOBAL PROPERTY SEVENZIP_JBINDING_LIB ${SEVENZIP_JBINDING_LIB}) 

IF(STATIC_BUILD) 
    SET_PROPERTY(TARGET 7-Zip-JBinding APPEND_STRING PROPERTY LINK_FLAGS " -static-libgcc -static-libstdc++ ")
ENDIF()

IF(MINGW)
#   TARGET_LINK_LIBRARIES(7-Zip-JBinding c)
ELSE(MINGW)
    TARGET_LINK_LIBRARIES(7-Zip-JBinding dl c pthread)
ENDIF(MINGW)



IF(MINGW)

    SET_PROPERTY(TARGET 7-Zip-JBinding APPEND_STRING PROPERTY LINK_FLAGS " -Wall -Wl,--kill-at -mthreads ")

    # Remove default libraries 
    #SET(CMAKE_CXX_STANDARD_LIBRARIES "-g") #-luser32 -loleaut32 -lole32 -lshell32 -lgdi32 -lwinspool -luuid -lcomdlg32 -ladvapi32 -lkernel32 
    #SET(CMAKE_C_STANDARD_LIBRARIES "-g") #-luser32 -loleaut32 -lole32 -lshell32 -lgdi32 -lwinspool -luuid -lcomdlg32 -ladvapi32 -lkernel32 

    # TODO This is from RW branch. Remove later on.
    #        SET_TARGET_PROPERTIES(7-Zip-JBinding PROPERTIES LINK_FLAGS "-Wl,--add-stdcall-alias -mthreads -Wl,--enable-auto-import") # -lpthread -lstdc++ -lws2_32") #-Wl,--add-stdcall-alias
    #    ELSE(MINGW)
    #	TARGET_LINK_LIBRARIES(7-Zip-JBinding ${CMAKE_THREAD_LIBS_INIT})
    #        SET_TARGET_PROPERTIES(7-Zip-JBinding PROPERTIES LINK_FLAGS "-Wl,--add-stdcall-alias -mthreads") # -lpthread -lstdc++ -lws2_32") #-Wl,--add-stdcall-alias
ELSE(MINGW)
    IF(NOT APPLE)
        SET_PROPERTY(TARGET 7-Zip-JBinding APPEND_STRING PROPERTY LINK_FLAGS " -Wl,-z,defs -Wl,--no-undefined ") # Check for missing symbols
    ENDIF()
ENDIF(MINGW)

#TARGET_LINK_LIBRARIES(7-Zip-JBinding duma)
#FILE(WRITE "link.sh" "${CMAKE_CXX_CREATE_SHARED_LIBRARY}")
#IF(CMAKE_COMPILER_IS_GNUCXX)
#  IF(CMAKE_COMPILER_IS_GNUCC)
#    SET(CMAKE_CXX_CREATE_SHARED_LIBRARY
#        "gcc <CMAKE_SHARED_LIBRARY_CXX_FLAGS> <LANGUAGE_COMPILE_FLAGS> <LINK_FLAGS> <CMAKE_SHARED_LIBRARY_CREATE_CXX_FLAGS> <CMAKE_SHARED_LIBRARY_SONAME_CXX_FLAG><TARGET_SONAME> -o <TARGET> <OBJECTS> <LINK_LIBRARIES>")
#  ENDIF()
#ENDIF()

# vim: ts=4 sts=4 sw=4 expandtab
