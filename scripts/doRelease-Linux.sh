#!/bin/sh
rm -rf SevenZipJBinding.Release/
mkdir SevenZipJBinding.Release/
cd SevenZipJBinding.Release/
cmake ../SevenZipJBinding -DCMAKE_BUILD_TYPE=Release -DJAVA_JDK=/usr/lib/jvm/java-5-sun
make
ctest -D Experimental
cmake -P ../SevenZipJBinding/scripts/upload-release.cmake
cd ..