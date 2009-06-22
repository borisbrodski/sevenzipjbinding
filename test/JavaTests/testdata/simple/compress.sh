#!/bin/bash

CREATE_ALL=n
CREATE_SIMPLE_ARJ=n
CREATE_SIMPLE_CPIO=n
CREATE_SIMPLE_LZMA=n
CREATE_SIMPLE_ZIP=n
CREATE_SIMPLE_7Z=y
CREATE_SIMPLE_RAR=n
CREATE_SIMPLE_TAR=n
CREATE_SIMPLE_GZIP=n
CREATE_SIMPLE_BZIP2=n

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