cmake_policy(SET CMP0007 NEW)
SET(SNIPPETS_DIRECTORY_JAVA "web/snippets")
SET(SNIPPETS_DIRECTORY_HTML "web.components/snippets")
SET(OUTPUT_DIRECTORY_HTML "web.components/output")

macro(PROCESS_SNIPPET_LINE_JAVA LINE_VAR)
    STRING(REGEX REPLACE "//$" "" TMP "${${LINE_VAR}}")
    STRING(REGEX REPLACE "[ \t]*<br>$" "" TMP "${TMP}")
    STRING(REGEX REPLACE "/\\*f\\*/([^/]+)/\\*([^*]*)\\*/" "\\1\\2" TMP "${TMP}")
    STRING(REGEX REPLACE "/\\*sf\\*/([^/]+)/\\*([^*]*)\\*/" "\\1\\2" TMP "${TMP}")
    STRING(REGEX REPLACE "/\\*s\\*/([^/]+)/\\*([^*]*)\\*/" "\\1\\2" TMP "${TMP}")
    STRING(REGEX REPLACE "/\\*(  +)\\*/" "\\1" TMP "${TMP}")
    SET(${LINE_VAR} "${TMP}")
endmacro()


macro(PROCESS_SNIPPET_COMMON_LINE_HTML LINE_VAR)
    STRING(REGEX REPLACE "[ \t]*<br>$" "" TMP "${${LINE_VAR}}")
    STRING(REGEX REPLACE "//$" "" TMP "${TMP}")
    STRING(REGEX REPLACE "&" "&amp;" TMP "${TMP}")
    STRING(REGEX REPLACE "<" "&#60;" TMP "${TMP}")
    STRING(REGEX REPLACE ">" "&#62;" TMP "${TMP}")
#    STRING(REGEX REPLACE " " "&nbsp;" TMP "${TMP}")
#    STRING(REGEX REPLACE "\t" "&nbsp;&nbsp;&nbsp;&nbsp;" TMP "${TMP}")
    STRING(REGEX REPLACE "@see" "<span class=\"java-javadoc-keyword\">@see</span>" TMP "${TMP}") 
    SET(${LINE_VAR} "${TMP}")
endmacro()

macro(PROCESS_SNIPPET_JAVADOC_LINE_HTML LINE_VAR)
    PROCESS_SNIPPET_COMMON_LINE_HTML(${LINE_VAR})
    STRING(REGEX REPLACE "code" "CoDe" TMP "${${LINE_VAR}}")
    SET(${LINE_VAR} "${TMP}")
endmacro()

macro(PROCESS_SNIPPET_LINE_HTML LINE_VAR)
    PROCESS_SNIPPET_COMMON_LINE_HTML(${LINE_VAR})

    STRING(REGEX REPLACE "(\"[^\"]+\")" "##string##\\1##end##" TMP "${${LINE_VAR}}")
    STRING(REGEX REPLACE "(^|[^a-zA-Z])(implements|null|import|if|for|int|byte|long|new|void|try|catch|finally|throws|throw|return|break|class|static|public|private|switch|case|default|final|extends|this|else|true|false)($|[^a-zA-Z])" 
                            "\\1##keyword##\\2##end##\\3" TMP "${TMP}")
    STRING(REGEX REPLACE "(SevenZip\\.)(openInArchive|initSevenZipFromPlatformJAR)"
                            "\\1##staticmethod##\\2##end##" TMP "${TMP}")
    STRING(REGEX REPLACE "(System\\.)(exit)"
                            "\\1##staticmethod##\\2##end##" TMP "${TMP}")
    STRING(REGEX REPLACE "(ArchiveFormat\\.)(ZIP)" "\\1##staticfield##\\2##end##" TMP "${TMP}")
    STRING(REGEX REPLACE "(System\\.)(out|err)" "\\1##staticfield##\\2##end##" TMP "${TMP}")
    STRING(REGEX REPLACE "(Arrays\\.)(hashCode)" "\\1##staticmethod##\\2##end##" TMP "${TMP}")
    STRING(REGEX REPLACE "(String\\.)(format)" "\\1##staticmethod##\\2##end##" TMP "${TMP}")
    STRING(REGEX REPLACE "(Integer\\.)(valueOf)" "\\1##staticmethod##\\2##end##" TMP "${TMP}")
    
    # Remove highlighint inside a "// ..." comment
    WHILE("${TMP}" MATCHES ".*(//[^#]*)##[a-z]+##(.*)$")
        STRING(REGEX REPLACE "(//[^#]*)##[a-z]+##(.*)$" "\\1\\2" TMP "${TMP}")
    ENDWHILE()
    
    STRING(REGEX REPLACE "(//.*)$" "##comment##\\1##end##" TMP "${TMP}")
    STRING(REGEX REPLACE "/\\*f\\*/([^/]+)/\\*([^*]*)\\*/" "##field##\\1##end##\\2" TMP "${TMP}")
    STRING(REGEX REPLACE "/\\*sf\\*/([^/]+)/\\*([^*]*)\\*/" "##staticfield##\\1##end##\\2" TMP "${TMP}")
    STRING(REGEX REPLACE "/\\*s\\*/([^/]+)/\\*([^*]*)\\*/" "##staticmethod##\\1##end##\\2" TMP "${TMP}")

    STRING(REGEX REPLACE "##end##" "</span>" TMP "${TMP}") 
    STRING(REGEX REPLACE "##string##" "<span class=\"java-string\">" TMP "${TMP}") 
    STRING(REGEX REPLACE "##staticmethod##" "<span class=\"java-staticmethod\">" TMP "${TMP}") 
    STRING(REGEX REPLACE "##staticfield##" "<span class=\"java-staticfield\">" TMP "${TMP}") 
    STRING(REGEX REPLACE "##comment##" "<span class=\"java-comment\">" TMP "${TMP}") 
    STRING(REGEX REPLACE "##keyword##" "<span class=\"java-keyword\">" TMP "${TMP}") 
    STRING(REGEX REPLACE "##field##" "<span class=\"java-field\">" TMP "${TMP}") 
    STRING(REGEX REPLACE "/\\*(  +)\\*/" "\\1" TMP "${TMP}")
#    MESSAGE("${TMP}")
    SET(${LINE_VAR} "${TMP}")
endmacro() 

macro(PROCESS_OUTPUT_LINE_HTML LINE_VAR)
    STRING(REGEX REPLACE "&" "&amp;" TMP "${${LINE_VAR}}")
    STRING(REGEX REPLACE "<" "&#60;" TMP "${TMP}")
    STRING(REGEX REPLACE ">" "&#62;" TMP "${TMP}")
    STRING(REGEX REPLACE "\\\\n" "\n" TMP "${TMP}")
    SET(${LINE_VAR} "${TMP}")
endmacro()

macro(PROCESS_JAVADOC_CLASSES LINE_VAR)

    # find . -name "*.html" | sort
    SET(JAVADOC_HTML_FILES      
        net/sf/sevenzipjbinding/ArchiveFormat.html
        net/sf/sevenzipjbinding/ExtractAskMode.html
        net/sf/sevenzipjbinding/ExtractOperationResult.html
        net/sf/sevenzipjbinding/IArchiveExtractCallback.html
        net/sf/sevenzipjbinding/IArchiveOpenCallback.html
        net/sf/sevenzipjbinding/IArchiveOpenVolumeCallback.html
        net/sf/sevenzipjbinding/ICryptoGetTextPassword.html
        net/sf/sevenzipjbinding/IInArchive.html
        net/sf/sevenzipjbinding/IInStream.html
        net/sf/sevenzipjbinding/impl/InArchiveImpl.html
        net/sf/sevenzipjbinding/impl/InputStreamSequentialInStream.html
        net/sf/sevenzipjbinding/impl/OutArchive7zImpl.html
        net/sf/sevenzipjbinding/impl/OutArchiveBZip2Impl.html
        net/sf/sevenzipjbinding/impl/OutArchiveGZipImpl.html
        net/sf/sevenzipjbinding/impl/OutArchiveImpl.html
        net/sf/sevenzipjbinding/impl/OutArchiveTarImpl.html
        net/sf/sevenzipjbinding/impl/OutArchiveZipImpl.html
        net/sf/sevenzipjbinding/impl/package-frame.html
        net/sf/sevenzipjbinding/impl/package-summary.html
        net/sf/sevenzipjbinding/impl/package-tree.html
        net/sf/sevenzipjbinding/impl/RandomAccessFileInStream.html
        net/sf/sevenzipjbinding/impl/RandomAccessFileOutStream.html
        net/sf/sevenzipjbinding/impl/SequentialInStreamImpl.html
        net/sf/sevenzipjbinding/impl/VolumedArchiveInStream.html
        net/sf/sevenzipjbinding/IOutArchive.html
        net/sf/sevenzipjbinding/IOutCreateArchive7z.html
        net/sf/sevenzipjbinding/IOutCreateArchiveBZip2.html
        net/sf/sevenzipjbinding/IOutCreateArchiveGZip.html
        net/sf/sevenzipjbinding/IOutCreateArchive.html
        net/sf/sevenzipjbinding/IOutCreateArchiveTar.html
        net/sf/sevenzipjbinding/IOutCreateArchiveZip.html
        net/sf/sevenzipjbinding/IOutCreateCallbackBase.html
        net/sf/sevenzipjbinding/IOutCreateCallbackGeneric.html
        net/sf/sevenzipjbinding/IOutCreateCallback.html
        net/sf/sevenzipjbinding/IOutFeatureSetLevel.html
        net/sf/sevenzipjbinding/IOutFeatureSetMultithreading.html
        net/sf/sevenzipjbinding/IOutFeatureSetSolid.html
        net/sf/sevenzipjbinding/IOutItemCallback7z.html
        net/sf/sevenzipjbinding/IOutItemCallbackBase.html
        net/sf/sevenzipjbinding/IOutItemCallbackBZip2.html
        net/sf/sevenzipjbinding/IOutItemCallbackGZip.html
        net/sf/sevenzipjbinding/IOutItemCallback.html
        net/sf/sevenzipjbinding/IOutItemCallbackTar.html
        net/sf/sevenzipjbinding/IOutItemCallbackZip.html
        net/sf/sevenzipjbinding/IOutStream.html
        net/sf/sevenzipjbinding/IOutUpdateArchive7z.html
        net/sf/sevenzipjbinding/IOutUpdateArchiveBZip2.html
        net/sf/sevenzipjbinding/IOutUpdateArchiveGZip.html
        net/sf/sevenzipjbinding/IOutUpdateArchive.html
        net/sf/sevenzipjbinding/IOutUpdateArchiveTar.html
        net/sf/sevenzipjbinding/IOutUpdateArchiveZip.html
        net/sf/sevenzipjbinding/IOutUpdateCallbackBase.html
        net/sf/sevenzipjbinding/IOutUpdateCallbackGeneric.html
        net/sf/sevenzipjbinding/IOutUpdateCallback.html
        net/sf/sevenzipjbinding/IProgress.html
        net/sf/sevenzipjbinding/IPropertyProvider7z.html
        net/sf/sevenzipjbinding/ISeekableStream.html
        net/sf/sevenzipjbinding/ISequentialInStream.html
        net/sf/sevenzipjbinding/ISequentialOutStream.html
        net/sf/sevenzipjbinding/NCoderPropID.html
        net/sf/sevenzipjbinding/NFileTimeType.html
        net/sf/sevenzipjbinding/package-frame.html
        net/sf/sevenzipjbinding/package-summary.html
        net/sf/sevenzipjbinding/package-tree.html
        net/sf/sevenzipjbinding/PropertyInfo.html
        net/sf/sevenzipjbinding/PropID.AttributesBitMask.html
        net/sf/sevenzipjbinding/PropID.html
        net/sf/sevenzipjbinding/SevenZipException.html
        net/sf/sevenzipjbinding/SevenZip.html
        net/sf/sevenzipjbinding/SevenZipNativeInitializationException.html
        net/sf/sevenzipjbinding/simple/impl/package-frame.html
        net/sf/sevenzipjbinding/simple/impl/package-summary.html
        net/sf/sevenzipjbinding/simple/impl/package-tree.html
        net/sf/sevenzipjbinding/simple/impl/SimpleInArchiveImpl.html
        net/sf/sevenzipjbinding/simple/impl/SimpleInArchiveItemImpl.html
        net/sf/sevenzipjbinding/simple/ISimpleInArchive.html
        net/sf/sevenzipjbinding/simple/ISimpleInArchiveItem.html
        net/sf/sevenzipjbinding/simple/package-frame.html
        net/sf/sevenzipjbinding/simple/package-summary.html
        net/sf/sevenzipjbinding/simple/package-tree.html
        net/sf/sevenzipjbinding/util/ByteArrayStream.html
        net/sf/sevenzipjbinding/util/package-frame.html
        net/sf/sevenzipjbinding/util/package-summary.html
        net/sf/sevenzipjbinding/util/package-tree.html
    )
    SET(TMP "${${LINE_VAR}}")
    FOREACH(HTML_FILE ${JAVADOC_HTML_FILES})
        IF(NOT EXISTS "javadoc/${JD_PATH}${HTML_FILE}")
            MESSAGE(FATAL_ERROR "HTML file not found: 'javadoc/${JD_PATH}${HTML_FILE}'")
        ENDIF()
    ENDFOREACH()
    FOREACH(HTML_FILE ${JAVADOC_HTML_FILES})
        STRING(REGEX REPLACE ".*/([^/]+)\\.html" "\\1" CLASS_NAME "${HTML_FILE}")
        STRING(REGEX REPLACE "([^a-zA-Z0-9>/]|^)(${CLASS_NAME})([^a-zA-Z0-9]|$)" 
                "\\1<a href=\"javadoc/${JD_PATH}${HTML_FILE}\">\\2</a>\\3" TMP "${TMP}")
#        STRING(REGEX REPLACE "${CLASS_NAME}" 
#                "XXXXX" TMP "${TMP}")
    ENDFOREACH()
    SET(${LINE_VAR} "${TMP}")
endmacro()


macro(ADJUST_PADDING SNIPPET_FILE)
    SET(PADDING -1)
    file(STRINGS ${SNIPPET_FILE} SNIPPET_FILE_LINES)
    SET(SNIPPET_NAME "")
    SET(FIRST_LINE_SET 0)
    LIST(LENGTH SNIPPET_FILE_LINES LINES_COUNT)
    MATH(EXPR LINES_COUNT "${LINES_COUNT}-1")
    SET(FIRST_LINE "")
    FOREACH(INDEX RANGE ${LINES_COUNT})
        LIST(GET SNIPPET_FILE_LINES ${INDEX} LINE)
#        MESSAGE("LINE: ${LINE}")
        STRING(REPLACE "\t" "    " LINE "${LINE}")
        IF(NOT FIRST_LINE_SET)
            SET(FIRST_LINE "${LINE}")
            SET(FIRST_LINE_SET 1)
#            MESSAGE("FIRST_LINE: ${FIRST_LINE}")
            string(LENGTH "${LINE}" PADDING) 
        ELSE()
            IF(LINE)
                SET(I 0)
                SET(C1 "x")
                SET(C2 "x")
                WHILE("${C1}" STREQUAL "${C2}" )
                    #MESSAGE("LINE: ${FIRST_LINE}  ${I}")
                    # if ERROR here: verify, that the line after the BEGIN_SNIPPET is not an empty line
                    STRING(SUBSTRING "${FIRST_LINE}" ${I} 1 C1)
                    STRING(SUBSTRING "${LINE}" ${I} 1 C2)
                    MATH(EXPR I "${I} + 1")
                ENDWHILE()
                MATH(EXPR I "${I} - 1")
            ENDIF()
        ENDIF()
    ENDFOREACH()
    FILE(WRITE "${SNIPPET_FILE}" "")
    FOREACH(INDEX RANGE ${LINES_COUNT})
        LIST(GET SNIPPET_FILE_LINES ${INDEX} LINE)
        IF (LINE)
            STRING(REPLACE "\t" "    " LINE "${LINE}")
            string(LENGTH "${LINE}" LENGTH)
            MATH(EXPR LENGTH "${LENGTH} - ${I}") 
            STRING(SUBSTRING "${LINE}" ${I} ${LENGTH} LINE)
            FILE(APPEND "${SNIPPET_FILE}" "${LINE}\n")
        ELSE()
            FILE(APPEND "${SNIPPET_FILE}" "${LINE}\n")
        ENDIF()
#        MESSAGE("${I}${LINE}")
    ENDFOREACH()
endmacro()


macro(PROCESS_SNIPPET SNIPPET_FILE)
    file(STRINGS ${SNIPPET_FILE} JAVA_FILE_LINES)
    SET(SNIPPET_NAME "")
    SET(OUTPUT_NAME "")
    SET(IS_IN_JAVADOC 0)
    LIST(LENGTH JAVA_FILE_LINES LINES_COUNT)
    MATH(EXPR LINES_COUNT "${LINES_COUNT}-1")
    FOREACH(INDEX RANGE ${LINES_COUNT})
        LIST(GET JAVA_FILE_LINES ${INDEX} LINE)
        IF(LINE MATCHES "/\\* +END_SNIPPET +\\*/")
            ADJUST_PADDING("${SNIPPET_OUTPUT_FILENAME_HTML}")
            ADJUST_PADDING("${SNIPPET_OUTPUT_FILENAME_JAVA}")
            SET(SNIPPET_NAME "")
        ENDIF()
        IF(LINE MATCHES "/\\* +END_OUTPUT +\\*/")
            #ADJUST_PADDING("${OUTPUT_FILENAME_HTML}")
            SET(OUTPUT_NAME "")
        ENDIF()
        IF(NOT OUTPUT_NAME STREQUAL "")
            STRING(REGEX REPLACE ".*\"([^\"]+)\".*" "\\1" OUTPUT_LINE ${LINE})
            IF (NOT LINE STREQUAL "")
                PROCESS_OUTPUT_LINE_HTML(OUTPUT_LINE)
                FILE(APPEND ${OUTPUT_FILENAME_HTML} "${OUTPUT_LINE}")
            ENDIF()
        ENDIF()
        IF(NOT SNIPPET_NAME STREQUAL "")
            SET(JAVA_LINE "${LINE}")
            SET(HTML_LINE "${LINE}")
            PROCESS_SNIPPET_LINE_JAVA(JAVA_LINE)
            FILE(APPEND ${SNIPPET_OUTPUT_FILENAME_JAVA} "${JAVA_LINE}")
            FILE(APPEND ${SNIPPET_OUTPUT_FILENAME_JAVA} "\n")

            SET(HTML_PREFIX "")
            SET(HTML_POSTFIX "")
            IF(HTML_LINE MATCHES "^[ \t]*/\\*\\*[ \t]*$")
                SET(IS_IN_JAVADOC 1)
                SET(HTML_PREFIX "<span class=\"java-javadoc\">")
            ENDIF()
            IF(HTML_LINE MATCHES "^[ \t]*\\*/[ \t]*$")
                SET(IS_IN_JAVADOC 0)
                SET(HTML_POSTFIX "</span>")
            ENDIF()
            IF(IS_IN_JAVADOC)
                PROCESS_SNIPPET_JAVADOC_LINE_HTML(HTML_LINE)
            ELSE()
                PROCESS_SNIPPET_LINE_HTML(HTML_LINE)
            ENDIF()
            FILE(APPEND ${SNIPPET_OUTPUT_FILENAME_HTML} "${HTML_PREFIX}${HTML_LINE}${HTML_POSTFIX}")
            FILE(APPEND ${SNIPPET_OUTPUT_FILENAME_HTML} "\n")
        ENDIF()
        IF(LINE MATCHES "/\\* +BEGIN_SNIPPET\\([a-zA-Z0-9]+\\) +\\*/")
            STRING(REGEX REPLACE ".*\\(([a-zA-Z0-9]+)\\).*" "\\1" SNIPPET_NAME ${LINE})
            MESSAGE("Found snippet: ${SNIPPET_NAME}")
            SET(SNIPPET_OUTPUT_FILENAME_HTML "${SNIPPETS_DIRECTORY_HTML}/${SNIPPET_NAME}.html")
            SET(SNIPPET_OUTPUT_FILENAME_JAVA "${SNIPPETS_DIRECTORY_JAVA}/${SNIPPET_NAME}.java")
            FILE(WRITE ${SNIPPET_OUTPUT_FILENAME_HTML} "")
            FILE(WRITE ${SNIPPET_OUTPUT_FILENAME_JAVA} "")
        ENDIF()
        IF(LINE MATCHES "/\\* +BEGIN_OUTPUT\\([a-zA-Z0-9]+\\) +\\*/")
            STRING(REGEX REPLACE ".*\\(([a-zA-Z0-9]+)\\).*" "\\1" OUTPUT_NAME ${LINE})
            MESSAGE("Found output block: ${OUTPUT_NAME}")
            SET(OUTPUT_FILENAME_HTML "${OUTPUT_DIRECTORY_HTML}/${OUTPUT_NAME}.html")
            FILE(WRITE ${OUTPUT_FILENAME_HTML} "")
        ENDIF()
    ENDFOREACH()
endmacro()

function(APPEND_FILE OUTPUT_FILE FILE_TO_APPEND)
    file(STRINGS ${FILE_TO_APPEND} FILE_LINES)
    SET(SNIPPET_NAME "")
    LIST(LENGTH FILE_LINES LINES_COUNT)
    MATH(EXPR LINES_COUNT "${LINES_COUNT}-1")
    FOREACH(INDEX RANGE ${LINES_COUNT})
        LIST(GET FILE_LINES ${INDEX} LINE)
        PROCESS_JAVADOC_CLASSES(LINE)
        FILE(APPEND "${OUTPUT_FILE}" "${LINE}\n")
    ENDFOREACH()
endfunction()

macro(PROCESS_HTML FILENAME)
    MESSAGE("Processing HMTL: ${FILENAME}")
    SET(INPUT_HTML_FILE "web.components/${FILENAME}")
    SET(OUTPUT_HTML_FILE "web/${FILENAME}")

    file(STRINGS "${INPUT_HTML_FILE}" LINES)
    file(WRITE "${OUTPUT_HTML_FILE}" "")
    APPEND_FILE("${OUTPUT_HTML_FILE}" "web.components/header.html")
    FOREACH(LINE ${LINES})
        IF(LINE MATCHES "^[ \t]*##INCLUDE_SNIPPET\\([a-zA-Z0-9]+\\)[ \t]*$")
            STRING(REGEX REPLACE "^[ \t]*##INCLUDE_SNIPPET\\(([a-zA-Z0-9]+)\\)[ \t]*$" "\\1" SNIPPET_NAME ${LINE})
            MESSAGE("- including snippet: ${SNIPPET_NAME}")
            APPEND_FILE("${OUTPUT_HTML_FILE}" "web.components/snippet_header.html")
            APPEND_FILE("${OUTPUT_HTML_FILE}" "${SNIPPETS_DIRECTORY_HTML}/${SNIPPET_NAME}.html")
            APPEND_FILE("${OUTPUT_HTML_FILE}" "web.components/snippet_footer.html")
            FILE(APPEND "${OUTPUT_HTML_FILE}" "<div class=\"snippet-info\"><a href=\"snippets/${SNIPPET_NAME}.java\">download java code</a></div>\n")

        ELSEIF(LINE MATCHES "^[ \t]*##INCLUDE_OUTPUT\\([a-zA-Z0-9]+\\)[ \t]*$")
            STRING(REGEX REPLACE "^[ \t]*##INCLUDE_OUTPUT\\(([a-zA-Z0-9]+)\\)[ \t]*$" "\\1" OUTPUT_NAME ${LINE})
            MESSAGE("- including output: ${OUTPUT_NAME}")
            APPEND_FILE("${OUTPUT_HTML_FILE}" "web.components/output_header.html")
            APPEND_FILE("${OUTPUT_HTML_FILE}" "${OUTPUT_DIRECTORY_HTML}/${OUTPUT_NAME}.html")
            APPEND_FILE("${OUTPUT_HTML_FILE}" "web.components/output_footer.html")
        ELSE()
            PROCESS_JAVADOC_CLASSES(LINE)
            FILE(APPEND "${OUTPUT_HTML_FILE}" "${LINE}\n")
        ENDIF()
    ENDFOREACH()
    APPEND_FILE("${OUTPUT_HTML_FILE}" "web.components/footer.html")
endmacro()

FILE(GLOB JAVA_FILES "../test/JavaTests/src/net/sf/sevenzipjbinding/junit/snippets/*.java")
FOREACH(JAVA_FILE ${JAVA_FILES})
    PROCESS_SNIPPET("${JAVA_FILE}")
ENDFOREACH()


PROCESS_HTML("compression_snippets.html")
PROCESS_HTML("extraction_snippets.html")
PROCESS_HTML("first_steps.html")
PROCESS_HTML("index.html")
