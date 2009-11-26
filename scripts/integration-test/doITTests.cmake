SET(URL "http://sevenzipjbind.sourceforge.net/download.php?filename=")
SET(IT_PACKAGE_NAME "sevenzipjbinding-it-test-pack.zip")
SET(ITROOT "SevenZip.ITTests")

FIND_PROGRAM(SEVEN_ZIP
        7z
    PATHS
        "${SEVEN_ZIP_DIR}"
        "[HKEY_CURRENT_USER\\Software\\7-Zip;Path]"
        "[HKEY_LOCAL_MACHINE\\SOFTWARE\\7-Zip;Path]"
        /bin            
        /usr/bin
        /usr/local/bin
    DOC "7z archive progr"
)

IF(NOT SEVEN_ZIP)
    MESSAGE(FATAL_ERROR, "Can't find 7z executable. Please use -DSEVEN_ZIP=/path/to/7z or -DSEVEN_ZIP_DIR=/path/to/")
ENDIF()

FIND_PROGRAM(JAVA
        java
    PATHS
        /bin            
        /usr/bin
        /usr/local/bin
    DOC "Java VM"
)

IF(NOT JAVA)
    MESSAGE(FATAL_ERROR, "Can't find java executable. Please use -DJAVA=/path/to/java")
ENDIF()


IF(EXISTS "${ITROOT}")
    FILE(REMOVE_RECURSE "${ITROOT}")
ENDIF()
FILE(MAKE_DIRECTORY "${ITROOT}")

MESSAGE("Download integration test pack from ${URL}${IT_PACKAGE_NAME}")

FILE(DOWNLOAD "${URL}${IT_PACKAGE_NAME}" "${ITROOT}/${IT_PACKAGE_NAME}" STATUS status LOG log)
list(GET status 0 num_status)
IF (num_status GREATER 0)
    list(GET status 1 str_status)
    MESSAGE(FATAL_ERROR "Error downloading file: ${str_status}\n\n${log}")
ENDIF()

MESSAGE("Extract ${ITROOT}/${IT_PACKAGE_NAME} using ${SEVEN_ZIP}")
EXECUTE_PROCESS(COMMAND ${SEVEN_ZIP} -y x "${IT_PACKAGE_NAME}"
                WORKING_DIRECTORY ${ITROOT}
                RESULT_VARIABLE archive_result
                OUTPUT_VARIABLE archive_output 
                ERROR_VARIABLE archive_err)
IF(archive_result)
    MESSAGE(FATAL_ERROR "Error extracting archive ${IT_PACKAGE_NAME}\n\n${archive_err}")
ENDIF()
MESSAGE("Extraction OK")

FILE(GLOB FULL_ITTEST_NAME_LIST "${ITROOT}/*")
MESSAGE("${FULL_ITTEST_NAME_LIST}")

FOREACH(FULL_ITTEST_NAME ${FULL_ITTEST_NAME_LIST})
    IF(NOT FULL_ITTEST_NAME MATCHES ".*\\.zip")
        STRING(REGEX REPLACE "^.*/([^/]+)$" "\\1" ITTEST_DIR "${FULL_ITTEST_NAME}")
        SET(ITTEST_DIR "${ITROOT}/${ITTEST_DIR}")
        STRING(REGEX REPLACE "^.*/sevenzipjbinding-it-test-([^/]+)$" "\\1" VERSION "${FULL_ITTEST_NAME}")
        BREAK()
    ENDIF()
ENDFOREACH()

IF(NOT VERSION)
    MESSAGE(FATAL_ERROR "Couldn't recognize release version")
ENDIF()


MACRO(TEST_PACKAGE PLATFORM)
    SET(RELEASE_NAME "sevenzipjbinding-${VERSION}-${PLATFORM}")
    MESSAGE("Download release from ${URL}${RELEASE_NAME}")

    FILE(DOWNLOAD "${URL}${RELEASE_NAME}.zip" "${ITTEST_DIR}/${RELEASE_NAME}.zip" STATUS status LOG log)
    list(GET status 0 num_status)
    IF (num_status GREATER 0)
        list(GET status 1 str_status)
        MESSAGE(FATAL_ERROR "Error downloading file: ${str_status}\n\n${log}")
    ENDIF()

    MESSAGE("Extract ${ITTEST_DIR}/${RELEASE_NAME}.zip using ${SEVEN_ZIP}")
    EXECUTE_PROCESS(COMMAND ${SEVEN_ZIP} -y x "${RELEASE_NAME}.zip"
                    WORKING_DIRECTORY "${ITTEST_DIR}"
                    RESULT_VARIABLE archive_result
                    OUTPUT_VARIABLE archive_output 
                    ERROR_VARIABLE archive_err)
    IF(archive_result)
        MESSAGE(FATAL_ERROR "Error extracting archive ${ITTEST_DIR}/${RELEASE_NAME}\n\n${archive_output}\n${archive_err}")
    ENDIF()

    FILE(GLOB SEVENZIPJBINDING_LIBS "${ITTEST_DIR}/lib/s*")
    FOREACH(FILE ${SEVENZIPJBINDING_LIBS})
        FILE(REMOVE "${FILE}")
    ENDFOREACH()

    FILE(GLOB SEVENZIPJBINDING_LIBS "${ITTEST_DIR}/${RELEASE_NAME}/lib/s*")
    FOREACH(FILE ${SEVENZIPJBINDING_LIBS})
        STRING(REGEX REPLACE "^.*/([^/]+)$" "\\1" NEW_FILENAME "${FILE}")
        STRING(REGEX REPLACE "^(sevenzipjbinding-)[^.]+(.jar)$" "\\1Platform\\2" NEW_FILENAME "${NEW_FILENAME}")
        EXECUTE_PROCESS(COMMAND ${CMAKE_COMMAND} -E copy "${FILE}" "${ITTEST_DIR}/lib/${NEW_FILENAME}")
    ENDFOREACH()

    EXECUTE_PROCESS(COMMAND ${CMAKE_CTEST_COMMAND} -D Experimental
                    WORKING_DIRECTORY "${ITTEST_DIR}")
ENDMACRO()

# Single platform packages
#TEST_PACKAGE("Linux-amd64")
TEST_PACKAGE("Linux-i386")
#TEST_PACKAGE("Mac-i386")
#TEST_PACKAGE("Mac-x86_64")
#TEST_PACKAGE("Windows-amd64")
#TEST_PACKAGE("Windows-x86")

# Multiplatform packages
#TEST_PACKAGE("AllLinux")
#TEST_PACKAGE("AllWindows")
#TEST_PACKAGE("AllMac")
#TEST_PACKAGE("AllPlatforms")

