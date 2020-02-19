#!/bin/bash

if [[ "$*" == "" ]] ; then
  echo "$0 <files to upload>"
  exit 1
fi

echo -n "Enter version (e.g. 9.20-2.00beta) "
read VERSION

TARGET="boris_brodski,sevenzipjbind@frs.sourceforge.net:/home/frs/project/s/se/sevenzipjbind/7-Zip-JBinding/$VERSION/"

echo -n "Uploading to $TARGET. Proceed?"
read

for file in "$@" ; do
  #scp "$file" "boris_brodski,sevenzipjbind@frs.sourceforge.net:/home/frs/project/s/se/sevenzipjbind/OldFiles/"
  scp "$file" "$TARGET"
done
