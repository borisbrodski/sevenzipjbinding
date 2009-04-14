
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

include_directories(${P7ZIP_SRC}/CPP/myWindows)
include_directories(${P7ZIP_SRC}/CPP/)
include_directories(${P7ZIP_SRC}/CPP/include_windows)


add_definitions(
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
)


SET(P7ZIP_CPP_FILES
    ${P7ZIP_SRC}/CPP/myWindows/wine_date_and_time.cpp
    ${P7ZIP_SRC}/CPP/Common/MyWindows.cpp
    ${P7ZIP_SRC}/CPP/Windows/DLL.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileDir.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileFind.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileIO.cpp
    ${P7ZIP_SRC}/CPP/Windows/FileName.cpp
    ${P7ZIP_SRC}/CPP/Windows/PropVariant.cpp
    ${P7ZIP_SRC}/CPP/Windows/PropVariantConversions.cpp
    ${P7ZIP_SRC}/CPP/Common/IntToString.cpp
    ${P7ZIP_SRC}/CPP/Common/MyString.cpp
    ${P7ZIP_SRC}/CPP/Common/StringConvert.cpp
    ${P7ZIP_SRC}/CPP/Common/MyVector.cpp
    ${P7ZIP_SRC}/CPP/Common/Wildcard.cpp
    ${P7ZIP_SRC}/CPP/7zip/Common/FileStreams.cpp
)

#SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fvisibility=hidden -fvisibility-inlines-hidden")

SET(CMAKE_VERBOSE_MAKEFILE OFF)
IF(CMAKE_COMPILER_IS_GNUCC)
    SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fmessage-length=0")
ENDIF(CMAKE_COMPILER_IS_GNUCC)
IF(CMAKE_COMPILER_IS_GNUCXX)
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fmessage-length=0")
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

ADD_EXECUTABLE(test Client7z.cpp ${P7ZIP_CPP_FILES})

TARGET_LINK_LIBRARIES(test dl)

IF(HAVE_PTHREADS) 
  TARGET_LINK_LIBRARIES(test ${CMAKE_THREAD_LIBS_INIT}) 
ENDIF(HAVE_PTHREADS) 

