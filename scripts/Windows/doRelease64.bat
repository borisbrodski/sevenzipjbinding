@echo off
SET MY_DIR=%~dp0

SET BUILD_DIR=SevenZipJBinding.Release64
SET BUILD_ENV_BAT=env-win64.bat

call %MY_DIR%prepare-build.bat
if errorlevel 1 (exit /b 1)

cmake ../SevenZipJBinding %CMAKE_TOOLCHAIN% -DCMAKE_BUILD_TYPE=Release "-DJAVA_JDK=%JAVA_JDK%" 
%CMD_MAKE%
ctest -D Experimental -I
%CMD_MAKE% package

cd ..
