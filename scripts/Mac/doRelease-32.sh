#!/bin/sh
rm -rf SevenZipJBinding.Release-32
mkdir SevenZipJBinding.Release-32
cd SevenZipJBinding.Release-32
cmake ../SevenZipJBinding -DJAVA_PARAMS=-d32 -DJAVA_JDK=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home

make
ctest -D Experimental
make package
cmake -P ../SevenZipJBinding/scripts/upload-release.cmake
cd ..

