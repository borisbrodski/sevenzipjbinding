IF(WIN32 OR WIN64)
    MESSAGE("-- Looking for mingwm10.dll")
    IF (CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")
        # CygWin
        FIND_FILE(MINWGM10_DLL_FILENAME "mingwm10.dll" $ENV{PATH})
    ELSE(CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")
        # MinGW
        FIND_FILE(MINWGM10_DLL_FILENAME "mingwm10.dll" $ENV{PATH})
    ENDIF(CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")
    MARK_AS_ADVANCED(MINWGM10_DLL_FILENAME)

    IF (CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")
        # CygWin
        FIND_PROGRAM(CYGPATH_EXE_FILENAME "cygpath.exe" $ENV{PATH})
        MARK_AS_ADVANCED(CYGPATH_EXE_FILENAME)
    ENDIF(CMAKE_SYSTEM_NAME MATCHES "CYGWIN.*")

    IF(CYGPATH_EXE_FILENAME)
        MESSAGE("-- Looking for mingwm10.dll - found: ${CYGPATH_EXE_FILENAME}")
    ELSE()
        MESSAGE(FATAL_ERROR "mingwm10.dll not found. Please point cmake to mingwm10.dll using GUI or -DCYGPATH_EXE_FILENAME=<path-to-dll> option.")
    ENDIF()
ENDIF(WIN32 OR WIN64)
