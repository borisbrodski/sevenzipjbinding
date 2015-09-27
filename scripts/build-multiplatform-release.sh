#!/bin/bash

ROOTDIR=$(pwd)

RELEASE_NAME_PATTERN='\([a-z\-]\+binding-[0-9.-]\+\(\(alpha\|rc\|beta\)[0-9]*-\)\?\(extr-only-\)\?\)\([^.]\+\)\.zip'

GetTargetName() {
    eval "$1=$(echo $2 | sed -e "s/$RELEASE_NAME_PATTERN/\\5/")"
}

GetReleaseName() {
    eval "$1=$(echo $2 | sed -e "s/$RELEASE_NAME_PATTERN/\\1/")"
}

copyzipcontent=1

ProcessFile() {
    echo "=> Platform: $2 - $1"
    mkdir "$JARASSEMBLEDIR/$2"
    mkdir $1.dir
    cd $1.dir
    unzip ../$1 > /dev/null
    if [ $copyzipcontent == 1 ] ; then
        cp -r $ROOTDIR/$1.dir/*/* $ASSEMBLEDIR/
        rm $ASSEMBLEDIR/lib/*$2*.jar
        copyzipcontent=0
    fi
    cd */lib
    nativejar="$(pwd)/$(echo *$2.jar)"
    cd "$JARASSEMBLEDIR/"
    jar -xf "$nativejar"
    cd "$ROOTDIR"
    rm -r $1.dir
}

if [ $# -eq 0 ]; then
    echo Build a multi-platform release out of single-platform releases
    echo "./build-multiplatform.release.sh [ --name RelaseName ] release1.zip release2.zip ..."
    exit 0;
fi

releasefiles=""
multiplatformname="AllPlatforms"
for i in $*; do
    if [ "$i" == "--name" ] ; then
        multiplatformname="--name"
        continue
    fi
    if [ "$multiplatformname" == "--name" ] ; then
        multiplatformname="$i"
        continue
    fi
    if [ "$i" == "sevenzipjbinding-it-test-pack.zip" ] ; then
        continue
    fi
    GetTargetName target $i
    GetReleaseName release $i
    if [[ $target =~ .*binding.*  ]] ; then
        continue
    fi
    if [ "$target" == "src" ] ; then
        continue;
    fi
    if [[ $target =~ All.* ]] ; then
        continue;
    fi
    if [ $releasename ] ; then
        if [ "$releasename" != "$release" ] ; then
            echo "ERROR: not consistent release name. Prior: $releasename, current: $release"
            exit 1
        fi
    else
        releasename=$release    
    fi
    releasefiles="$releasefiles$IFS$i"
done

if [ "$multiplatformname" == "--name" ] ; then
    echo "Name for the multiplatform release not specified"
    exit 1
fi

ASSEMBLEDIR=$ROOTDIR/$release$multiplatformname
JARASSEMBLEDIR=$ROOTDIR/JAR-$multiplatformname
PLATFORMSFILE="$JARASSEMBLEDIR/sevenzipjbinding-platforms.properties"
TMPPLATFORMSFILE="$JARASSEMBLEDIR/sevenzipjbinding-platforms.properties.tmp"
MULTIPLATFORMJAR=sevenzipjbinding-$multiplatformname.jar

if [ -d "$ASSEMBLEDIR" ]; then
    rm -r "$ASSEMBLEDIR"
fi
mkdir "$ASSEMBLEDIR"
if [ -d "$JARASSEMBLEDIR" ]; then
    rm -r "$JARASSEMBLEDIR"
fi
mkdir "$JARASSEMBLEDIR"
echo "# Information about available platforms" > $TMPPLATFORMSFILE
count=1
echo Building multiplatform release \"$multiplatformname\" out of
for i in $releasefiles; do
    GetTargetName target $i
    GetReleaseName release $i
    ProcessFile "$i" "$target" "$release"
    echo "platform.$count=$target" >> $TMPPLATFORMSFILE
    count=$(($count+1))
done

mv $TMPPLATFORMSFILE $PLATFORMSFILE

cd "$JARASSEMBLEDIR"
find . \( -name "META-INF" -or -name "MANIFEST.MF" \) -delete
mkdir META-INF
echo "Manifest-Version: 1.0" > META-INF/MANIFEST.MF 
echo "Created-By: 1.5.0" >> META-INF/MANIFEST.MF 

jar cf $ASSEMBLEDIR/lib/$MULTIPLATFORMJAR *
cd ..
rm -r "$JARASSEMBLEDIR"
if [ -f $release$multiplatformname.zip ] ; then
    rm "$release$multiplatformname.zip"
fi
zip -r -9 -q $release$multiplatformname.zip $release$multiplatformname
rm -r $release$multiplatformname
