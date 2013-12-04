rem SET HTTP_PROXY=proxy.guso.de:1180
rmdir /S /Q SevenZipJBinding.Release
mkdir SevenZipJBinding.Release
cd SevenZipJBinding.Release
cmake ../SevenZipJBinding -G"MinGW Makefiles" -DUSE_MINGW_W64=Yes -DCMAKE_C_COMPILER=C:/MinGW-w64/bin/x86_64-w64-mingw32-gcc.exe -DCMAKE_CXX_COMPILER=C:/MinGW-w64/bin/x86_64-w64-mingw32-g++.exe "-DJAVA_JDK=C:/Program Files/Java/jdk1.5.0_20" -DBUILD_MYSPLIT=1  -DCMAKE_SHARED_LINKER_FLAGS="-specs C:\SevenZipJBinding\gcc64.specs"
C:\MinGW\bin\mingw32-make.exe 
rem ctest -D Experimental
rem C:\MinGW\bin\mingw32-make.exe package
rem cmake -P ../SevenZipJBinding/scripts/upload-release.cmake
cd ..