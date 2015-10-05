#!/bin/bash

if [[ "$*" == "" ]] ; then
  echo "$0 <files to upload>"
  exit 1
fi

for file in "$@" ; do
  #scp "$file" "boris_brodski,sevenzipjbind@frs.sourceforge.net:/home/frs/project/s/se/sevenzipjbind/OldFiles/"
  scp "$file" "boris_brodski,sevenzipjbind@frs.sourceforge.net:/home/frs/project/s/se/sevenzipjbind/7-Zip-JBinding/9.20-2.00beta/"
done
