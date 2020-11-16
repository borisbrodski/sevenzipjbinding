
IF(CMAKE_HOST_WIN32) #true for win64 also
    SET(PATH_SEP ";")
ELSE()
    SET(PATH_SEP ":")
ENDIF()

FILE(READ "java-executable" JAVA)
FILE(STRINGS "java-executable-params" JAVA_PARAMS)

IF(NOT JAVA)
    MESSAGE(FATAL_ERROR "Internal error. Can't read 'java-executable' file with the java execuable name.")
ENDIF()

FILE(GLOB_RECURSE TEST_LIBS "lib/" "*.jar")
SET(TEST_LIBS_STR "")
FOREACH(TEST_LIB ${TEST_LIBS})
    IF(NOT ("${TEST_LIBS_STR}" STREQUAL ""))
        SET(TEST_LIBS_STR "${TEST_LIBS_STR}${PATH_SEP}")
    ENDIF()
    SET(TEST_LIBS_STR "${TEST_LIBS_STR}${TEST_LIB}")
ENDFOREACH()

EXECUTE_PROCESS(COMMAND
                    "${JAVA}" -cp "${TEST_LIBS_STR}"
                    ${JAVA_PARAMS}
                    ${RUNTIME_JAVA_OPTS}
                    "-DSINGLEBUNDLE=${SINGLEBUNDLE}"
                    "-Dskip-debug-mode-tests=true"
                    org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite
                WORKING_DIRECTORY .
                RESULT_VARIABLE RESULT)
IF(RESULT)
    MESSAGE(SEND_ERROR "Error during JUnit Tests")
ENDIF(RESULT)
