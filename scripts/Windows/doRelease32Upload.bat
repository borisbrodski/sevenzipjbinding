@echo off
SET MY_DIR=%~dp0

SET BUILD_DIR=SevenZipJBinding.Release32

call %MY_DIR%env-win.bat

cd %BUILD_DIR%
@echo on
%MSYS_CMD% 'scp *.zip %SCP_UPLOAD_PATH%'

cd ..