@echo off
SET DO_ALL_DIR=%~dp0

echo.
echo Cleaning
echo - Removing all logs
echo.
if exist doDebug32.log   (del doDebug32.log)
if exist doRelease32.log (del doRelease32.log)
if exist doDebug64.log   (del doDebug64.log)
if exist doRelease64.log (del doRelease64.log)


echo.
echo Calling doDebug32.bat
echo.
echo.
cd %DO_ALL_DIR%
call doDebug32.bat > doDebug32.log 2>&1
if errorlevel 1 (exit /b 1)


echo.
echo Calling doRelease32.bat
echo.
echo.
cd %DO_ALL_DIR%
call doRelease32.bat > doRelease32.log 2>&1

echo.
echo Calling doDebug64.bat
echo.
echo.
cd %DO_ALL_DIR%
call doDebug64.bat > doDebug64.log 2>&1

echo.
echo Calling doRelease64.bat
echo.
echo.
cd %DO_ALL_DIR%
call doRelease64.bat > doRelease64.log 2>&1

echo Done

