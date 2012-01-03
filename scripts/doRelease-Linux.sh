#!/bin/bash
rm -rf SevenZipJBinding.Release/
mkdir SevenZipJBinding.Release/
cd SevenZipJBinding.Release/
cmake ../SevenZipJBinding -DCMAKE_BUILD_TYPE=Release -DJAVA_JDK=/usr/lib/jvm/java-5-sun
make
ctest -D Experimental
make package
read -p "Upload to the sourceforge web space (y/n)? "
if [ "$REPLY" == "y" ] ; then
    cmake -P ../SevenZipJBinding/scripts/upload-release.cmake
fi
cd ..
