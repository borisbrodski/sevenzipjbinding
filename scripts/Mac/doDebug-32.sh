#!/bin/sh
rm -rf SevenZipJBinding.Debug-32
mkdir SevenZipJBinding.Debug-32
cd SevenZipJBinding.Debug-32
cmake ../SevenZipJBinding -DCMAKE_BUILD_TYPE=Debug -DJAVA_PARAMS=-d32 -DJAVA_JDK=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home
make
ctest -D Experimental
