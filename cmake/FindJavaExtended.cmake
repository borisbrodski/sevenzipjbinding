#
# Adds the seach to javah (header generator) the cmake default version
# of FindJava.cmake
# Addionaly to the cmake's defaults (refer to FindJava.cmake) i.e.
#    JAVA_RUNTIME,
#    JAVA_COMPILE and
#    JAVA_ARCHIVE,
# this code sets the following variables:
#    JAVA_FOUND           = ON when java was found, OFF otherwise
#    JAVA_HEADER_COMPILE  = the full path to Java header generator
#

# Call cmake default version
FIND_PACKAGE( Java )

FIND_PROGRAM( JAVA_HEADER_COMPILE
  javah
  PATHS "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.4;JavaHome]/bin"
        "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.3;JavaHome]/bin"
        /usr/bin
        /usr/lib/java/bin
        /usr/share/java/bin
        /usr/local/bin
        /usr/local/java/bin
)
MARK_AS_ADVANCED( JAVA_HEADER_COMPILE )
  

IF( JAVA_COMPILE AND JAVA_ARCHIVE AND JAVA_HEADER_COMPILE )
  SET( JAVA_FOUND "ON" )
ELSE( JAVA_COMPILE AND JAVA_ARCHIVE AND JAVA_HEADER_COMPILE )
  SET( JAVA_FOUND "OFF" )
ENDIF( JAVA_COMPILE AND JAVA_ARCHIVE AND JAVA_HEADER_COMPILE )
