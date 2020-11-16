@echo off

REM * Set proxy, if needed
REM SET HTTP_PROXY=proxy.server:8080

REM * Path to the Git-bin diretory with scp.exe and sh.exe
SET GIT_MSYS_BIN=C:\Program Files\Git\usr\bin

REM * Command to call Git-MinGW sh passing CMD as a parameter to execute
REM * Used to upload release using scp
REM SET MSYS_CMD=C:\WINDOWS\SysWOW64\cmd.exe /c "C:\Program Files (x86)\Git\bin\sh.exe" --login -c
SET MSYS_CMD="%GIT_MSYS_BIN%\sh.exe" --login -c

REM * URL to upload build artifacts
REM SET SCP_UPLOAD_PATH=sevenzipjbinding@xtextcasts.org:/home/sevenzipjbinding/release/
SET SCP_UPLOAD_PATH=boris_brodski,sevenzipjbind@frs.sourceforge.net:/home/frs/project/s/se/sevenzipjbind/OldFiles/



