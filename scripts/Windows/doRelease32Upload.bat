@echo off

call env-win32.bat

SET BUILD_DIR=SevenZipJBinding.Release32

cd %BUILD_DIR%

%MSYS_CMD% 'scp *.zip %SCP_UPLOAD_PATH%'

cd ..