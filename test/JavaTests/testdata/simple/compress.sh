#!/bin/bash

TMP=/tmp
IMAGEX_SUBST_DRIVE=f

CREATE_ALL=n
CREATE_SIMPLE_ARJ=n
CREATE_SIMPLE_CPIO=n
CREATE_SIMPLE_LZMA=n
CREATE_SIMPLE_LZH=n
CREATE_SIMPLE_ZIP=n
CREATE_SIMPLE_7Z=n
CREATE_SIMPLE_RAR=n
CREATE_SIMPLE_TAR=n
CREATE_SIMPLE_GZIP=n
CREATE_SIMPLE_BZIP2=n
CREATE_SIMPLE_DEB=n
CREATE_SIMPLE_XAR=n
CREATE_SIMPLE_UDF=n
CREATE_SIMPLE_WIM=n
CREATE_SIMPLE_FAT=n
CREATE_SIMPLE_NTFS=n

if test $CREATE_SIMPLE_ARJ = y -o $CREATE_ALL = y ;then
    rm arj/*.arj
    for i in 0 2 4
    do
	for j in *.dat
	do
	    arj a -m$i arj/$j.$i.arj $j
	    arj a -m$i -gTestPass arj/pass-$j.$i.arj $j
	done
    done
fi


if test $CREATE_SIMPLE_CPIO = y -o $CREATE_ALL = y ; then
    rm cpio/*.cpio
    for i in 0
    do
	for j in *.dat
	do
	    echo $j | cpio -o > cpio/$j.$i.cpio
	    # No encryption supported
	done
    done
fi

if test $CREATE_SIMPLE_LZMA = y -o $CREATE_ALL = y ; then
    rm lzma/*.lzma
    for i in 1 5 9
    do
	for j in *.dat
        do
	    lzma -z -$i -c $j > lzma/$j.$i.lzma
	    # No encryption supported
        done
    done
fi

if test $CREATE_SIMPLE_LZH = y -o $CREATE_ALL = y ; then
    rm lzh/*.lzh
    for i in 5 6 7
    do
	for j in *.dat
        do
	    lha ao$i lzh/$j.$i.lzh $j
        done
    done
fi

if test $CREATE_SIMPLE_ZIP = y -o $CREATE_ALL = y ; then
    rm zip/*.zip
    for i in 0 5 9
    do
	for j in *.dat
	do
	    zip -u -$i zip/$j.$i.zip $j
	    zip -u -$i -P TestPass zip/pass-$j.$i.zip $j
        done
    done
fi

if test $CREATE_SIMPLE_7Z = y -o $CREATE_ALL = y ; then
    rm 7z/*.7z
    for i in 0 5 9
    do
	for j in *.dat
        do
	    7z a -t7z -m0=lzma -mx=$i 7z/$j.$i.7z $j
	    7z a -t7z -m0=lzma -mx=$i -pTestPass 7z/pass-$j.$i.7z $j
	    7z a -t7z -m0=lzma -mx=$i -pTestPass -mhe=on 7z/passh-$j.$i.7z $j

	    7z a -v10000b -t7z -m0=lzma -mx=$i 7z/vol-$j.$i.7z $j
	    7z a -v10000b -t7z -m0=lzma -mx=$i -pTestPass 7z/vol-pass-$j.$i.7z $j
	    7z a -v10000b -t7z -m0=lzma -mx=$i -pTestPass -mhe=on 7z/vol-passh-$j.$i.7z $j
        done
    done
fi

if test $CREATE_SIMPLE_RAR = y -o $CREATE_ALL = y ; then
    rm rar/*.rar
    for i in 0 2 5
    do
        for j in *.dat
        do
	    rar a -m$i rar/$j.$i.rar $j
	    rar a -m$i -pTestPass rar/pass-$j.$i.rar $j
	    rar a -m$i -hpTestPass rar/passh-$j.$i.rar $j

	    rar a -v10000b -m$i rar/vol-$j.$i.rar $j
	    rar a -v10000b -m$i -pTestPass rar/vol-pass-$j.$i.rar $j
	    rar a -v10000b -m$i -hpTestPass rar/vol-passh-$j.$i.rar $j
        done
    done
fi

if test $CREATE_SIMPLE_TAR = y -o $CREATE_ALL = y ; then
    rm tar/*.tar
    for i in 0
    do
        for j in *.dat
        do
	    tar cf tar/$j.$i.tar $j
        done
    done
fi

if test $CREATE_SIMPLE_GZIP = y -o $CREATE_ALL = y ; then
    rm gzip/*.gz
    for i in 1 5 9
    do
        for j in *.dat
	do
	    cat $j | gzip -$i > gzip/$j.$i.gz
        done
    done
fi

if test $CREATE_SIMPLE_BZIP2 = y -o $CREATE_ALL = y ; then
    rm bzip2/*.bz2
    for i in 1 5 9
    do
        for j in *.dat
        do
	        cat $j | bzip2 -$i > bzip2/$j.$i.bz2
        done
    done
fi

if test $CREATE_SIMPLE_DEB = y -o $CREATE_ALL = y ; then
    rm deb/*.deb
    for j in *.dat
    do
        ar rc deb/$j.1.deb $j
        ar rcs deb/$j.2.deb $j
        ar rcS deb/$j.3.deb $j
    done
fi

if test $CREATE_SIMPLE_XAR = y -o $CREATE_ALL = y ; then
    rm xar/*.xar
    for j in *.dat
    do
        xar -c --compression none -f xar/$j.1.xar $j
        xar -c --compression gzip  -f xar/$j.2.xar $j
        xar -c --compression bzip2 -f xar/$j.3.xar $j
    done
fi

if test $CREATE_SIMPLE_UDF = y -o $CREATE_ALL = y ; then
    rm udf/*.zip
    MNT=$TMP/udf-mnt
    mkdir $MNT
    for i in 102 150
    do
        for j in *.dat
        do
            IMAGE=udf/$j.$i.udf
            dd if=/dev/zero of=$IMAGE bs=1024 count=1000
            mkudffs -r 0x0$i $IMAGE
            sudo mount -o loop -t udf $IMAGE $MNT
            sudo rm -rf $MNT/lost+found
            sudo cp $j $MNT/
            sudo umount $MNT
            7z a -tzip -mx=9 $IMAGE.zip $IMAGE
            rm $IMAGE
        done
    done
fi

if test $CREATE_SIMPLE_WIM = y -o $CREATE_ALL = y ;then
    rm wim/*.wim
    for i in 0:maximum 1:fast 2:none
    do
        for j in *.dat
        do
            rm /$IMAGEX_SUBST_DRIVE/*.dat
            cp $j /$IMAGEX_SUBST_DRIVE/
            cmd /c "imagex /capture $IMAGEX_SUBST_DRIVE: wim/$j.${i:0:1}.wim Simple_test__$j /compress ${i:2}"
        done
    done
fi

if test $CREATE_SIMPLE_FAT = y -o $CREATE_ALL = y ;then
    mkdir -p fat
    rm -rf __tmp_volume
    mkdir -p __tmp_volume
    rm -f fat/*.fat
    rm -f fat/*.zip
    #for i in 0:maximum 1:fast 2:none

    for sectorsPerCluster in 1 2 4
    do
      for i in 1:80 2:80 3:40 4:40 5:40
      do
        FILE="fat/simple${i:0:1}.dat.$sectorsPerCluster.fat"
        echo ""
        echo "----------------> Creating $FILE"
        echo ""
        dd if=/dev/zero "of=$FILE" count=${i:2} bs=1024 \
          && mkfs.vfat -s $sectorsPerCluster "$FILE" \
          && sudo mount "$FILE" __tmp_volume \
          && sudo cp simple${i:0:1}.dat __tmp_volume/ \
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

    for i in 1:1025 2:1025 3:1025 4:1025
    do
      FILE="ntfs/simple${i:0:1}.dat.1.ntfs"
      echo ""
      echo "----------------> Creating $FILE"
      echo ""
      dd if=/dev/zero "of=$FILE" count=${i:2} bs=1024 \
        && mkfs.ntfs -F "$FILE" \
        && sudo mount "$FILE" __tmp_volume \
        && sudo cp simple${i:0:1}.dat __tmp_volume/ \
        && ls -l __tmp_volume \
        && echo "COPIED SUCCESSFULLY"
      sync
      sudo umount "$PWD/__tmp_volume"
      zip -9 --junk-paths $FILE.zip $FILE
      rm $FILE
    done
    rm -rf __tmp_volume
fi

