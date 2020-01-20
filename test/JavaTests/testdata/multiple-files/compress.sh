#!/bin/bash

IMAGEX_SUBST_DRIVE=f

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
CREATE_SIMPLE_XAR=n
CREATE_SIMPLE_UDF=n
CREATE_SIMPLE_WIM=n
CREATE_SIMPLE_FAT=n
CREATE_SIMPLE_NTFS=n
CREATE_SIMPLE_HFS=n


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

            pushd $TMP_CONTENT_DIR/$j && rar a -r -v2700b -m$i $TEST_DIR/rar/vol-$j.$i.rar . ; popd
            pushd $TMP_CONTENT_DIR/$j && rar a -r -v2700b -m$i -pTestPass $TEST_DIR/rar/vol-pass-$j.$i.rar . ; popd
            pushd $TMP_CONTENT_DIR/$j && rar a -r -v2700b -m$i -hpTestPass $TEST_DIR/rar/vol-passh-$j.$i.rar . ; popd
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

if test $CREATE_SIMPLE_UDF = y -o $CREATE_ALL = y ; then
    rm udf/*.zip
    MNT=$TMP/udf-mnt
    mkdir $MNT
    for i in 102 150
    do
        for j in *.zip
        do
            IMAGE=udf/$j.$i.udf
            dd if=/dev/zero of=$IMAGE bs=1024 count=2000
            mkudffs -r 0x0$i $IMAGE
            sudo mount -o loop -t udf $IMAGE $MNT
            sudo rm -rf $MNT/lost+found
            sudo cp -r $TMP_CONTENT_DIR/$j/* $MNT/
            sudo sh -c "dd if=/dev/zero of=$MNT/ignoreme.txt count=0 bs=1"
            sudo umount $MNT
            7z a -tzip -mx=9 $IMAGE.zip $IMAGE
            rm $IMAGE
        done
    done
fi

if test $CREATE_SIMPLE_WIM = y -o $CREATE_ALL = y ;then
    if [ ! -d "/$IMAGEX_SUBST_DRIVE/" ]; then
        echo "'/$IMAGEX_SUBST_DRIVE' not a directory"
        exit 1
    fi
    if [ "$(ls -A "/$IMAGEX_SUBST_DRIVE/")" ]; then
        echo "Directory '/$IMAGEX_SUBST_DRIVE' isn't empty! Aborting"
        exit 1
    fi
    rm wim/*.wim
    for i in 0:maximum 1:fast 2:none
    do
        for j in *.zip
        do
            rm -rf /$IMAGEX_SUBST_DRIVE/*
            cp -r $TMP_CONTENT_DIR/$j/* /$IMAGEX_SUBST_DRIVE/
            cmd "/c imagex /capture f: wim/$j.${i:0:1}.wim Multiple_files_test__$j /compress ${i:2}"
        done
    done
fi

if test $CREATE_SIMPLE_FAT = y -o $CREATE_ALL = y ;then
    mkdir -p fat
    rm -rf __tmp_volume
    mkdir -p __tmp_volume
    rm -f fat/*.fat
    rm -f fat/*.zip

    for sectorsPerCluster in 1 2 4
    do
      for i in 1:1300 2:70 3:60
      do
        FILE="fat/archive${i:0:1}.zip.$sectorsPerCluster.fat"
        echo ""
        echo "----------------> Creating $FILE"
        echo ""
        dd if=/dev/zero "of=$FILE" count=${i:2} bs=1024 \
          && mkfs.vfat -s $sectorsPerCluster "$FILE" \
          && sudo mount "$FILE" __tmp_volume \
          && sudo unzip -q archive${i:0:1}.zip -d __tmp_volume/ \
          && ls -l __tmp_volume \
          && echo "COPIED SUCCESSFULLY"
        sync
        sudo umount "$PWD/__tmp_volume"
        zip -9 --junk-paths $FILE.zip $FILE
        rm $FILE
      done
    done
    rm -rf __tmp_volume
fi


if test $CREATE_SIMPLE_NTFS = y -o $CREATE_ALL = y ;then
    mkdir -p ntfs
    rm -rf __tmp_volume
    mkdir -p __tmp_volume
    rm -f ntfs/*.ntfs
    rm -f ntfs/*.zip

    for i in 1:1190 2:1025 3:1025 4:1025
    do
      FILE="ntfs/archive${i:0:1}.zip.1.ntfs"
      echo ""
      echo "----------------> Creating $FILE"
      echo ""
      dd if=/dev/zero "of=$FILE" count=${i:2} bs=1024 \
        && mkfs.ntfs -F "$FILE" \
        && sudo mount "$FILE" __tmp_volume \
        && sudo unzip -q archive${i:0:1}.zip -d __tmp_volume/ \
        && ls -l __tmp_volume \
        && echo "COPIED SUCCESSFULLY"
      sync
      sudo umount "$PWD/__tmp_volume"
      zip -9 --junk-paths $FILE.zip $FILE
      rm $FILE
    done
    rm -rf __tmp_volume
fi

if test $CREATE_SIMPLE_HFS = y -o $CREATE_ALL = y ;then
    mkdir -p hfs
    rm -rf __tmp_volume
    mkdir -p __tmp_volume
    rm -f hfs/*.hfs
    rm -f hfs/*.zip

    # -h Creates a legacy HFS format filesystem. This option is not recommended for file
    #    systems that will be primarily used with Mac OS X or Darwin.
    # -w Adds an HFS wrapper around the HFS Plus file system.  This wrapper is required
    #    if the file system will be used to boot natively into Mac OS 9.
    for params in 1: # 2:-h 3:-w TODO not supported in 7-zip 16.02.  Test with later 7-zip
    do
      for i in 1:5000 2:5000 3:5000
      do
        FILE="hfs/archive${i:0:1}.zip.${params:0:1}.hfs"
        echo ""
        echo "----------------> Creating $FILE"
        echo ""
        echo "CMD: mkfs.hfsplus -v 7-zip-test ${params:2} '$FILE'"
        dd if=/dev/zero "of=$FILE" count=${i:2} bs=1024 \
          && mkfs.hfsplus -v 7-zip-test ${params:2} "$FILE" \
          && sudo mount "$FILE" __tmp_volume \
          && sudo unzip -q archive${i:0:1}.zip -d __tmp_volume/ \
          && ls -l __tmp_volume \
          && echo "COPIED SUCCESSFULLY"
        sync
        sudo umount "$PWD/__tmp_volume"
        zip -9 --junk-paths $FILE.zip $FILE
        rm $FILE
      done
    done
    rm -rf __tmp_volume
fi

# vim: set ts=2 sts=2 sw=2 expandtab:
