set(UPLOAD_URL "boris_brodski,sevenzipjbind@frs.sourceforge.net:/home/frs/project/s/se/sevenzipjbind/OldFiles/")

FIND_PROGRAM(SCP
        scp
    PATHS
        /bin            
        /usr/bin
        /usr/local/bin
        C:/Programme/Git/bin
    DOC "Secure copy tool"
)

IF(NOT SCP)
    MESSAGE(FATAL_ERROR, "Can't find secure copy tool SCP. Please use -DSCP=/path/to/scp")
ENDIF()

macro(UPLOAD_FILE FILENAME)
    message("Uploading: ${FILENAME}")
    
    
    execute_process(COMMAND ${SCP} ${FILENAME} "${UPLOAD_URL}"
                    RESULT_VARIABLE RESULT)
    if(RESULT)
        message(FATAL_ERROR "Error uploading file: ${RESULT}")
    endif()
endmacro()

if(FILENAME)
    UPLOAD_FILE("${FILENAME}")
else()
    file(GLOB FILENAMES "sevenzipjbinding-*.zip")
    SET(FILES "")
    foreach(FILENAME ${FILENAMES})
        #UPLOAD_FILE("${FILENAME}")
        SET(FILES "${FILES} ${FILENAME}")
    endforeach()
    UPLOAD_FILE("${FILENAMES}")
endif()

