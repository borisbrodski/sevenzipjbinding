SET PATH=%PATH%;C:\MinGW\bin
rmdir /S /Q Testing.OLD
ren Testing Testing.OLD
FOR /D %%p IN ("C:\Documents and Settings\Administrator\Local Settings\Temp\Seven*") DO rmdir "%%p" /s /q 
c:\MinGW\bin\mingw32-make.exe 
ctest -I 16,16