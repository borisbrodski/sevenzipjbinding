#!/bin/bash
if [ "$1" == "" ] ; then
    echo "Usage: fetch <build-directory>"
    exit 1
fi
BINARY_DIR=$(readlink -f $1)
SOURCE_DIR=`cat $BINARY_DIR/DartConfiguration.tcl | grep SourceDirectory | sed 's$SourceDirectory: \(.*\)$\1$g'`
SCRIPT_HOME=`readlink -f $0 | sed 's|\(.*/\)\?[^/]*|\1|g'`

SEVENZIP_VERSION_PATTERN='^SET(SEVENZIPJBINDING_VERSON \([^)]\+\)) *'
VERSION=`cat $SOURCE_DIR/CMakeLists.txt | grep "$SEVENZIP_VERSION_PATTERN" |  sed "s/$SEVENZIP_VERSION_PATTERN/\1/"`

ITTEST_PACKAGE=sevenzipjbinding-it-test-$VERSION
ITTEST_PACKAGE_ZIP=sevenzipjbinding-it-test-pack.zip
WORKING_DIR=/tmp/$ITTEST_PACKAGE

if [ -d $WORKING_DIR ] ; then
     echo "Cleaning working directory: $WORKING_DIR"
     rm -rf $WORKING_DIR
fi
mkdir $WORKING_DIR
echo "Using working directory: $WORKING_DIR"
cd $WORKING_DIR

echo "Source dir: $SOURCE_DIR"
echo "Version   : $VERSION"
echo Fetching 7-Zip-JBinding test information from $1




if [ ! -d "$SOURCE_DIR" ] ; then
    echo "Incorret source directory: $SOURCE_DIR"
    exit 1
fi

cat $BINARY_DIR/DartConfiguration.tcl | \
	sed 's$\(SourceDirectory: \).*$\1.$g' | \
	sed 's$\(BuildDirectory: \).*$\1.$g' | \
	sed 's/\(CVSCommand: \).*/\1echo "No configuration required for IT tests"/g' | \
	sed 's/\(SVNCommand: \).*/\1echo "No configuration required for IT tests"/g' | \
	sed 's/\(UpdateCommand: \).*/\1echo "No configuration required for IT tests"/g' | \
	sed 's/\(MakeCommand: \).*/\1echo "No configuration required for IT tests"/g' | \
	sed 's/\(ConfigureCommand: \).*/\1echo "No configuration required for IT tests"/g' > \
	DartConfiguration.tcl


cat $BINARY_DIR/CTestTestfile.cmake | \
	sed 's$"[^"]\+/JUnitRunner.cmake"$"ITJUnitTestRunner.cmake"$g' | \
	sed 's$"/usr/bin/cmake"$"cmake"$g' > \
	CTestTestfile.cmake

cp $BINARY_DIR/jbinding-java/sevenzipjbinding-tests.jar .
#cp $SCRIPT_HOME/run-it-test.sh .
cp $SCRIPT_HOME/ITJUnitTestRunner.cmake .
mkdir lib
cp $SOURCE_DIR/test/JavaTests/lib/* lib/

rm -rf testdata
find $SOURCE_DIR/test/JavaTests/testdata -not -path "*/.svn*" -printf "%P\0" | while read -d $'\0' file
do
  if [ -d "$SOURCE_DIR/test/JavaTests/testdata/$file" ] ; then
    mkdir "testdata/$file"
  else
    cp "$SOURCE_DIR/test/JavaTests/testdata/$file" "testdata/$file" 
  fi
done

echo Compressing it tests into $ITTEST_PACKAGE_ZIP
cd - > /dev/null
rm -f "$ITTEST_PACKAGE_ZIP" 
7z -bd -mx=9 a "$ITTEST_PACKAGE_ZIP" "$WORKING_DIR/" > /dev/null

read -p "Upload to the sourceforge web space (y/n)? "
if [ "$REPLY" == "y" ] ; then
    cmake -DFILENAME="$ITTEST_PACKAGE_ZIP" -DDESCRIPTION="Integration test pack" -P $SCRIPT_HOME/../upload-release.cmake
fi
