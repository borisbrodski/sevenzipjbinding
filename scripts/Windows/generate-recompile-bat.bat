@echo off

SET OUTPUT_FILE=recompile.bat
echo @echo off > %OUTPUT_FILE%
echo. >> %OUTPUT_FILE%

echo SET PATH=%MINGW_HOME%\bin;%%PATH%% >> %OUTPUT_FILE%
echo. >> %OUTPUT_FILE%

echo if exist Testing.OLD ( >> %OUTPUT_FILE%
echo   rmdir /S /Q Testing.OLD >> %OUTPUT_FILE%
echo ) >> %OUTPUT_FILE%
echo if exist Testing ( >> %OUTPUT_FILE%
echo   ren Testing Testing.OLD >> %OUTPUT_FILE%
echo ) >> %OUTPUT_FILE%

echo FOR /D %%%%p IN ("%TEMP%\Seven*") DO rmdir "%%%%p" /s /q >> %OUTPUT_FILE%
REM FOR /D %%p IN ("C:\Documents and Settings\Administrator\Local Settings\Temp\Seven*") DO rmdir "%%p" /s /q 

echo. >> %OUTPUT_FILE%
echo %MINGW_HOME%\bin\mingw32-make.exe >> %OUTPUT_FILE%

echo. >> %OUTPUT_FILE%
echo REM ctest -I 1,1 >> %OUTPUT_FILE%

