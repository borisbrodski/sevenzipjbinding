CMAKE_MINIMUM_REQUIRED(VERSION 2.8.11)
PROJECT(SevenZipJBinding)

# Compile on Mac OS https://bug477945.bugzilla.mozilla.org/attachment.cgi?id=376115
# 64 bit java: /System/Library/Frameworks/JavaVM.framework/Versions/1.6/Commands/java
# CC="gcc-4.2 -arch i386"
# CXX="g++-4.2 -arch i386"

SET(CMAKE_BUILD_TYPE Release CACHE STRING "Build target: Release or Debug")
OPTION(STATIC_BUILD "links the dynamic library statically. Such library will not require dependencies on dynamic libraries at runtime" TRUE)

SET(MINGW32     No CACHE BOOL "Use MinGW toolchain (32 bit)")
SET(MINGW64     No CACHE BOOL "Use MinGW toolchain (64 bit)")
SET(RUNTIME_LIB CACHE BOOL "Compiler runtime library to package and load before 7-Zip-JBinding")

SET(JAVA_TMP       CACHE PATH "Path to tmp directory")
SET(JAVA_PARAMS    CACHE STRING "Additional parameters for java")

IF (MINGW32 OR MINGW64)
    SET(MINGW Yes)
ELSE()
    SET(MINGW No)
ENDIF()

SET(WINDOWS ${CMAKE_HOST_WIN32}) #true also for win64


IF(CMAKE_SYSTEM_NAME MATCHES "Windows.*")
    SET(WINDOWS Yes)
ENDIF()

IF(WINDOWS AND NOT MINGW)
    MESSAGE(FATAL_ERROR "On Windows only MinGW build is supported")
ENDIF()

INCLUDE(cmake/FindJavaExtended.cmake)
#INCLUDE(cmake/CheckGCC.cmake)



# Change version also in SevenZip.SEVENZIPJBINDING_VERSION
SET(SEVENZIPJBINDING_VERSON 16.02-2.01)



SET(RELEASE_PATH ${PROJECT_BINARY_DIR})
IF (SEVENZIPJBINDING_VERSON)
    SET(SEVENZIPJBINDING_VERSON_POSTFIX "-${SEVENZIPJBINDING_VERSON}")
ELSE(SEVENZIPJBINDING_VERSON)
    SET(SEVENZIPJBINDING_VERSON_POSTFIX "")
ENDIF(SEVENZIPJBINDING_VERSON)

IF("${CMAKE_SYSTEM_NAME}" STREQUAL "Darwin") # TODO Switch to JAVA_SYSTEM
    MESSAGE("---- Mac OS X ----")
    SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -arch ${JAVA_ARCH}")
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -arch ${JAVA_ARCH}")
ENDIF()

SET(PLATFORM ${JAVA_SYSTEM}-${JAVA_ARCH})

SET(SEVENZIPJBINDING_LIB_JAR ${PROJECT_BINARY_DIR}/sevenzipjbinding-${PLATFORM}.jar)
SET(SEVENZIPJBINDING_LIB_MANIFEST ${PROJECT_BINARY_DIR}/MANIFEST.MF)
SET(SEVENZIPJBINDING_FILENAME sevenzipjbinding${SEVENZIPJBINDING_VERSON_POSTFIX}-${PLATFORM})
SET(SEVENZIPJBINDING_SOURCE_FILENAME sevenzipjbinding${SEVENZIPJBINDING_VERSON_POSTFIX}-src)

SET(JAVA_SOURCE_DIR "${PROJECT_SOURCE_DIR}/jbinding-java/src")
SET(SEVENZIPJBINDING_JAR "${PROJECT_BINARY_DIR}/jbinding-java/sevenzipjbinding.jar")
SET(SEVENZIPJBINDING_MANIFEST "${PROJECT_BINARY_DIR}/jbinding-java/MANIFEST.MF")

SET_PROPERTY(GLOBAL PROPERTY SEVENZIPJBINDING_JAR ${SEVENZIPJBINDING_JAR}) 
SET_PROPERTY(GLOBAL PROPERTY MINGW                 ${MINGW}) 
SET_PROPERTY(GLOBAL PROPERTY WINDOWS               ${WINDOWS}) 

FILE(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/jbinding-java)
IF(WINDOWS)
    SET(PATH_SEP ";")
ELSE()
    SET(PATH_SEP ":")
ENDIF()

MACRO(GENERATE_MANIFEST_MF
    MANIFEST_FILE
    TITLE
)
    STRING(TIMESTAMP DATE_TIME "%Y-%m-%d %H:%M:%SZ" UTC)
    FILE(WRITE "${MANIFEST_FILE}"
"Manifest-Version: 1.0
CMake: ${CMAKE_VERSION}
Created-By: 1.6.0_33-b03 (Sun Microsystems Inc.)
Implementation-Title: ${TITLE}
Implementation-Vendor: sevenzipjbind.sf.net
Implementation-Version: ${SEVENZIPJBINDING_VERSON}
Built-By: Boris Brodski
Built-Date: ${DATE_TIME}
")
endmacro(GENERATE_MANIFEST_MF)

MACRO(CREATE_COMPILE_JAVA_CUSTOM_COMMAND 
        P_BUILD_NAME    # spaceless simple name of the build process. For example: "test-src-build"  
        P_SRC_DIR       # java source directory
        P_MANIFEST_MF   # MANIFEST.MF
        P_JAR_FILE      # output jar file
    )    # list of classpath entries
    SET(BIN_DIR ${PROJECT_BINARY_DIR}/jbinding-java/bin-${P_BUILD_NAME})
    FILE(MAKE_DIRECTORY ${BIN_DIR})

    SET(JAVAC_ARGS_FILE ${PROJECT_BINARY_DIR}/javac-args-${P_BUILD_NAME}.tmp)
    FILE(GLOB_RECURSE JAVA_SOURCE_FILES RELATIVE ${P_SRC_DIR} ${P_SRC_DIR}/*.java)

    FILE(WRITE ${JAVAC_ARGS_FILE} "-source 1.5 -target 1.5\n")
    FILE(APPEND ${JAVAC_ARGS_FILE} "-encoding UTF-8\n")
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

    ADD_CUSTOM_COMMAND(OUTPUT "${P_JAR_FILE}"
        COMMAND ${JAVA_COMPILE} @${JAVAC_ARGS_FILE}
        COMMAND ${JAVA_ARCHIVE} cfm "${P_JAR_FILE}" "${P_MANIFEST_MF}" -C "${BIN_DIR}" .
        DEPENDS ${JAVA_SOURCE_FILES_FULL}
        WORKING_DIRECTORY "${P_SRC_DIR}"
        COMMENT Compiling java ${P_BUILD_NAME}
        VERBATIM
    )
ENDMACRO()

GENERATE_MANIFEST_MF("${SEVENZIPJBINDING_MANIFEST}" "7-Zip-JBinding")
GENERATE_MANIFEST_MF("${SEVENZIPJBINDING_LIB_MANIFEST}" "7-Zip-JBinding native lib (${PLATFORM})")
CREATE_COMPILE_JAVA_CUSTOM_COMMAND(core "${JAVA_SOURCE_DIR}" "${SEVENZIPJBINDING_MANIFEST}" "${SEVENZIPJBINDING_JAR}")

ADD_CUSTOM_TARGET(sevenzipjbinding-jar ALL
                  DEPENDS ${SEVENZIPJBINDING_JAR}
                  COMMENT Processing java build target)

ADD_SUBDIRECTORY(jbinding-cpp)

IF(BUILD_MYSPLIT)
    ADD_SUBDIRECTORY(tools/mysplit)
ENDIF()

ADD_DEPENDENCIES(7-Zip-JBinding sevenzipjbinding-jar)

cmake_policy(SET CMP0026 OLD)
# TODO: Not allowed due to policy CMP0026
GET_TARGET_PROPERTY(SEVENZIP_JBINDING_LIB 7-Zip-JBinding LOCATION)
GET_FILENAME_COMPONENT(SEVENZIP_JBINDING_LIB_FILENAME "${SEVENZIP_JBINDING_LIB}" NAME)

FILE(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/${PLATFORM})
STRING(RANDOM LENGTH 12 BUILD_REF)

SET(SEVENZIPJBINDING_LIB_PROPERTY_FILE ${PROJECT_BINARY_DIR}/${PLATFORM}/sevenzipjbinding-lib.properties)
SET(SEVENZIPJBINDING_PLATFORMS_PROPERTY_FILE sevenzipjbinding-platforms.properties)
FILE(WRITE ${PROJECT_BINARY_DIR}/${SEVENZIPJBINDING_PLATFORMS_PROPERTY_FILE} "platform.1=${PLATFORM}\n")

IF(MINGW)
    SET(MAKE_COMMAND "mingw32-make")
ELSE()
    SET(MAKE_COMMAND "make")
ENDIF()

MESSAGE("")
MESSAGE("Build type: ${CMAKE_BUILD_TYPE}, platform: ${PLATFORM}")
MESSAGE("")
MESSAGE("Java VM: ${JAVA_RUNTIME}")
MESSAGE("Java compiler: ${JAVA_COMPILE}")
MESSAGE("Java header compiler: ${JAVA_HEADER_COMPILE}")
MESSAGE("Java documentation tool: ${JAVA_DOC}")
MESSAGE("Java archiver: ${JAVA_ARCHIVE}")
MESSAGE("MinGW: ${MINGW}")
MESSAGE("MinGW-32: ${MINGW32}")
MESSAGE("MinGW-64: ${MINGW64}")
MESSAGE("APPLE: ${APPLE}")
IF(RUNTIME_LIB)
    MESSAGE("Runtime libaray to use: ${RUNTIME_LIB_FILENAME}")
ENDIF()
MESSAGE("Output package: ./${SEVENZIPJBINDING_FILENAME}.zip")
MESSAGE("")
MESSAGE("")
MESSAGE("Type '${MAKE_COMMAND}' to compile")
MESSAGE("Type 'ctest' to run tests (it could take up to 90 minutes on a slow CPU)")
MESSAGE("Type '${MAKE_COMMAND} package' to build a distribution package")
MESSAGE("")

ADD_CUSTOM_TARGET(sevenzipjbinding-lib-jar ALL
    DEPENDS ${SEVENZIPJBINDING_LIB_JAR}) 

ADD_DEPENDENCIES(sevenzipjbinding-lib-jar 7-Zip-JBinding) #sevenzipjbinding-jar

SET(LIB_PROPERTY_FILE_CREATOR ${PROJECT_BINARY_DIR}/LibPropertyFileCreator.cmake)
FILE(WRITE ${LIB_PROPERTY_FILE_CREATOR} "
    FILE(SHA1 \"${PROJECT_BINARY_DIR}/jbinding-cpp/${SEVENZIP_JBINDING_LIB_FILENAME}\" HASH1)
    FILE(WRITE  \"${SEVENZIPJBINDING_LIB_PROPERTY_FILE}\" \"build.ref=${BUILD_REF}\\n\\n\")
    FILE(APPEND \"${SEVENZIPJBINDING_LIB_PROPERTY_FILE}\" \"lib.1.name=${SEVENZIP_JBINDING_LIB_FILENAME}\\n\")
    FILE(APPEND \"${SEVENZIPJBINDING_LIB_PROPERTY_FILE}\" \"lib.1.hash=\${HASH1}\\n\")
")

IF(RUNTIME_LIB)
    FILE(APPEND ${LIB_PROPERTY_FILE_CREATOR} "
        FILE(SHA1 \"${RUNTIME_LIB_FILENAME}\" HASH2)
        FILE(APPEND \"${SEVENZIPJBINDING_LIB_PROPERTY_FILE}\" \"lib.2.name=${RUNTIME_LIB}\\n\")
        FILE(APPEND \"${SEVENZIPJBINDING_LIB_PROPERTY_FILE}\" \"lib.2.hash=\${HASH2}\\n\")
    ")
ENDIF(RUNTIME_LIB)


ADD_CUSTOM_COMMAND(OUTPUT ${SEVENZIPJBINDING_LIB_PROPERTY_FILE}
                   COMMAND ${CMAKE_COMMAND} -P ${LIB_PROPERTY_FILE_CREATOR}
                   DEPENDS 7-Zip-JBinding
                   WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
                   COMMENT Calculating hash of the native libraries)

ADD_CUSTOM_COMMAND(OUTPUT ${SEVENZIPJBINDING_LIB_JAR}
                   COMMAND ${CMAKE_COMMAND} -E copy
                                           ${PROJECT_BINARY_DIR}/jbinding-cpp/${SEVENZIP_JBINDING_LIB_FILENAME}
                                           ${PLATFORM}
                   COMMAND ${JAVA_ARCHIVE} cfm "${SEVENZIPJBINDING_LIB_JAR}" "${SEVENZIPJBINDING_LIB_MANIFEST}"
                                           -C ${PROJECT_BINARY_DIR}
                                           ${PLATFORM}
                   COMMAND ${JAVA_ARCHIVE} uf ${SEVENZIPJBINDING_LIB_JAR} 
                                           -C ${PROJECT_BINARY_DIR}
                                           ${SEVENZIPJBINDING_PLATFORMS_PROPERTY_FILE}
                   DEPENDS ${SEVENZIPJBINDING_LIB_PROPERTY_FILE}
                   WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
                   COMMENT Building Platformdependend jar)

IF(RUNTIME_LIB)
    ADD_CUSTOM_COMMAND(TARGET sevenzipjbinding-lib-jar POST_BUILD
                   COMMAND ${CMAKE_COMMAND} -E copy
                                           ${RUNTIME_LIB_FILENAME}
                                           ${PLATFORM}
                   COMMAND ${JAVA_ARCHIVE} uf ${SEVENZIPJBINDING_LIB_JAR} 
                                               -C ${PROJECT_BINARY_DIR}
                                               ${PLATFORM}/${MINGW_RUNTIME_DLL_NAME}
                   WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
                   COMMENT Adding MinGW runtime dll to the jar)
ENDIF()


# ------------------- CPack -------------------

SET(CPACK_PACKAGE_DESCRIPTION_SUMMARY "7-Zip-JBinding - java binding for p7zip, crossplatform version of 7-Zip.")
SET(CPACK_PACKAGE_DESCRIPTION_SUMMARY ZIP)
SET(CPACK_GENERATOR ZIP)
SET(CPACK_SOURCE_GENERATOR ZIP)
SET(CPACK_PACKAGE_FILE_NAME ${SEVENZIPJBINDING_FILENAME})
SET(CPACK_SOURCE_PACKAGE_FILE_NAME ${SEVENZIPJBINDING_SOURCE_FILENAME})
SET(CPACK_SOURCE_IGNORE_FILES "/\\\\.git/;doc/src")
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
    EXECUTE_PROCESS(COMMAND \"${JAVA_DOC}\"
                                -public -version -author -quiet
                                -sourcepath ${PROJECT_SOURCE_DIR}/jbinding-java/src 
                                -subpackages net.sf.sevenzipjbinding 
                                -d ${JAVADOC_DIR}
                    WORKING_DIRECTORY .
    )
    
    # Copy javadoc back into the source folder
    EXECUTE_PROCESS(COMMAND ${CMAKE_COMMAND}
                                -E copy_directory
                                .
                                \"${PROJECT_SOURCE_DIR}/doc/web/javadoc\"
                    WORKING_DIRECTORY \"${JAVADOC_DIR}\"
    )
    
    # Generate web site
    EXECUTE_PROCESS(COMMAND ${CMAKE_COMMAND}
                                -D SEVENZIPJBINDING_VERSION=${SEVENZIPJBINDING_VERSON}
                                -P \"${PROJECT_SOURCE_DIR}/doc/build_html.cmake\"
                                ${PROJECT_SOURCE_DIR}/doc/web/javadoc
                    WORKING_DIRECTORY \"${PROJECT_SOURCE_DIR}/doc/\"
    )
    
    COPY_FROM_SRC(\"${PROJECT_SOURCE_DIR}/jbinding-java/src\" \"${PREPACKAGE_TMP}/java-src/\" \"\")
    COPY_FROM_SRC(\"${PROJECT_SOURCE_DIR}/jbinding-cpp\" \"${PREPACKAGE_TMP}/cpp-src/\" CMakeLists.txt)

    EXECUTE_PROCESS(COMMAND \"${JAVA_ARCHIVE}\" cMf ${PREPACKAGE_TMP}/java-src.zip .
    WORKING_DIRECTORY ${PREPACKAGE_TMP}/java-src/)

    EXECUTE_PROCESS(COMMAND \"${JAVA_ARCHIVE}\" cMf ${PREPACKAGE_TMP}/cpp-src.zip .
    WORKING_DIRECTORY ${PREPACKAGE_TMP}/cpp-src/)

    EXECUTE_PROCESS(COMMAND \"${JAVA_ARCHIVE}\" cMf ${PREPACKAGE_TMP}/javadoc.zip .
    WORKING_DIRECTORY ${JAVADOC_DIR})

    EXECUTE_PROCESS(COMMAND \"${JAVA_ARCHIVE}\" cMf ${PREPACKAGE_TMP}/website.zip .
    WORKING_DIRECTORY \"${PROJECT_SOURCE_DIR}/doc/web\")
"
)

FOREACH(PREPACKAGE_ACTION ${PREPACKAGE_ACTIONS})
    INSTALL(CODE "${PREPACKAGE_ACTION}")
ENDFOREACH(PREPACKAGE_ACTION ${PREPACKAGE_ACTIONS})

INSTALL(FILES   AUTHORS
                ChangeLog
                COPYING
                LGPL
                README
                ReleaseNotes.txt
                THANKS
        DESTINATION .)
INSTALL(FILES	CMakeFile-bin-dist.txt
	DESTINATION .
	RENAME CMakeLists.txt)
INSTALL(FILES   ${SEVENZIPJBINDING_JAR} 
                ${SEVENZIPJBINDING_LIB_JAR}
        DESTINATION lib)

INSTALL(FILES   ${PREPACKAGE_TMP}/java-src.zip
                ${PREPACKAGE_TMP}/cpp-src.zip
                ${PREPACKAGE_TMP}/javadoc.zip
                ${PREPACKAGE_TMP}/website.zip
        DESTINATION .)

#INSTALL(DIRECTORY ${JAVADOC_DIR}/
#        DESTINATION javadoc)

#INSTALL(DIRECTORY ${PROJECT_SOURCE_DIR}/jbinding-java/src/
#        DESTINATION java-src
#        REGEX "/\\.svn/" EXCLUDE)


# -------------------------- CTest

SET(DART_TESTING_TIMEOUT "36000" CACHE STRING "")	# Increase time out up to 10 hours
INCLUDE(CTest)
IF(BUILD_TESTING)
    MESSAGE("-- Prepare for testing")
    SET(JAVA_TEST_SOURCE_DIR "${PROJECT_SOURCE_DIR}/test/JavaTests/src")
    SET(TESTS_JAR "${PROJECT_BINARY_DIR}/jbinding-java/sevenzipjbinding-tests.jar")
    SET(TESTS_MANIFEST "${PROJECT_BINARY_DIR}/jbinding-java/test-MANIFEST.MF")
    SET(JUNIT_LIB "${PROJECT_SOURCE_DIR}/test/JavaTests/lib/junit-4.11.jar")
    SET(HAMCREST_LIB "${PROJECT_SOURCE_DIR}/test/JavaTests/lib/hamcrest-core-1.3.jar")
    
    GENERATE_MANIFEST_MF("${TESTS_MANIFEST}" "7-Zip-JBinding tests")
    CREATE_COMPILE_JAVA_CUSTOM_COMMAND(test "${JAVA_TEST_SOURCE_DIR}" "${TESTS_MANIFEST}" "${TESTS_JAR}" "${JUNIT_LIB}" "${HAMCREST_LIB}" "${SEVENZIPJBINDING_JAR}")

    ADD_CUSTOM_TARGET(sevenzipjbinding-test-jar ALL
                      DEPENDS 7-Zip-JBinding ${TESTS_JAR}
                      COMMENT Processing java build target)


    SET(JUNIT_TEST_RUNNER ${PROJECT_BINARY_DIR}/JUnitRunner.cmake)
    FILE(WRITE ${JUNIT_TEST_RUNNER} "STRING(REPLACE \"%3D\" \"=\" JAVA_OPT_TO_USE \"\${JAVA_OPT}\")
                                     EXECUTE_PROCESS(COMMAND
                                            \"${JAVA_RUNTIME}\" -Xmx512m -cp \"${JUNIT_LIB}${PATH_SEP}${HAMCREST_LIB}${PATH_SEP}${TESTS_JAR}${PATH_SEP}${SEVENZIPJBINDING_JAR}${PATH_SEP}${SEVENZIPJBINDING_LIB_JAR}\"
                                            \"-DSINGLEBUNDLE=\${SINGLEBUNDLE}\" \${JAVA_OPT_TO_USE} ")
    IF(JAVA_TMP)
       FILE(APPEND ${JUNIT_TEST_RUNNER} "\"-Djava.io.tmpdir=${JAVA_TMP}\" ")
    ENDIF()
    IF(JAVA_PARAMS)
       FILE(APPEND ${JUNIT_TEST_RUNNER} "${JAVA_PARAMS} ")
    ENDIF()

    FILE(APPEND ${JUNIT_TEST_RUNNER} "\${RUNTIME_JAVA_OPTS} ")

    IF(NOT(CMAKE_BUILD_TYPE STREQUAL "Debug"))
       FILE(APPEND ${JUNIT_TEST_RUNNER} "\"-Dskip-debug-mode-tests=true\" ")
    ENDIF()

    FILE(APPEND ${JUNIT_TEST_RUNNER} "org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite
                                            WORKING_DIRECTORY ${JAVA_TEST_SOURCE_DIR}/..
                                            RESULT_VARIABLE RESULT)
                                     IF(RESULT)
                                         MESSAGE(SEND_ERROR \"Error during JUnit Tests. Return code: \${RESULT}\")
                                     ENDIF(RESULT)
                                     ")
    SET(RUNTIME_PARAMS -D "RUNTIME_JAVA_OPTS=\$ENV{RUNTIME_JAVA_OPTS}")
    add_test(JUnit-common                       ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Common tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-common-no-privileged-init    ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Common tests" -D "JAVA_OPT=-Dsevenzip.no_doprivileged_initialization%3D1" -P ${JUNIT_TEST_RUNNER})

    add_test(JUnit-init-std-1                   ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Init tests (Std) 1" -D "JAVA_OPT=-Dsevenziptest.standard_initialization_test_phase%3D1" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-init-std-2                   ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Init tests (Std) 2" -D "JAVA_OPT=-Dsevenziptest.standard_initialization_test_phase%3D2" -P ${JUNIT_TEST_RUNNER})

    add_test(JUnit-init-verify-1                ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Init tests (Verify) 1" -D "JAVA_OPT=-Dsevenziptest.standard_initialization_test_phase%3D1" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-init-verify-2                ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Init tests (Verify) 2" -D "JAVA_OPT=-Dsevenziptest.standard_initialization_test_phase%3D2" -P ${JUNIT_TEST_RUNNER})

    add_test(JUnit-tools                        ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Tools tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-snippets                     ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Snippets tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-snippets-no-privileged-init  ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Snippets tests" -D "JAVA_OPT=-Dsevenzip.no_doprivileged_initialization%3D1" -P ${JUNIT_TEST_RUNNER})
    
    add_test(JUnit-encoding-utf-8               ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Encoding tests 1" -D "JAVA_OPT=-Dfile.encoding%3DUTF8" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-encoding-cp1252              ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Encoding tests 2" -D "JAVA_OPT=-Dfile.encoding%3DCP1252" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-encoding-cp1251              ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Encoding tests 3" -D "JAVA_OPT=-Dfile.encoding%3DCP1251" -P ${JUNIT_TEST_RUNNER})

    add_test(JUnit-bug-reports                  ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Bug report tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-single-file-extraction       ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Single file tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-multiple-files-extraction    ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Multiple files tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-compression                  ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Compression tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-badarchive                   ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Bad archive tests" -P ${JUNIT_TEST_RUNNER})
    add_test(JUnit-misc                         ${CMAKE_COMMAND} ${RUNTIME_PARAMS} -D "SINGLEBUNDLE=Misc tests" -P ${JUNIT_TEST_RUNNER})
ENDIF(BUILD_TESTING)

