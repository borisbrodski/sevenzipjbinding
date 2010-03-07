#!/bin/sh
scp -r "boris_brodski,sevenzipjbind@frs.sourceforge.net:/home/frs/project/s/se/sevenzipjbind/OldFiles/" .
mv OldFiles/* .
rm -rf OldFiles