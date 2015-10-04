#!/bin/sh

FILES_TO_UPDATE="
  AUTHORS
  COPYING
  ChangeLog
  LGPL
  NEWS
  README
  ReleaseNotes.txt
  THANKS
"

FULLPATH=$(readlink -f $0)
DIR=`dirname "$FULLPATH"`
UPDATE_FILE_IN_PACKAGE="$DIR/update-file-in-packages.sh"
SOURCE_DIR="$DIR/../.."

ARCHIVES="$@"

if [ ! -f "$UPDATE_FILE_IN_PACKAGE" ] ;  then
  echo "Script not found: '$UPDATE_FILE_IN_PACKAGE'"
  exit 1
fi

for file in $FILES_TO_UPDATE ; do
  "$UPDATE_FILE_IN_PACKAGE" "$SOURCE_DIR/$file" "$@"
done

