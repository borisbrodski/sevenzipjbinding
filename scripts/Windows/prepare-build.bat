@echo off
SET MY_DIR=%~dp0

call %MY_DIR%env-win.bat
call %MY_DIR%%BUILD_ENV_BAT%

echo 7-Zip-JBinding build configuration on Windows:
echo.
echo - Source dir: %MY_DIR%SevenZipJBinding
echo - Build dir:  %MY_DIR%%BUILD_DIR%
echo - Java JDK:   %JAVA_JDK%
echo - MinGW home: %MINGW_HOME%
echo.
echo.

if not exist %MY_DIR%\SevenZipJBinding (
  echo ERROR: 7-Zip-JBinding source directory not found
  echo Place sources in %MY_DIR%SevenZipJBinding
  exit /b 1
)

if exist %BUILD_DIR% (
  rmdir /S /Q %BUILD_DIR%
)
mkdir %BUILD_DIR%
cd %BUILD_DIR%
call %MY_DIR%\generate-recompile-bat.bat

echo Executing 'cmake' and then 'make'...
echo.
echo.