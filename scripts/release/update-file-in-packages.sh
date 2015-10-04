#!/bin/bash

FILE_TO_UPDATE="$1"

if [[ "$FILE_TO_UPDATE" == "" ]] ; then
  echo "Usage: $0 <changed file> <archive files to update>"
  exit 1
fi

if [ ! -f "$FILE_TO_UPDATE" ] ; then
  echo "File '$FILE_TO_UPDATE' doesn't exists"
  exit 1
fi

echo "Updating $FILE_TO_UPDATE ..."

shift 1
for ARCHIVE in "$@" ; do
  NAME="${ARCHIVE%.zip}"
  echo "Updating archive '$NAME'"
  if [[ -e "$NAME" ]] ; then
    echo "ERROR: '$NAME' already exists. Remove it firts, then try again."
    exit 1
  fi
  mkdir "$NAME"
  cp "$FILE_TO_UPDATE" "$NAME/"
  7z a "$ARCHIVE" $NAME/* > update.log || (cat update.log ; echo "ERROR updating archive"; exit 1)
  rm -rf "$NAME"
done

