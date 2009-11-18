#!/bin/bash

if [ "$*" == "" ] ; then
    echo "Usage ./upload-release.sh <release-file-name> [<description>]"
    exit 1
fi

curl -T $1 -H "Filename:$(basename $1)" -H "Descr:$2" http://sevenzipjbind.sourceforge.net/upload.php