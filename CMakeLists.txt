CMAKE_MINIMUM_REQUIRED(VERSION 2.6)
PROJECT(SevenZipJBinding)

INCLUDE(FindJava)
INCLUDE(CTest)


# Uncomment this to build a version
#SET(SEVENZIPJBINDING_VERSON 0.5.0)

IF (SEVENZIPJBINDING_VERSON)
    SET(RELEASE_PATH ${PROJECT_BINARY_DIR}/releases/${SEVENZIPJBINDING_VERSON})
    SET(SEVENZIPJBINDING_VERSON_POSTFIX "-${SEVENZIPJBINDING_VERSON}")
ELSE(SEVENZIPJBINDING_VERSON)
    SET(RELEASE_PATH ${PROJECT_BINARY_DIR})
    SET(SEVENZIPJBINDING_VERSON_POSTFIX "")
ENDIF(SEVENZIPJBINDING_VERSON)

SET(PLATFORM ${CMAKE_SYSTEM_NAME}-${CMAKE_SYSTEM_PROCESSOR})
SET(SEVENZIPJBINDING_LIB_JAR ${PROJECT_BINARY_DIR}/sevenzipjbinding-${PLATFORM}.jar)
SET(SEVENZIPJBINDING_ZIP ${RELEASE_PATH}/sevenzipjbinding${SEVENZIPJBINDING_VERSON_POSTFIX}.zip)


ADD_SUBDIRECTORY(jbinding-java)
ADD_SUBDIRECTORY(jbinding-cpp)

ADD_DEPENDENCIES(7-Zip-JBinding sevenzip-jbinding-jar)

GET_PROPERTY(SEVENZIP_JBINDING_JAR    GLOBAL PROPERTY     SEVENZIP_JBINDING_JAR) 
GET_PROPERTY(USE_MINGW                GLOBAL PROPERTY     USE_MINGW) 
GET_TARGET_PROPERTY(SEVENZIP_JBINDING_LIB 7-Zip-JBinding LOCATION)
GET_FILENAME_COMPONENT(SEVENZIP_JBINDING_LIB_FILENAME "${SEVENZIP_JBINDING_LIB}" NAME)

SET(SEVENZIP_JBINDING_LIB_PROPERTY_FILE sevenzipjbinding-lib.properties)
FILE(WRITE ${PROJECT_BINARY_DIR}/${SEVENZIP_JBINDING_LIB_PROPERTY_FILE} "sevenzipjbinding.libname.1=${SEVENZIP_JBINDING_LIB_FILENAME}\n")
IF(USE_MINGW)
    IF (MINWGM10_DLL_FILENAME MATCHES "/.*" AND USE_CYGWIN)
        IF(NOT CYGPATH_EXE_FILENAME)
            MESSAGE(FATAL_ERROR "cygpath.exe wasn't found")
        ENDIF(NOT CYGPATH_EXE_FILENAME)
        EXECUTE_PROCESS(COMMAND cygpath.exe -m ${MINWGM10_DLL_FILENAME} OUTPUT_VARIABLE MINWGM10_DLL_FILENAME)
    ENDIF()
    GET_FILENAME_COMPONENT(MINWGM10_DLL_DIR ${MINWGM10_DLL_FILENAME} PATH)
    GET_FILENAME_COMPONENT(MINWGM10_DLL_NAME ${MINWGM10_DLL_FILENAME} NAME)
    FILE(APPEND ${PROJECT_BINARY_DIR}/${SEVENZIP_JBINDING_LIB_PROPERTY_FILE} "sevenzipjbinding.libname.2=${MINWGM10_DLL_NAME}")
ENDIF(USE_MINGW)

MESSAGE("----------------------------")
MESSAGE("Platform: ${PLATFORM}")
MESSAGE("LIB: ${SEVENZIP_JBINDING_LIB}")
MESSAGE("LIB-Name: ${SEVENZIP_JBINDING_LIB_FILENAME}")
MESSAGE("JAR: ${SEVENZIP_JBINDING_JAR}")
MESSAGE("SEVENZIPJBINDING_ZIP: ${SEVENZIPJBINDING_ZIP}")
MESSAGE("mingwm10.dll: ${MINWGM10_DLL_FILENAME}")
MESSAGE("-Tools:")
MESSAGE("JAR: ${JAVA_ARCHIVE}")
MESSAGE("----------------------------")

FILE(REMOVE ${SEVENZIPJBINDING_ZIP})


ADD_CUSTOM_TARGET(SevenZipJBinding-lib-jar ALL
    DEPENDS ${SEVENZIPJBINDING_LIB_JAR}) 

ADD_DEPENDENCIES(SevenZipJBinding-lib-jar 7-Zip-JBinding) #sevenzip-jbinding-jar

IF(USE_MINGW)
    ADD_CUSTOM_COMMAND(TARGET SevenZipJBinding-lib-jar POST_BUILD
                       COMMAND ${JAVA_ARCHIVE} uf ${SEVENZIPJBINDING_LIB_JAR} 
                                               -C ${MINWGM10_DLL_DIR}
                                               ${MINWGM10_DLL_NAME}
                       WORKING_DIRECTORY ${PROJECT_BINARY_DIR}
                       COMMENT Adding mingwm10.dll to the jar)
ENDIF(USE_MINGW)

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



# ------------------- CPack -------------------

SET(CPACK_PACKAGE_DESCRIPTION_SUMMARY "SevenZip-JBinding - java binding for p7zip, crossplatform version of 7-Zip.")
SET(CPACK_PACKAGE_DESCRIPTION_SUMMARY ZIP)
SET(CPACK_GENERATOR ZIP)
SET(CPACK_SOURCE_GENERATOR ZIP)
#SET(CPACK_SOURCE_IGNORE_FILES /CVS/;/\\.svn/;\\.swp$;\\.#;/#;.*~;cscope.*)

INCLUDE(CPack)

SET(PREPACKAGE_TMP "${PROJECT_BINARY_DIR}/prepackage.tmp")
SET(JAVADOC_DIR "${PREPACKAGE_TMP}/javadoc")
FILE(REMOVE_RECURSE ${PREPACKAGE_TMP})
FILE(MAKE_DIRECTORY ${PREPACKAGE_TMP})

SET(PREPACKAGE_ACTIONS 
    "EXECUTE_PROCESS(COMMAND javadoc 
                                -public -version -author
                                -sourcepath ${PROJECT_SOURCE_DIR}/jbinding-java/src 
                                -subpackages net.sf.sevenzipjbinding 
                                -d ${JAVADOC_DIR}
    WORKING_DIRECTORY .)"

    "EXECUTE_PROCESS(COMMAND zip -q -9 -r ${PREPACKAGE_TMP}/src.zip . -x \"/*.svn/*\"
    WORKING_DIRECTORY ${PROJECT_SOURCE_DIR}/jbinding-java/src/)"

    "EXECUTE_PROCESS(COMMAND zip -q -9 -r ${PREPACKAGE_TMP}/javadoc.zip .
    WORKING_DIRECTORY javadoc/)"
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
                release-notes.txt
                THANKS
        DESTINATION .)
INSTALL(FILES   ${SEVENZIP_JBINDING_JAR} 
                ${SEVENZIPJBINDING_LIB_JAR}
        DESTINATION lib)

INSTALL(FILES   ${PREPACKAGE_TMP}/src.zip
                ${PREPACKAGE_TMP}/javadoc.zip
        DESTINATION .)

#INSTALL(DIRECTORY ${JAVADOC_DIR}/
#        DESTINATION javadoc)

#INSTALL(DIRECTORY ${PROJECT_SOURCE_DIR}/jbinding-java/src/
#        DESTINATION java-src
#        REGEX "/\\.svn/" EXCLUDE)


# -------------------------- CTest

IF(BUILD_TESTING)
    MESSAGE("Prepare for testing")
    GET_PROPERTY(JAVA_BIN_DIR GLOBAL PROPERTY JAVA_BIN_DIR)
    
    SET(JUNIT_JAVA_SOURCE_DIR ${PROJECT_SOURCE_DIR}/test/JavaTests/src)
    SET(JUNIT_JAVA_BIN_DIR ${PROJECT_BINARY_DIR}/bin-junit)
    SET(JUNIT_JAVAC_ARGS_FILE ${PROJECT_BINARY_DIR}/javac-args-junit.tmp)
    SET(JUNIT_LIB ${PROJECT_SOURCE_DIR}/test/JavaTests/lib/junit-4.6.jar)

    FILE(MAKE_DIRECTORY ${JUNIT_JAVA_BIN_DIR})
    FILE(GLOB_RECURSE JUNIT_JAVA_SOURCE_FILES RELATIVE ${JUNIT_JAVA_SOURCE_DIR} ${JUNIT_JAVA_SOURCE_DIR}/*.java)

    IF(USE_MINGW)
        SET(PATH_SEP ";")
    ELSE(USE_MINGW)
        SET(PATH_SEP ":")
    ENDIF(USE_MINGW)
    
    FILE(WRITE ${JUNIT_JAVAC_ARGS_FILE} "-source 1.5 -target 1.5\n")
    FILE(APPEND ${JUNIT_JAVAC_ARGS_FILE} "-d ${JUNIT_JAVA_BIN_DIR} -s ${JUNIT_JAVA_BIN_DIR}\n")
    FILE(APPEND ${JUNIT_JAVAC_ARGS_FILE} "-cp ${JUNIT_LIB}${PATH_SEP}${JAVA_BIN_DIR}\n")
    FOREACH(F ${JUNIT_JAVA_SOURCE_FILES})
        FILE(APPEND ${JUNIT_JAVAC_ARGS_FILE} "${F}\n")
    ENDFOREACH(F)
    
    ADD_CUSTOM_TARGET(SevenZipJBinding-tests ALL
        COMMAND ${JAVA_COMPILE} @${JUNIT_JAVAC_ARGS_FILE}
        DEPENDS ${SEVENZIPJBINDING_LIB_JAR}
        WORKING_DIRECTORY ${JUNIT_JAVA_SOURCE_DIR}
        COMMENT Compiling test classes)

    MESSAGE("JUnit-Tests ${JAVA_RUNTIME} -cp ${JUNIT_LIB}:${JUNIT_JAVA_BIN_DIR}:${JAVA_BIN_DIR}:${SEVENZIPJBINDING_LIB_JAR} org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite")

    SET(JUNIT_TEST_RUNNER ${PROJECT_BINARY_DIR}/JUnitRunner.cmake)
    FILE(WRITE ${JUNIT_TEST_RUNNER} "EXECUTE_PROCESS(COMMAND
                                            ${JAVA_RUNTIME} -cp \"${JUNIT_LIB}${PATH_SEP}${JUNIT_JAVA_BIN_DIR}${PATH_SEP}${JAVA_BIN_DIR}${PATH_SEP}${SEVENZIPJBINDING_LIB_JAR}\"
                                            org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite
                                            WORKING_DIRECTORY ${JUNIT_JAVA_SOURCE_DIR}/..
                                            RESULT_VARIABLE RESULT)
                                     IF(RESULT)
                                         MESSAGE(SEND_ERROR \"Error during JUnit Tests\")
                                     ENDIF(RESULT)
                                     ")
    add_test(JUnit-Tests ${CMAKE_COMMAND} -P ${JUNIT_TEST_RUNNER})
#                                         org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite) JUnitInitializationTest
ENDIF(BUILD_TESTING)
