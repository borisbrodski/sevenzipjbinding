#!/bin/bash

CREATE_ALL=n
CREATE_SIMPLE_ARJ=n
CREATE_SIMPLE_CPIO=n
CREATE_SIMPLE_LZH=n
CREATE_SIMPLE_ISO=n
CREATE_SIMPLE_7Z=n
CREATE_SIMPLE_RAR=n
CREATE_SIMPLE_TAR=n
CREATE_SIMPLE_ZIP=n
CREATE_SIMPLE_DEB=n
CREATE_SIMPLE_XAR=y


TMP=/tmp
TMP_CONTENT_DIR=$TMP/7-Zip-JBinding-Multiple-Files-Test-Content
TEST_DIR=`pwd`

rm -R $TMP_CONTENT_DIR
mkdir $TMP_CONTENT_DIR

for j in *.zip
do
    mkdir $TMP_CONTENT_DIR/$j
    unzip -q -d $TMP_CONTENT_DIR/$j $j
done

if test $CREATE_SIMPLE_7Z = y -o $CREATE_ALL = y ; then
    rm 7z/*.7z 
    rm 7z/*.7z.0*
    for j in *.zip
    do
	for i in 0 5 9
	do
	    7z a -t7z -m0=lzma -mx=$i 7z/$j.$i.7z "$TMP_CONTENT_DIR/$j/*"
	    7z a -t7z -m0=lzma -mx=$i -pTestPass 7z/pass-$j.$i.7z "$TMP_CONTENT_DIR/$j/*"
	    7z a -t7z -m0=lzma -mx=$i -pTestPass -mhe=on 7z/passh-$j.$i.7z "$TMP_CONTENT_DIR/$j/*"
	    
	    7z a -v200000b -t7z -m0=lzma -mx=$i 7z/vol-$j.$i.7z "$TMP_CONTENT_DIR/$j/*"
	    7z a -v200000b -t7z -m0=lzma -mx=$i -pTestPass 7z/vol-pass-$j.$i.7z "$TMP_CONTENT_DIR/$j/*"
	    7z a -v200000b -t7z -m0=lzma -mx=$i -pTestPass -mhe=on 7z/vol-passh-$j.$i.7z "$TMP_CONTENT_DIR/$j/*"
        done
    done
fi

if test $CREATE_SIMPLE_ARJ = y -o $CREATE_ALL = y ;then
    rm arj/*.arj
    for i in 0 2 4
    do
        for j in *.zip
        do
            pushd $TMP_CONTENT_DIR/$j && arj a -r -m$i $TEST_DIR/arj/$j.$i.arj "*" ; popd
            pushd $TMP_CONTENT_DIR/$j && arj a -r -m$i -gTestPass $TEST_DIR/arj/pass-$j.$i.arj . ; popd
        done
    done
fi


if test $CREATE_SIMPLE_CPIO = y -o $CREATE_ALL = y ; then
    rm cpio/*.cpio
    for i in 0
    do
	    for j in *.zip
	    do
	        pushd $TMP_CONTENT_DIR/$j && find . | cpio -o > $TEST_DIR/cpio/$j.$i.cpio ; popd
	    done
    done
fi


if test $CREATE_SIMPLE_LZH = y -o $CREATE_ALL = y ; then
    rm lzh/*.lzh
    for i in 5 6 7
    do
	    for j in *.zip
	    do
	        pushd $TMP_CONTENT_DIR/$j && lha ao$i $TEST_DIR/lzh/$j.$i.lzh * ; popd
	    done
    done
fi


if test $CREATE_SIMPLE_ISO = y -o $CREATE_ALL = y ; then
    rm iso/*.iso
    for j in *.zip
    do
        i=0
        pushd $TMP_CONTENT_DIR && genisoimage -o $TEST_DIR/iso/$j.$i.iso -J $j ; popd
        i=1
        pushd $TMP_CONTENT_DIR && genisoimage -o $TEST_DIR/iso/$j.$i.iso -R $j ; popd
        i=2
        pushd $TMP_CONTENT_DIR && genisoimage -o $TEST_DIR/iso/$j.$i.iso -R -J -hfs $j ; popd
    done
fi

if test $CREATE_SIMPLE_RAR = y -o $CREATE_ALL = y ; then
    rm rar/*.rar
    for i in 0 2 5
    do
        for j in *.zip
        do
            pushd $TMP_CONTENT_DIR/$j && rar a -r -m$i $TEST_DIR/rar/$j.$i.rar . ; popd
            pushd $TMP_CONTENT_DIR/$j && rar a -r -m$i -pTestPass $TEST_DIR/rar/pass-$j.$i.rar . ; popd
            pushd $TMP_CONTENT_DIR/$j && rar a -r -m$i -hpTestPass $TEST_DIR/rar/passh-$j.$i.rar . ; popd

            pushd $TMP_CONTENT_DIR/$j && rar a -r -v200000b -m$i $TEST_DIR/rar/vol-$j.$i.rar . ; popd
            pushd $TMP_CONTENT_DIR/$j && rar a -r -v200000b -m$i -pTestPass $TEST_DIR/rar/vol-pass-$j.$i.rar . ; popd
            pushd $TMP_CONTENT_DIR/$j && rar a -r -v200000b -m$i -hpTestPass $TEST_DIR/rar/vol-passh-$j.$i.rar . ; popd
        done
    done
fi

if test $CREATE_SIMPLE_TAR = y -o $CREATE_ALL = y ; then
    rm tar/*.tar
    for i in 0
    do
        for j in *.zip
        do
            pushd $TMP_CONTENT_DIR/$j && tar cf $TEST_DIR/tar/$j.$i.tar . ; popd
        done
    done
fi

if test $CREATE_SIMPLE_ZIP = y -o $CREATE_ALL = y ; then
    rm zip/*.zip
    for i in 0 5 9
    do
        for j in *.zip
        do
            pushd $TMP_CONTENT_DIR/$j && zip -u -r -$i $TEST_DIR/zip/$j.$i.zip * ; popd
            pushd $TMP_CONTENT_DIR/$j && zip -u -r -$i -P TestPass $TEST_DIR/zip/pass-$j.$i.zip * ; popd
        done
    done
fi

if test $CREATE_SIMPLE_DEB = y -o $CREATE_ALL = y ; then
    rm deb/*.deb
    for j in *.zip
    do
        pushd $TMP_CONTENT_DIR/$j && find . -maxdepth 1 -type f -print0 | xargs -0 ar cr $TEST_DIR/deb/$j.1.deb; popd
        pushd $TMP_CONTENT_DIR/$j && find . -maxdepth 1 -type f -print0 | xargs -0 ar crs $TEST_DIR/deb/$j.2.deb; popd
        pushd $TMP_CONTENT_DIR/$j && find . -maxdepth 1 -type f -print0 | xargs -0 ar crS $TEST_DIR/deb/$j.3.deb; popd
    done
fi

if test $CREATE_SIMPLE_XAR = y -o $CREATE_ALL = y ; then
    rm xar/*.xar
    for j in *.zip
    do
        pushd $TMP_CONTENT_DIR/$j && xar -c --compression none -f $TEST_DIR/xar/$j.1.xar *; popd
        pushd $TMP_CONTENT_DIR/$j && xar -c --compression gzip -f $TEST_DIR/xar/$j.2.xar *; popd
        pushd $TMP_CONTENT_DIR/$j && xar -c --compression bzip2 -f $TEST_DIR/xar/$j.3.xar *; popd
    done
fi

