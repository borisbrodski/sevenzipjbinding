IF(CMAKE_HOST_WIN32) #true for win64 also
    MESSAGE("-- Looking for mingwm10.dll")
    GET_FILENAME_COMPONENT(COMPILER_BIN_DIR "${CMAKE_CXX_COMPILER}" PATH)

    FIND_FILE(MINWGM10_DLL_FILENAME mingwm10.dll "$ENV{PATH}" "${COMPILER_BIN_DIR}")
    MARK_AS_ADVANCED(MINWGM10_DLL_FILENAME)

    IF(MINWGM10_DLL_FILENAME)
        MESSAGE("-- Looking for mingwm10.dll - found: ${MINWGM10_DLL_FILENAME}")
    ELSE()
        MESSAGE(FATAL_ERROR "mingwm10.dll not found. Please point cmake to mingwm10.dll using GUI or -DMINWGM10_DLL_FILENAME=<path-to-dll> option.")
    ENDIF()

    IF (CYGWIN)
        # CygWin
        FIND_PROGRAM(CYGPATH_EXE_FILENAME "cygpath.exe" $ENV{PATH})
        IF(CYGPATH_EXE_FILENAME)
            MESSAGE("-- Looking for cygpath.exe - found: ${CYGPATH_EXE_FILENAME}")
        ELSE()
            MESSAGE(FATAL_ERROR "cygpath.exe not found. Please point cmake to mingwm10.dll using GUI or -DCYGPATH_EXE_FILENAME=<path-to-exe> option.")
        ENDIF()

        MARK_AS_ADVANCED(CYGPATH_EXE_FILENAME)
    ENDIF()

ENDIF(CMAKE_HOST_WIN32)
