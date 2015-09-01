@echo off
echo doDebug32
cd C:\SevenZipJBinding\
call doDebug32.bat > doDebug32.log 2>&1

echo doRelease32
cd C:\SevenZipJBinding\
call doRelease32.bat > doRelease32.log 2>&1

echo doDebug64
cd C:\SevenZipJBinding\
call doDebug64.bat > doDebug64.log 2>&1

echo doRelease64
cd C:\SevenZipJBinding\
call doRelease64.bat > doRelease64.log 2>&1

echo "Done"
pause
