#!/bin/bash

if [[ "$1" == "" ]] ; then
  echo "Usage: $0 <zip-files-to-check>"
  exit 0
fi

for file in "$@" ; do
  echo "Checking $file"

  # Archive test with 7z
  7z t "$file" >log 2>&1 || (echo "7z detected broken archive" ; cat log)

done
rm log
echo "done"
