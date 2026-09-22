# Add extended seach to the java tools.
#
# Input variables (for example, through -Dvar=value cmake option)
# - JAVA_JDK - path to jdk8 or higher 
# - JAVA_HOME - path to jdk8 or higher 
#
# Sets:
#  - JAVA_COMPILE        (<jdk>/bin/javac)
#  - JAVA_RUNTIME        (<jdk>/bin/java)
#  - JAVA_HEADER_COMPILE (<jdk>/bin/javah)
#  - JAVA_DOC            (<jdk>/bin/javadoc)
#  - JAVA_ARCHIVE        (<jdk>/bin/jar)
#  - JAVA_INCLUDE_PATH   (<jdk>/include) Path to jni.h
#  - JAVA_ARCH           (System.getProperty("os.arch"))

SET(JAVA_JDK CACHE PATH "Path to JDK 8 or higher")
IF(NOT JAVA_JDK_OLD)
    SET(JAVA_JDK_OLD CACHE INTERNAL "Internal: Old copy of JAVA_JDK")
ENDIF()
IF(JAVA_HOME)
    SET(JAVA_JDK "${JAVA_HOME}")
ENDIF()
SET(HELP
"Please set JAVA_HOME to jdk8 or higher or use -DJAVA_JDK=<path-to-jdk> switch for cmake.
Don't forget to delete 'CMakeCache.txt' file, if you want '-D' parameter to take effect.")

IF(NOT "${JAVA_JDK}" STREQUAL "${JAVA_JDK_OLD}")
    IF(NOT JAVA_JDK_OLD STREQUAL "")
        MESSAGE("Java JDK path (JAVA_JDK) was changed. Redo search...")
    ENDIF()
    SET(JAVA_COMPILE                 JAVA_COMPILE-NOTFOUND)
    SET(JAVA_RUNTIME                 JAVA_RUNTIME-NOTFOUND)
    SET(JAVA_HEADER_COMPILE          JAVA_HEADER_COMPILE-NOTFOUND)
    SET(JAVA_DOC                     JAVA_DOC-NOTFOUND)
    SET(JAVA_ARCHIVE                 JAVA_ARCHIVE-NOTFOUND)
    SET(JAVA_INCLUDE_PATH            JAVA_INCLUDE_PATH-NOTFOUND)
    
    SET(JAVA_JDK_OLD "${JAVA_JDK}" CACHE INTERNAL "Internal: Old copy of JAVA_JDK" FORCE)
ENDIF()

IF(NOT JAVA_INCLUDE_PATH)
    MESSAGE("-- Looking for java JNI jni.h include path")
    FIND_FILE(JAVA_JNI_H
                jni.h
            PATHS
                "${JAVA_JDK}/include"
                "$ENV{JAVA_HOME}/include"
            NO_CMAKE_FIND_ROOT_PATH
    )
    
    IF(NOT JAVA_JNI_H)
        INCLUDE(FindJNI)
        
        SET(JNI_INCLUDE_DIRS CACHE INTERNAL "Ignored" FORCE)
        SET(JNI_LIBRARIES CACHE INTERNAL "Ignored" FORCE)
        SET(JAVA_AWT_LIBRARY CACHE INTERNAL "Ignored" FORCE)
        SET(JAVA_JVM_LIBRARY CACHE INTERNAL "Ignored" FORCE)
#        SET(JAVA_INCLUDE_PATH CACHE INTERNAL "Ignored" FORCE)
#        SET(JAVA_INCLUDE_PATH2 CACHE INTERNAL "Ignored" FORCE)
        SET(JAVA_AWT_INCLUDE_PATH CACHE INTERNAL "Ignored" FORCE)
    ELSE()
        GET_FILENAME_COMPONENT(JAVA_INCLUDE_PATH_VAR "${JAVA_JNI_H}" PATH)
        SET(JAVA_INCLUDE_PATH "${JAVA_INCLUDE_PATH_VAR}" CACHE PATH "Include path for jni.h")
        MARK_AS_ADVANCED(JAVA_INCLUDE_PATH)
    ENDIF()
    SET(JAVA_JNI_H CACHE INTERNAL "Ignored" FORCE)
    
    IF(JAVA_INCLUDE_PATH)
        MESSAGE("-- Looking for java JNI jni.h include path - found: ${JAVA_INCLUDE_PATH}")
    ELSE()
        MESSAGE(FATAL_ERROR "Java JNI jni.h include file not found. ${HELP}")
    ENDIF()
ENDIF()


IF(NOT JAVA_INCLUDE_PATH2)
    MESSAGE("-- Looking for java JNI jni_md.h include path")
    FIND_FILE(JAVA_JNI_MD_H
                jni_md.h
            PATHS
                "${JAVA_INCLUDE_PATH}/win32"
                "${JAVA_INCLUDE_PATH}/linux"
                "${JAVA_INCLUDE_PATH}/freebsd"
                "${JAVA_INCLUDE_PATH}/netbsd"
                "${JAVA_INCLUDE_PATH}/darwin"
            NO_CMAKE_FIND_ROOT_PATH
    )

    IF(JAVA_JNI_MD_H)
        GET_FILENAME_COMPONENT(JAVA_INCLUDE_PATH2_VAR "${JAVA_JNI_MD_H}" PATH)
        SET(JAVA_INCLUDE_PATH2 "${JAVA_INCLUDE_PATH2_VAR}" CACHE PATH "Include path for jni_md.h")
        MARK_AS_ADVANCED(JAVA_INCLUDE_PATH2)
        SET(JAVA_JNI_MD_H CACHE INTERNAL "Ignored" FORCE)
        MESSAGE("-- Looking for java JNI jni_md.h include path - found: ${JAVA_INCLUDE_PATH2}")
    ELSE()
        MESSAGE(FATAL_ERROR "Java JNI jni_md.h include file not found. ${HELP}")
    ENDIF()
ENDIF()


GET_FILENAME_COMPONENT(JAVA_JNI_JDK_PATH "${JAVA_INCLUDE_PATH}/.." ABSOLUTE)


IF(NOT JAVA_COMPILE)
    MESSAGE("-- Looking for java compiler 'javac'")
    FIND_PROGRAM(JAVA_COMPILE
            javac
        PATHS 
            "${JAVA_JDK}/bin"
            "$ENV{JAVA_HOME}/bin"
            "${JAVA_JNI_JDK_PATH}/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.4;JavaHome]/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.3;JavaHome]/bin"
            /usr/bin
            /usr/lib/java/bin
            /usr/share/java/bin
            /usr/local/bin
            /usr/local/java/bin
        DOC "'javac': java compiler"
        NO_DEFAULT_PATH
        NO_CMAKE_ENVIRONMENT_PATH
        NO_CMAKE_PATH
        NO_SYSTEM_ENVIRONMENT_PATH
        NO_CMAKE_SYSTEM_PATH
        NO_CMAKE_FIND_ROOT_PATH
    )
    FIND_PROGRAM(JAVA_COMPILE
            javac
    )
    MARK_AS_ADVANCED(JAVA_COMPILE)
    
    IF(JAVA_COMPILE)
        MESSAGE("-- Looking for java compiler 'javac' - found: ${JAVA_COMPILE}")
    ELSE()
        MESSAGE(FATAL_ERROR "Java compiler 'javac' not found. ${HELP}")
    ENDIF()
ENDIF()


IF(NOT JAVA_HEADER_COMPILE)
    MESSAGE("-- Looking for java header compiler 'javah'")
    FIND_PROGRAM(JAVA_HEADER_COMPILE
      javah
      PATHS 
            "${JAVA_JDK}/bin"
            "$ENV{JAVA_HOME}/bin"
            "${JAVA_JNI_JDK_PATH}/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.4;JavaHome]/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.3;JavaHome]/bin"
            /usr/bin
            /usr/lib/java/bin
            /usr/share/java/bin
            /usr/local/bin
            /usr/local/java/bin
        DOC "'javah' tool to use"
        NO_DEFAULT_PATH
        NO_CMAKE_ENVIRONMENT_PATH
        NO_CMAKE_PATH
        NO_SYSTEM_ENVIRONMENT_PATH
        NO_CMAKE_SYSTEM_PATH
        NO_CMAKE_FIND_ROOT_PATH
    )
    FIND_PROGRAM(JAVA_HEADER_COMPILE
      javah
    )
    MARK_AS_ADVANCED(JAVA_HEADER_COMPILE)
    
    IF(JAVA_HEADER_COMPILE)
        MESSAGE("-- Looking for java header compiler 'javah' - found: ${JAVA_HEADER_COMPILE}")
        SET(JAVA_HEADER_USE_JAVAC No CACHE INTERNAL "Generate JNI headers with 'javac -h' instead of 'javah'")
    ELSE()
        # 'javah' was removed in JDK 10. Fall back to 'javac -h' (available since JDK 8), which
        # generates identical JNI headers from the sources. This is required for toolchains where no
        # JDK<=9 is available - e.g. Windows/arm64, for which no JDK 8 exists at all (Java 8 predates
        # Windows-on-Arm). The produced .h files and the resulting jar stay Java-8 compatible.
        MESSAGE("-- 'javah' not found (removed in JDK 10+) - will generate JNI headers with 'javac -h'")
        SET(JAVA_HEADER_COMPILE "${JAVA_COMPILE}")
        SET(JAVA_HEADER_USE_JAVAC Yes CACHE INTERNAL "Generate JNI headers with 'javac -h' instead of 'javah'")
    ENDIF()
ENDIF()


IF(NOT JAVA_DOC)
    MESSAGE("-- Looking for java API Documentation Generator 'javadoc'")
    FIND_PROGRAM(JAVA_DOC
            javadoc
        PATHS 
            "${JAVA_JDK}/bin"
            "$ENV{JAVA_HOME}/bin"
            "${JAVA_JNI_JDK_PATH}/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.4;JavaHome]/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.3;JavaHome]/bin"
            /usr/bin
            /usr/lib/java/bin
            /usr/share/java/bin
            /usr/local/bin
            /usr/local/java/bin
        DOC "'javadoc' tool to use"
        NO_DEFAULT_PATH
        NO_CMAKE_ENVIRONMENT_PATH
        NO_CMAKE_PATH
        NO_SYSTEM_ENVIRONMENT_PATH
        NO_CMAKE_SYSTEM_PATH
        NO_CMAKE_FIND_ROOT_PATH
    )
    FIND_PROGRAM(JAVA_DOC
            javadoc
    )
    MARK_AS_ADVANCED(JAVA_DOC)
    
    IF(JAVA_DOC)
        MESSAGE("-- Looking for java API Documentation Generator 'javadoc' - found: ${JAVA_DOC}")
    ELSE()
        MESSAGE(FATAL_ERROR "Java API Documentation Generator 'javadoc' not found. ${HELP}")
    ENDIF()
ENDIF()


IF(NOT JAVA_RUNTIME)
    MESSAGE("-- Looking for java VM 'java'")
    FIND_PROGRAM(JAVA_RUNTIME
            java
        PATHS 
            "${JAVA_JDK}/bin"
            "$ENV{JAVA_HOME}/bin"
            "${JAVA_JNI_JDK_PATH}/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.4;JavaHome]/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.3;JavaHome]/bin"
            /usr/bin
            /usr/lib/java/bin
            /usr/share/java/bin
            /usr/local/bin
            /usr/local/java/bin
        DOC "'java': java JVM"
        NO_DEFAULT_PATH
        NO_CMAKE_ENVIRONMENT_PATH
        NO_CMAKE_PATH
        NO_SYSTEM_ENVIRONMENT_PATH
        NO_CMAKE_SYSTEM_PATH
        NO_CMAKE_FIND_ROOT_PATH
    )
    FIND_PROGRAM(JAVA_RUNTIME
            java
    )
    MARK_AS_ADVANCED(JAVA_RUNTIME)
    
    IF(JAVA_RUNTIME)
        MESSAGE("-- Looking for java VM 'java' - found: ${JAVA_RUNTIME}")
    ELSE()
        MESSAGE(FATAL_ERROR "Java VM 'java' not found. ${HELP}")
    ENDIF()
ENDIF()


IF(NOT JAVA_ARCHIVE)
    MESSAGE("-- Looking for java archiver 'jar'")
    FIND_PROGRAM(JAVA_ARCHIVE
            jar
        PATHS 
            "${JAVA_JDK}/bin"
            "$ENV{JAVA_HOME}/bin"
            "${JAVA_JNI_JDK_PATH}/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.4;JavaHome]/bin"
            "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.3;JavaHome]/bin"
            /usr/bin
            /usr/lib/java/bin
            /usr/share/java/bin
            /usr/local/bin
            /usr/local/java/bin
        DOC "'jar': java archiver"
        NO_DEFAULT_PATH
        NO_CMAKE_ENVIRONMENT_PATH
        NO_CMAKE_PATH
        NO_SYSTEM_ENVIRONMENT_PATH
        NO_CMAKE_SYSTEM_PATH
        NO_CMAKE_FIND_ROOT_PATH
    )
    FIND_PROGRAM(JAVA_ARCHIVE
            jar
    )
    MARK_AS_ADVANCED(JAVA_ARCHIVE)
    
    IF(JAVA_ARCHIVE)
        MESSAGE("-- Looking for java archiver 'jar' - found: ${JAVA_ARCHIVE}")
    ELSE()
        MESSAGE(FATAL_ERROR "Java archiver 'jar' not found. ${HELP}")
    ENDIF()
ENDIF()


IF(NOT JAVAC_TEST_OK)
    MESSAGE("-- Checking java compile")
    
    SET(JAVAC_TEST_DIR "${PROJECT_BINARY_DIR}/javac-test")
    FILE(MAKE_DIRECTORY "${JAVAC_TEST_DIR}")
    
    # Test Java 8 stream API (lambda expressions)
    FILE(WRITE "${JAVAC_TEST_DIR}/JavaVersionTest.java" "
import java.util.Arrays;
import java.util.List;

public class JavaVersionTest {
    public static void main(String args[]) {
        List<String> list = Arrays.asList(\"a\", \"b\", \"c\");
        list.stream().forEach(s -> System.out.print(s));
    }
}")
    EXECUTE_PROCESS(COMMAND ${JAVA_COMPILE} JavaVersionTest.java 
                    WORKING_DIRECTORY ${JAVAC_TEST_DIR}
                    RESULT_VARIABLE version_test_result
                    OUTPUT_VARIABLE version_test_output 
                    ERROR_VARIABLE version_test_err)
    IF(version_test_result)
        MESSAGE(FATAL_ERROR "${JAVA_COMPILE} does not support Java 8 features (lambda expressions).
        
NOTE: Java 8 or higher is required in order to compile 7-Zip-JBinding.
Your JDK path: ${JAVA_JDK}
        
Javac error message: ${version_test_err}")
    ENDIF()
    
    FILE(WRITE "${JAVAC_TEST_DIR}/TestClass.java" "
public class TestClass {
    private static class Java15Test<T> {
        void doIt(T t) {t.toString();}
    }
    public static void main(String args[]) {
        new Java15Test<String>().doIt(\"Hello World\");
        System.out.print(System.getProperty(\"os.arch\"));
    }
}")
    EXECUTE_PROCESS(COMMAND ${JAVA_COMPILE} TestClass.java 
                    WORKING_DIRECTORY ${JAVAC_TEST_DIR}
                    RESULT_VARIABLE javac_test_result
                    OUTPUT_VARIABLE javac_test_output 
                    ERROR_VARIABLE javac_test_err)
    IF(javac_test_result)
        MESSAGE(FATAL_ERROR "${JAVA_COMPILE} can't compile simple java program.
        
NOTE: Java 8 or higher is required in order to compile 7-Zip-JBinding.
        
Javac error message: ${javac_test_err}")
    ENDIF()
        
    SET(JAVAC_TEST_OK "1" CACHE INTERNAL "Javac test passed")
    MESSAGE("-- Checking java compile - ok")
ENDIF()

IF(NOT JAVA_ARCH OR NOT JAVA_SYSTEM)
    MESSAGE("-- Checking java system properties")
    SET(JAVAC_TEST_DIR "${PROJECT_BINARY_DIR}/javac-test")
    IF(NOT EXISTS "${JAVAC_TEST_DIR}")
        FILE(MAKE_DIRECTORY "${JAVAC_TEST_DIR}")
    ENDIF()
    FILE(WRITE "${JAVAC_TEST_DIR}/JavaSystemPropertyTest.java" "
public class JavaSystemPropertyTest {
    public static void main(String args[]) {
        String property = System.getProperty(args[0]);
        System.out.print(property.split(\" \")[0]);
    }
}")
    EXECUTE_PROCESS(COMMAND ${JAVA_COMPILE} JavaSystemPropertyTest.java 
                    WORKING_DIRECTORY ${JAVAC_TEST_DIR}
                    RESULT_VARIABLE javac_result
                    OUTPUT_VARIABLE javac_output 
                    ERROR_VARIABLE javac_err)
    IF(javac_result)
        MESSAGE(FATAL_ERROR "${JAVA_COMPILE} can't compile simple java program.
        
NOTE: Java 8 or higher is required in order to compile 7-Zip-JBinding.
        
Javac error message: ${javac_err}")
    ENDIF()

    EXECUTE_PROCESS(COMMAND ${JAVA_RUNTIME} ${JAVA_PARAMS} JavaSystemPropertyTest os.arch
                    WORKING_DIRECTORY ${JAVAC_TEST_DIR}
                    RESULT_VARIABLE java_result
                    OUTPUT_VARIABLE java_output 
                    ERROR_VARIABLE java_err)
    IF(java_result)
        MESSAGE(FATAL_ERROR "${JAVA_RUNTIME} can't run simple java program.
        
NOTE: Java 8 or higher is required in order to compile 7-Zip-JBinding.
        
Javac error message: ${java_err}")
    ENDIF()
    SET(JAVA_ARCH "${java_output}" CACHE INTERNAL "Java os.name")

    EXECUTE_PROCESS(COMMAND ${JAVA_RUNTIME} ${JAVA_PARAMS} JavaSystemPropertyTest os.name
                    WORKING_DIRECTORY ${JAVAC_TEST_DIR}
                    RESULT_VARIABLE java_result
                    OUTPUT_VARIABLE java_output 
                    ERROR_VARIABLE java_err)
    IF(java_result)
        MESSAGE(FATAL_ERROR "${JAVA_RUNTIME} can't run simple java program.
        
NOTE: Java 8 or higher is required in order to compile 7-Zip-JBinding.
        
Javac error message: ${java_err}")
    ENDIF()

    SET(JAVA_SYSTEM "${java_output}" CACHE INTERNAL "Java os.arch")
    MESSAGE("-- Checking java compile - ok (arch: ${JAVA_ARCH}, system: ${JAVA_SYSTEM})")
ENDIF()


# Call cmake default version
#MESSAGE("JAVA_INCLUDE_PATH: ${JAVA_INCLUDE_PATH}")
#MESSAGE("JAVA_COMPILE: ${JAVA_COMPILE}")
#MESSAGE("JAVA_RUNTIME: ${JAVA_RUNTIME}")
#MESSAGE("JAVA_HEADER_COMPILE: ${JAVA_HEADER_COMPILE}")
#MESSAGE("JAVA_DOC: ${JAVA_DOC}")
#MESSAGE("JAVA_ARCHIVE: ${JAVA_ARCHIVE}")
