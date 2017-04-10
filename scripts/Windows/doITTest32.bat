@echo off
SET MY_DIR=%~dp0

SET BUILD_DIR=SevenZipJBinding.IT-Tests


call env-win.bat
call env-win32.bat

if exist %BUILD_DIR% (
  rmdir /S /Q %BUILD_DIR%
)
mkdir %BUILD_DIR%

%GIT_MSYS_BIN%\scp.exe

cmake "-DPACKAGES=Windows-x86;AllWindows;AllPlatforms" -DJAVA=%JAVA_JDK%bin\java.exe "-DSCP=%GIT_MSYS_BIN%\scp.exe" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake


