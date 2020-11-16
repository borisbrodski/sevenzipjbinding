@echo off
call env-win64.bat

SET BUILD_DIR=SevenZipJBinding.Eclipse
SET BUILD_ENV_BAT=env-win64.bat

call %MY_DIR%prepare-build.bat
if errorlevel 1 (exit /b 1)

cmake ../SevenZipJBinding %CMAKE_TOOLCHAIN% -DCMAKE_BUILD_TYPE=Debug "-DJAVA_JDK=%JAVA_JDK%" -G"Eclipse CDT4 - MinGW Makefiles"
