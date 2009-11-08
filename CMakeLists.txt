CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
PROJECT(SevenZipJBinding)

# Compile on Mac OS https://bug477945.bugzilla.mozilla.org/attachment.cgi?id=376115
# CC="gcc-4.2 -arch i386"
# CXX="g++-4.2 -arch i386"


# Uncomment to build in debug mode
# SET(CMAKE_BUILD_TYPE Debug)

# Uncomment to build Windows 64 version
# SET(USE_MINGW_W64 Yes)

IF (NOT CMAKE_BUILD_TYPE)
    SET(CMAKE_BUILD_TYPE Release)
ENDIF()

INCLUDE(cmake/FindJavaExtended.cmake)
INCLUDE(CTest)


# Uncomment this to build a version
SET(SEVENZIPJBINDING_VERSON 4.65-1.02rc-extr-only)

SET(RELEASE_PATH ${PROJECT_BINARY_DIR})
IF (SEVENZIPJBINDING_VERSON)
    SET(SEVENZIPJBINDING_VERSON_POSTFIX "-${SEVENZIPJBINDING_VERSON}")
ELSE(SEVENZIPJBINDING_VERSON)
    SET(SEVENZIPJBINDING_VERSON_POSTFIX "")
ENDIF(SEVENZIPJBINDING_VERSON)

IF(USE_MINGW_W64)
	SET(PLATFORM ${CMAKE_SYSTEM_NAME}-amd64)
ELSE()
	SET(PLATFORM ${CMAKE_SYSTEM_NAME}-${CMAKE_SYSTEM_PROCESSOR})
ENDIF()

SET(SEVENZIPJBINDING_LIB_JAR ${PROJECT_BINARY_DIR}/sevenzip-jbinding-${PLATFORM}.jar)
SET(SEVENZIPJBINDING_FILENAME sevenzip-jbinding${SEVENZIPJBINDING_VERSON_POSTFIX}-${PLATFORM})
#SET(SEVENZIPJBINDING_ZIP ${RELEASE_PATH}/${SEVENZIPJBINDING_FILENAME}.zip)

# ------------------- Java -------------------

# Variables:
#
# JAVA_SOURCE_DIR               - path to java source code
# SEVENZIP_JBINDING_JAR         - (global) target JAR file

SET(JAVA_SOURCE_DIR ${PROJECT_SOURCE_DIR}/jbinding-java/src)
SET(SEVENZIP_JBINDING_JAR ${PROJECT_BINARY_DIR}/jbinding-java/sevenzip-jbinding.jar)

FILE(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/jbinding-java)
SET_PROPERTY(GLOBAL PROPERTY SEVENZIP_JBINDING_JAR ${SEVENZIP_JBINDING_JAR}) 

IF(CMAKE_HOST_WIN32) #true for win64 also
    SET(PATH_SEP ";")
ELSE()
    SET(PATH_SEP ":")
ENDIF()

MACRO(CREATE_COMPILE_JAVA_CUSTOM_COMMAND 
        P_BUILD_NAME    # spaceless simple name of the build process. For example: "test-src-build"  
        P_SRC_DIR       # java source directory
        P_JAR_FILE      # output jar file
        )    # list of classpath entries
    SET(BIN_DIR ${PROJECT_BINARY_DIR}/jbinding-java/bin-${P_BUILD_NAME})
    FILE(MAKE_DIRECTORY ${BIN_DIR})

    SET(JAVAC_ARGS_FILE ${PROJECT_BINARY_DIR}/javac-args-${P_BUILD_NAME}.tmp)
    FILE(GLOB_RECURSE JAVA_SOURCE_FILES RELATIVE ${P_SRC_DIR} ${P_SRC_DIR}/*.java)

    FILE(WRITE ${JAVAC_ARGS_FILE} "-source 1.5 -target 1.5\n")
    FILE(APPEND ${JAVAC_ARGS_FILE} "-d ${BIN_DIR}\n")
    IF(NOT "${ARGN}" STREQUAL "")
        FILE(APPEND ${JAVAC_ARGS_FILE} "-cp ")
        SET(SEP "")
        FOREACH(D ${ARGN})
            FILE(APPEND ${JAVAC_ARGS_FILE} "${SEP}${D}")
            SET(SEP "${PATH_SEP}")
        ENDFOREACH()
        FILE(APPEND ${JAVAC_ARGS_FILE} "\n")
    ENDIF()
    FOREACH(F ${JAVA_SOURCE_FILES})
        FILE(APPEND ${JAVAC_ARGS_FILE} "${F}\n")
    ENDFOREACH(F)
    
    SET(JAVA_SOURCE_FILES_FULL "")
    FOREACH(F ${JAVA_SOURCE_FILES})
        LIST(APPEND JAVA_SOURCE_FILES_FULL "${P_SRC_DIR}/${F}")
    ENDFOREACH(F)

    ADD_CUSTOM_COMMAND(OUTPUT ${P_JAR_FILE}
        COMMAND ${JAVA_COMPILE} @${JAVAC_ARGS_FILE}
        COMMAND ${JAVA_ARCHIVE} cf ${P_JAR_FILE} -C ${BIN_DIR} .
        DEPENDS ${JAVA_SOURCE_FILES_FULL}
        WORKING_DIRECTORY "${P_SRC_DIR}"
        COMMENT Compiling java ${P_BUILD_NAME}
        VERBATIM
    )
ENDMACRO()

CREATE_COMPILE_JAVA_CUSTOM_COMMAND(core "${JAVA_SOURCE_DIR}" "${SEVENZIP_JBINDING_JAR}")

ADD_CUSTOM_TARGET(sevenzip-jbinding-jar ALL
                  DEPENDS ${SEVENZIP_JBINDING_JAR}
                  COMMENT Processing java build target)


#ADD_SUBDIRECTORY(jbinding-java)
ADD_SUBDIRECTORY(jbinding-cpp)

ADD_DEPENDENCIES(7-Zip-JBinding sevenzip-jbinding-jar)

GET_PROPERTY(USE_MINGW                GLOBAL PROPERTY     USE_MINGW) 
GET_TARGET_PROPERTY(SEVENZIP_JBINDING_LIB 7-Zip-JBinding LOCATION)
GET_FILENAME_COMPONENT(SEVENZIP_JBINDING_LIB_FILENAME "${SEVENZIP_JBINDING_LIB}" NAME)

SET(SEVENZIP_JBINDING_LIB_PROPERTY_FILE sevenzipjbinding-lib.properties)
FILE(WRITE ${PROJECT_BINARY_DIR}/${SEVENZIP_JBINDING_LIB_PROPERTY_FILE} "sevenzipjbinding.libname.1=${SEVENZIP_JBINDING_LIB_FILENAME}\n")
IF(USE_MINGW)
    IF (MINW_RUNTIME_DLL_FILENAME MATCHES "/.*" AND USE_CYGWIN)
        IF(NOT CYGPATH_EXE_FILENAME)
            MESSAGE(FATAL_ERROR "cygpath.exe wasn't found")
        ENDIF(NOT CYGPATH_EXE_FILENAME)
        EXECUTE_PROCESS(COMMAND cygpath.exe -m ${MINW_RUNTIME_DLL_FILENAME} OUTPUT_VARIABLE MINW_RUNTIME_DLL_FILENAME)
    ENDIF()
# !!! TODO
    GET_FILENAME_COMPONENT(MINWG_RUNTIME_DLL_DIR ${MINW_RUNTIME_DLL_FILENAME} PATH)
    GET_FILENAME_COMPONENT(MINWG_RUNTIME_DLL_NAME ${MINW_RUNTIME_DLL_FILENAME} NAME)
    FILE(APPEND ${PROJECT_BINARY_DIR}/${SEVENZIP_JBINDING_LIB_PROPERTY_FILE} "sevenzipjbinding.libname.2=${MINWG_RUNTIME_DLL_NAME}")
ENDIF(USE_MINGW)

MESSAGE("")
MESSAGE("Build type: ${CMAKE_BUILD_TYPE}, platform: ${PLATFORM}")
MESSAGE("")
MESSAGE("Java VM: ${JAVA_RUNTIME}")
MESSAGE("Java compiler: ${JAVA_COMPILE}")
MESSAGE("Java header compiler: ${JAVA_HEADER_COMPILE}")
MESSAGE("Java documentation tool: ${JAVA_DOC}")
MESSAGE("Java archiver: ${JAVA_ARCHIVE}")
IF(USE_MINGW)
    IF(USE_MINGW_W64)
        MESSAGE("libgcc_s_sjlj-1.dll to use: ${MINW_RUNTIME_DLL_FILENAME}")
    ELSE()
        MESSAGE("mingwm10.dll to use: ${MINW_RUNTIME_DLL_FILENAME}")
    ENDIF()
ENDIF()
MESSAGE("Output package: ./${SEVENZIPJBINDING_FILENAME}.zip")
MESSAGE("")
MESSAGE("")
IF(USE_MINGW)
    MESSAGE("Type 'mingw32-make' to compile")
ELSE()
    MESSAGE("Type 'make' to compile")
ENDIF()
MESSAGE("Type 'ctest' to run tests (it could take up to 30 minutes on a slow CPU)")
MESSAGE("Type 'make package' to build a distribution package")
MESSAGE("")

ADD_CUSTOM_TARGET(sevenzip-jbinding-lib-jar ALL
    DEPENDS ${SEVENZIPJBINDING_LIB_JAR}) 

ADD_DEPENDENCIES(sevenzip-jbinding-lib-jar 7-Zip-JBinding) #sevenzip-jbinding-jar


ADD_CUSTOM_COMMAND(OUTPUT ${SEVENZIPJBINDING_LIB_JAR}
                   COMMAND ${JAVA_ARCHIVE} cf ${SEVENZIPJBINDING_LIB_JAR} 
                                           -C ${PROJECT_BINARY_DIR}/jbinding-cpp
                                           ${SEVENZIP_JBINDING_LIB_FILENAME}
                   COMMAND ${JAVA_ARCHIVE} uf ${SEVENZIPJBINDING_LIB_JAR} 
                                           -C ${PROJECT_BINARY_DIR}
                                           ${SEVENZIP_JBINDING_LIB_PROPERTY_FILE}
                   DEPENDS ${SEVENZIP_JBINDING_LIB}
                   WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
                   COMMENT Building Platformdependend jar)


IF(CMAKE_HOST_WIN32)
    ADD_CUSTOM_COMMAND(TARGET sevenzip-jbinding-lib-jar POST_BUILD
                       COMMAND ${JAVA_ARCHIVE} uf ${SEVENZIPJBINDING_LIB_JAR} 
                                               -C ${MINWG_RUNTIME_DLL_DIR}
                                               ${MINWG_RUNTIME_DLL_NAME}
                       WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
                       COMMENT Adding MinGW runtime dll to the jar)
ENDIF()


# ------------------- CPack -------------------

SET(CPACK_PACKAGE_DESCRIPTION_SUMMARY "7-Zip-JBinding - java binding for p7zip, crossplatform version of 7-Zip.")
SET(CPACK_PACKAGE_DESCRIPTION_SUMMARY ZIP)
SET(CPACK_GENERATOR ZIP)
SET(CPACK_PACKAGE_FILE_NAME ${SEVENZIPJBINDING_FILENAME})
SET(CPACK_SOURCE_GENERATOR ZIP)
#SET(CPACK_SOURCE_IGNORE_FILES "/CVS/;/\\.svn/;\\.swp$;\\.#;/#;.*~;cscope.*")

INCLUDE(CPack)

SET(PREPACKAGE_TMP "${PROJECT_BINARY_DIR}/prepackage.tmp")
SET(JAVADOC_DIR "${PREPACKAGE_TMP}/javadoc/")
FILE(REMOVE_RECURSE ${PREPACKAGE_TMP})
FILE(MAKE_DIRECTORY ${PREPACKAGE_TMP})

SET(PREPACKAGE_ACTIONS 
    "
    MACRO(COPY_FROM_SRC SRC DEST EXCLUDE_PATTERN)
        FILE(GLOB_RECURSE Files RELATIVE \${SRC} \${SRC}/*)
        FILE(MAKE_DIRECTORY \"\${DEST}\")
        FOREACH(File \${Files})
	        IF(NOT File MATCHES \".*(/|^)[.]svn/.*\")
	            IF(\"x\${EXCLUDE_PATTERN}\" STREQUAL \"x\" OR NOT File MATCHES \"\${EXCLUDE_PATTERN}\")
                    configure_file(
                            \${SRC}/\${File}
                            \${DEST}/\${File}
                            @ONLY)
                ENDIF()
            ENDIF()
        ENDFOREACH()
    ENDMACRO()

    FILE(MAKE_DIRECTORY \"${JAVADOC_DIR}\")
    EXECUTE_PROCESS(COMMAND ${JAVA_DOC}
                                -public -version -author -quiet
                                -sourcepath ${PROJECT_SOURCE_DIR}/jbinding-java/src 
                                -subpackages net.sf.sevenzipjbinding 
                                -d ${JAVADOC_DIR}
    WORKING_DIRECTORY .)
    
    COPY_FROM_SRC(\"${PROJECT_SOURCE_DIR}/jbinding-java/src\" \"${PREPACKAGE_TMP}/java-src/\" \"\")
    COPY_FROM_SRC(\"${PROJECT_SOURCE_DIR}/jbinding-cpp\" \"${PREPACKAGE_TMP}/cpp-src/\" CMakeLists.txt)

    EXECUTE_PROCESS(COMMAND ${JAVA_ARCHIVE} cMf ${PREPACKAGE_TMP}/java-src.zip .
    WORKING_DIRECTORY ${PREPACKAGE_TMP}/java-src/)

    EXECUTE_PROCESS(COMMAND ${JAVA_ARCHIVE} cMf ${PREPACKAGE_TMP}/cpp-src.zip .
    WORKING_DIRECTORY ${PREPACKAGE_TMP}/cpp-src/)

    EXECUTE_PROCESS(COMMAND ${JAVA_ARCHIVE} cMf ${PREPACKAGE_TMP}/javadoc.zip .
    WORKING_DIRECTORY ${JAVADOC_DIR})"
)

FOREACH(PREPACKAGE_ACTION ${PREPACKAGE_ACTIONS})
    INSTALL(CODE "${PREPACKAGE_ACTION}")
ENDFOREACH(PREPACKAGE_ACTION ${PREPACKAGE_ACTIONS})

INSTALL(FILES   AUTHORS
                ChangeLog
                COPYING
                LGPL
                NEWS
                README
                ReleaseNotes.txt
                THANKS
        DESTINATION .)
INSTALL(FILES	CMakeFile-bin-dist.txt
	DESTINATION .
	RENAME CMakeLists.txt)
INSTALL(FILES   ${SEVENZIP_JBINDING_JAR} 
                ${SEVENZIPJBINDING_LIB_JAR}
        DESTINATION lib)

INSTALL(FILES   ${PREPACKAGE_TMP}/java-src.zip
                ${PREPACKAGE_TMP}/cpp-src.zip
                ${PREPACKAGE_TMP}/javadoc.zip
        DESTINATION .)

#INSTALL(DIRECTORY ${JAVADOC_DIR}/
#        DESTINATION javadoc)

#INSTALL(DIRECTORY ${PROJECT_SOURCE_DIR}/jbinding-java/src/
#        DESTINATION java-src
#        REGEX "/\\.svn/" EXCLUDE)


# -------------------------- CTest

IF(BUILD_TESTING)
    MESSAGE("-- Prepare for testing")
    SET(JAVA_TEST_SOURCE_DIR ${PROJECT_SOURCE_DIR}/test/JavaTests/src)
    SET(TESTS_JAR ${PROJECT_BINARY_DIR}/jbinding-java/sevenzip-jbinding-tests.jar)
    SET(JUNIT_LIB ${PROJECT_SOURCE_DIR}/test/JavaTests/lib/junit-4.6.jar)
    SET(CTEST_TEST_TIMEOUT 7200)
    
    CREATE_COMPILE_JAVA_CUSTOM_COMMAND(test "${JAVA_TEST_SOURCE_DIR}" "${TESTS_JAR}" "${JUNIT_LIB}" ${SEVENZIP_JBINDING_JAR})

    ADD_CUSTOM_TARGET(sevenzip-jbinding-test-jar ALL
                      DEPENDS ${TESTS_JAR}
                      COMMENT Processing java build target)


    SET(JUNIT_TEST_RUNNER ${PROJECT_BINARY_DIR}/JUnitRunner.cmake)
    FILE(WRITE ${JUNIT_TEST_RUNNER} "EXECUTE_PROCESS(COMMAND
                                            ${JAVA_RUNTIME} -cp \"${JUNIT_LIB}${PATH_SEP}${TESTS_JAR}${PATH_SEP}${SEVENZIP_JBINDING_JAR}${PATH_SEP}${SEVENZIPJBINDING_LIB_JAR}\"
                                            \"-DSINGLEDIR=\${TEST_DIR}\" \"-DSRCDIR=\${SRCDIR}\"
                                            org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite
                                            WORKING_DIRECTORY ${JAVA_TEST_SOURCE_DIR}/..
                                            RESULT_VARIABLE RESULT)
                                     IF(RESULT)
                                         MESSAGE(SEND_ERROR \"Error during JUnit Tests\")
                                     ENDIF(RESULT)
                                     ")
    add_test(JUnit-init                         ${CMAKE_COMMAND} -DTEST_DIR=src/net/sf/sevenzipjbinding/junit -DSRCDIR=src -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-tools                        ${CMAKE_COMMAND} -DTEST_DIR=src/net/sf/sevenzipjbinding/junit/tools -DSRCDIR=src -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-snippets                     ${CMAKE_COMMAND} -DTEST_DIR=src/net/sf/sevenzipjbinding/junit/snippets -DSRCDIR=src -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-single-file-extraction       ${CMAKE_COMMAND} -DTEST_DIR=src/net/sf/sevenzipjbinding/junit/singlefile -DSRCDIR=src -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-multiple-files-extraction    ${CMAKE_COMMAND} -DTEST_DIR=src/net/sf/sevenzipjbinding/junit/multiplefiles -DSRCDIR=src -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-badarchive                   ${CMAKE_COMMAND} -DTEST_DIR=src/net/sf/sevenzipjbinding/junit/badarchive -DSRCDIR=src -P ${JUNIT_TEST_RUNNER})
#                                         org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite) JUnitInitializationTest
ENDIF(BUILD_TESTING)
