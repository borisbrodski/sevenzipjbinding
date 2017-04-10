@echo off
SET MY_DIR=%~dp0

REM * 7-Zip-JBinding should be build with Java 5
SET JAVA_JDK=C:/Program Files/Java/jdk1.5.0_22

REM * MinGW to use
SET MINGW_HOME=D:/Coding/x86_64-6.2.0-release-win32-sjlj-rt_v5-rev1

REM * Set additional DLL to be packed with 7-Zip-JBinding native library
REM * Required for SEH MinGW versions.
REM SET RUNTIME_LIB=libgcc_s_seh-1.dll
SET RUNTIME_LIB=


SET CMD_MAKE=%MINGW_HOME%/bin/mingw32-make.exe
SET CMD_GCC=%MINGW_HOME%/bin/gcc.exe
SET CMD_GPP=%MINGW_HOME%/bin/g++.exe
SET CMD_WINDRES=%MINGW_HOME%/bin/windres.exe

SET CMAKE_TOOLCHAIN=-G"MinGW Makefiles" -DMINGW64=Yes -DCMAKE_RC_COMPILER:FILEPATH=%CMD_WINDRES%  -DCMAKE_C_COMPILER:FILEPATH=%CMD_GCC% -DCMAKE_CXX_COMPILER:FILEPATH=%CMD_GPP% -DCMAKE_SHARED_LINKER_FLAGS:STRING="-specs %MY_DIR%\gcc64.specs" -DRUNTIME_LIB=%RUNTIME_LIB%
