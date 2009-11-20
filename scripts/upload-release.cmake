set(UPLOAD_URL "http://sevenzipjbind.sourceforge.net/upload.php")
set(TMP_CHUCK_FILENAME "chunk.bin")
set(CHUNK_LENGTH 500000)


macro(UPLOAD_FILE FILENAME DESCRIPTION)
    execute_process(COMMAND ${CMAKE_COMMAND} -E md5sum ${FILENAME} OUTPUT_VARIABLE MD5)
    string(REGEX REPLACE "([a-zA-Z0-9]+).*" "\\1" MD5 "${MD5}")
    
    message("Uploading: ${FILENAME} md5: ${MD5}")
    
    file(GLOB CHUNKS  RELATIVE "${CMAKE_BINARY_DIR}" "chunk.*")
    foreach(CHUNK ${CHUNKS})
        file(REMOVE "${CHUNK}")
    endforeach()

    execute_process(COMMAND split "${FILENAME}" -b ${CHUNK_LENGTH} chunk.
                    RESULT_VARIABLE RESULT)
    if(RESULT)
        message(FATAL_ERROR "Split failed: ${RESULT}")
    endif()
    
    file(GLOB CHUNKS  RELATIVE "${CMAKE_BINARY_DIR}" "chunk.*")
    
    set(CHUNK_NUMBER 1)
    get_filename_component(FILEBASENAME "${FILENAME}" NAME)
    foreach(CHUNK ${CHUNKS})
        message("Uploading chunk ${CHUNK_NUMBER}: '${CHUNK}'")
        execute_process(COMMAND curl -T "${CHUNK}"
                                -H "Chunk:${CHUNK_NUMBER}"
                                -H "Filename:${FILEBASENAME}"
                                -H "Descr:${DESCRIPTION}"
                                "${UPLOAD_URL}"
                        RESULT_VARIABLE RESULT)
        if(RESULT)
            message(FATAL_ERROR "Error uploading chunk: ${RESULT}")
        endif()
        math(EXPR CHUNK_NUMBER "${CHUNK_NUMBER} + 1")
    endforeach()    
    
    file(WRITE "${TMP_CHUCK_FILENAME}" "x")    
    execute_process(COMMAND curl -T "${TMP_CHUCK_FILENAME}"
                            -H "Filename:${FILEBASENAME}"
                            -H "MD5:${MD5}"
                            "${UPLOAD_URL}"
                    RESULT_VARIABLE RESULT)
    file(REMOVE "${TMP_CHUCK_FILENAME}")
    foreach(CHUNK ${CHUNKS})
        FILE(REMOVE "${CHUNK}")
    endforeach()

    if(RESULT)
        message(FATAL_ERROR "Error uploading chunk: ${RESULT}")
    endif()
endmacro()

if(DEFINED DESCRIPTION)
    set(DESCRIPTION "")
endif()

if(FILENAME)
    UPLOAD_FILE("${FILENAME}" "${DESCRIPTION}")
else()
    file(GLOB FILENAMES "sevenzipjbinding-*.zip")
    foreach(FILENAME ${FILENAMES})
        UPLOAD_FILE("${FILENAME}" "Uploaded by cmake ${CMAKE_SYSTEM_NAME}, ${CMAKE_SYSTEM_PROCESSOR}, ${CMAKE_SYSTEM_VERSION}")
    endforeach()
endif()

#UPLOAD_FILE("/home/boris/tmp/Diplom.pdf")
#UPLOAD_FILE("/home/boris/tmp/simple2.dat" "Test file for cmake")
#UPLOAD_FILE("filex.txt" "Test file for cmake")
#UPLOAD_FILE("1.bin" "Test file for cmake")
#UPLOAD_FILE("/home/boris/Coding/SevenZipJBinding/Releases/4.65-1.10rc-extr-only/sevenzipjbinding-4.65-1.10rc-extr-only-AllLinux.zip" "Reallife test")