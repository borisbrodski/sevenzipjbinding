IF(WINDOWS) #true for win64 also
    IF(NOT RUNTIME_LIB)
        MESSAGE(FATAL_ERROR "No runtime library provided. Use -DRUNTIME_LIB=<filename> to specify it")
    ENDIF()

    MESSAGE("-- Looking for ${RUNTIME_LIB}")

    GET_FILENAME_COMPONENT(COMPILER_BIN_DIR "${CMAKE_CXX_COMPILER}" PATH)

    FIND_FILE(RUNTIME_LIB_FILENAME "${RUNTIME_LIB}" "$ENV{PATH}" "${COMPILER_BIN_DIR}")

    MARK_AS_ADVANCED(RUNTIME_LIB_FILENAME)

    IF(RUNTIME_LIB_FILENAME)
        MESSAGE("-- Looking for ${RUNTIME_LIB} - found: ${RUNTIME_LIB_FILENAME}")
    ELSE()
        MESSAGE(FATAL_ERROR "${RUNTIME_LIB} not found. Please point cmake to ${RUNTIME_LIB} using GUI or -DRUNTIME_LIB_FILENAME=<path-to-lib> option.")
    ENDIF()
ENDIF(WINDOWS)
