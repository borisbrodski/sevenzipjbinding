#!/bin/bash

FILES=$(curl -s http://sevenzipjbind.sourceforge.net/ls-files.php)
IFS='
'
for file in $FILES; do
	echo "Downloading file: $file"
	curl http://sevenzipjbind.sourceforge.net/download.php?filename=$file > $file
done