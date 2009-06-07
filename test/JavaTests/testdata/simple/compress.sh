#!/bin/bash

rm arj/*.arj
for i in 0 2 4
do
    for j in *.dat
    do
	arj a -m$i arj/$j.$i.arj $j
    done
done

rm cpio/*.cpio
for i in 0
do
    for j in *.dat
    do
	echo $j | cpio -o > cpio/$j.$i.cpio
    done
done

rm lzma/*.lzma
for i in 1 5 9
do
    for j in *.dat
    do
	lzma -z -$i -c $j > lzma/$j.$i.lzma
    done
done

rm zip/*.zip
for i in 0 5 9
do
    for j in *.dat
    do
	zip -u -$i zip/$j.$i.zip $j
    done
done

rm 7z/*.7z
for i in 0 5 9
do
    for j in *.dat
    do
	7z a -t7z -m0=lzma -mx=$i 7z/$j.$i.7z $j
    done
done

rm rar/*.rar
for i in 0 2 5
do
    for j in *.dat
    do
	rar a -m$i rar/$j.$i.rar $j
    done
done

rm tar/*.tar
for i in 0
do
    for j in *.dat
    do
	tar cf tar/$j.$i.tar $j
    done
done

rm gzip/*.gz
for i in 1 5 9
do
    for j in *.dat
    do
	cat $j | gzip -$i > gzip/$j.$i.gz
    done
done

rm bzip2/*.bz2
for i in 1 5 9
do
    for j in *.dat
    do
	cat $j | bzip2 -$i > bzip2/$j.$i.bz2
    done
done
