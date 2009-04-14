#! /bin/sh

# global install
DEST_HOME=/usr/local
# for testing 
# DEST_HOME=${HOME}/INSTALL/usr/local
DEST_BIN=${DEST_HOME}/bin
DEST_SHARE=${DEST_HOME}/lib/p7zip
DEST_MAN=${DEST_HOME}/man
DEST_SHARE_DOC=${DEST_HOME}/share/doc/p7zip
DEST_DIR=
[ "$1" ] && DEST_BIN="$1"
[ "$2" ] && DEST_SHARE="$2"
[ "$3" ] && DEST_MAN="$3"
[ "$4" ] && DEST_SHARE_DOC="$4"
[ "$5" ] && DEST_DIR="$5"

use_share="n"

if [ -x bin/7zCon.sfx ]
then
  use_share="o"
fi

if [ -x bin/7z ]
then
  use_share="o"
fi

# cleaning previous install
rm -f  "${DEST_DIR}${DEST_BIN}/7z"
rm -f  "${DEST_DIR}${DEST_BIN}/7za"
rm -f  "${DEST_DIR}${DEST_BIN}/7zr"
rm -f  "${DEST_DIR}${DEST_SHARE}/7z"
rm -f  "${DEST_DIR}${DEST_SHARE}/7za"
rm -f  "${DEST_DIR}${DEST_SHARE}/7zCon.sfx"
rm -f  "${DEST_DIR}${DEST_SHARE}/7zr"
rm -f  "${DEST_DIR}${DEST_SHARE}/7z.so"
chmod -R 777 "${DEST_DIR}${DEST_SHARE}/Codecs" 2> /dev/null
chmod -R 777 "${DEST_DIR}${DEST_SHARE}/Formats" 2> /dev/null
rm -fr "${DEST_DIR}${DEST_SHARE}/Codecs"
rm -fr "${DEST_DIR}${DEST_SHARE}/Formats"
rm -f  "${DEST_DIR}${DEST_MAN}/man1/7z.1"
rm -f  "${DEST_DIR}${DEST_MAN}/man1/7za.1"
rm -f  "${DEST_DIR}${DEST_MAN}/man1/7zr.1"
chmod -R 777 "${DEST_DIR}${DEST_SHARE_DOC}" 2> /dev/null
rm -fr  "${DEST_DIR}${DEST_SHARE_DOC}"

if [ "${use_share}" = "o" ]
then
  mkdir -p "${DEST_DIR}${DEST_BIN}"
  mkdir -p "${DEST_DIR}${DEST_SHARE}"
  if [ -x bin/7za ]
  then
    echo "- installing ${DEST_DIR}${DEST_BIN}/7za"
    cp bin/7za "${DEST_DIR}${DEST_SHARE}/7za"
    chmod 777 "${DEST_DIR}${DEST_SHARE}/7za"
    strip     "${DEST_DIR}${DEST_SHARE}/7za"
    chmod 555 "${DEST_DIR}${DEST_SHARE}/7za"
    echo "#! /bin/sh" > "${DEST_DIR}${DEST_BIN}/7za"
    echo "\"${DEST_SHARE}/7za\" \"\$@\"" >> "${DEST_DIR}${DEST_BIN}/7za"
    chmod 555 "${DEST_DIR}${DEST_BIN}/7za"
  fi

  if [ -x bin/7zr ]
  then
    echo "- installing ${DEST_DIR}${DEST_BIN}/7zr"
    cp bin/7zr "${DEST_DIR}${DEST_SHARE}/7zr"
    chmod 777 "${DEST_DIR}${DEST_SHARE}/7zr"
    strip     "${DEST_DIR}${DEST_SHARE}/7zr"
    chmod 555 "${DEST_DIR}${DEST_SHARE}/7zr"
    echo "#! /bin/sh" > "${DEST_DIR}${DEST_BIN}/7zr"
    echo "\"${DEST_SHARE}/7zr\" \"\$@\"" >> "${DEST_DIR}${DEST_BIN}/7zr"
    chmod 555 "${DEST_DIR}${DEST_BIN}/7zr"
  fi

  if [ -x bin/7zCon.sfx ]
  then
    echo "- installing ${DEST_DIR}${DEST_SHARE}/7zCon.sfx"
    cp bin/7zCon.sfx "${DEST_DIR}${DEST_SHARE}/7zCon.sfx"
    chmod 777 "${DEST_DIR}${DEST_SHARE}/7zCon.sfx"
    strip     "${DEST_DIR}${DEST_SHARE}/7zCon.sfx"
    chmod 555 "${DEST_DIR}${DEST_SHARE}/7zCon.sfx"
  fi

  if [ -x bin/7z ]
  then
    echo "- installing ${DEST_DIR}${DEST_BIN}/7z"
    cp bin/7z "${DEST_DIR}${DEST_SHARE}/7z"
    chmod 777 "${DEST_DIR}${DEST_SHARE}/7z"
    strip     "${DEST_DIR}${DEST_SHARE}/7z"
    chmod 555 "${DEST_DIR}${DEST_SHARE}/7z"
    cp -r bin/Codecs "${DEST_DIR}${DEST_SHARE}/"
    chmod 555 "${DEST_DIR}${DEST_SHARE}"/*/*
    cp bin/7z.so "${DEST_DIR}${DEST_SHARE}/7z.so"
    chmod 555 "${DEST_DIR}${DEST_SHARE}/7z.so"
    echo "#! /bin/sh" > "${DEST_DIR}${DEST_BIN}/7z"
    echo "\"${DEST_SHARE}/7z\" \"\$@\"" >> "${DEST_DIR}${DEST_BIN}/7z"
    chmod 555 "${DEST_DIR}${DEST_BIN}/7z"
  fi

else
  if [ -x bin/7za ]
  then
    echo "- installing ${DEST_DIR}${DEST_BIN}/7za"
    mkdir -p "${DEST_DIR}${DEST_BIN}"
    cp bin/7za "${DEST_DIR}${DEST_BIN}/7za"
    chmod 555  "${DEST_DIR}${DEST_BIN}/7za"
  fi

  if [ -x bin/7zr ]
  then
    echo "- installing ${DEST_DIR}${DEST_BIN}/7zr"
    mkdir -p "${DEST_DIR}${DEST_BIN}"
    cp bin/7zr "${DEST_DIR}${DEST_BIN}/7zr"
    chmod 555  "${DEST_DIR}${DEST_BIN}/7zr"
  fi
fi

mkdir -p "${DEST_DIR}${DEST_MAN}/man1"
if [ -d DOCS ]
then
   echo "- installing ${DEST_DIR}${DEST_MAN}/man1/7z.1"
   sed -e s?"{DEST_SHARE_DOC}"?"${DEST_SHARE_DOC}/DOCS"?g man1/7z.1 > "${DEST_DIR}${DEST_MAN}/man1/7z.1"
   chmod 444 "${DEST_DIR}${DEST_MAN}/man1/7z.1"

   echo "- installing ${DEST_DIR}${DEST_MAN}/man1/7za.1"
   sed -e s?"{DEST_SHARE_DOC}"?"${DEST_SHARE_DOC}/DOCS"?g man1/7za.1 > "${DEST_DIR}${DEST_MAN}/man1/7za.1"
   chmod 444 "${DEST_DIR}${DEST_MAN}/man1/7za.1"

   echo "- installing ${DEST_DIR}${DEST_MAN}/man1/7zr.1"
   sed -e s?"{DEST_SHARE_DOC}"?"${DEST_SHARE_DOC}/DOCS"?g man1/7zr.1 > "${DEST_DIR}${DEST_MAN}/man1/7zr.1"
   chmod 444 "${DEST_DIR}${DEST_MAN}/man1/7zr.1"
else
   echo "- installing ${DEST_DIR}${DEST_MAN}/man1/7z.1"
   grep -v "{DEST_SHARE_DOC}" man1/7z.1 > "${DEST_DIR}${DEST_MAN}/man1/7z.1"
   chmod 444 "${DEST_DIR}${DEST_MAN}/man1/7z.1"

   echo "- installing ${DEST_DIR}${DEST_MAN}/man1/7za.1"
   grep -v "{DEST_SHARE_DOC}" man1/7za.1 > "${DEST_DIR}${DEST_MAN}/man1/7za.1"
   chmod 444 "${DEST_DIR}${DEST_MAN}/man1/7za.1"

   echo "- installing ${DEST_DIR}${DEST_MAN}/man1/7zr.1"
   grep -v "{DEST_SHARE_DOC}" man1/7zr.1 > "${DEST_DIR}${DEST_MAN}/man1/7zr.1"
   chmod 444 "${DEST_DIR}${DEST_MAN}/man1/7zr.1"
fi

if [ -f README ]
then
  echo "- installing ${DEST_DIR}${DEST_SHARE_DOC}/README"
  mkdir -p  "${DEST_DIR}${DEST_SHARE_DOC}"
  cp README "${DEST_DIR}${DEST_SHARE_DOC}/README"
  chmod 444 "${DEST_DIR}${DEST_SHARE_DOC}/README"
fi

if [ -f ChangeLog ]
then
  echo "- installing ${DEST_DIR}${DEST_SHARE_DOC}/ChangeLog"
  mkdir -p     "${DEST_DIR}${DEST_SHARE_DOC}"
  cp ChangeLog "${DEST_DIR}${DEST_SHARE_DOC}/ChangeLog"
  chmod 444    "${DEST_DIR}${DEST_SHARE_DOC}/ChangeLog"
fi

if [ -d DOCS ]
then
  echo "- installing HTML help in ${DEST_DIR}${DEST_SHARE_DOC}/DOCS"
  mkdir -p "${DEST_DIR}${DEST_SHARE_DOC}"
  cp -r DOCS "${DEST_DIR}${DEST_SHARE_DOC}/DOCS"
  find "${DEST_DIR}${DEST_SHARE_DOC}/DOCS" -type d -exec chmod 555 {} \;
  find "${DEST_DIR}${DEST_SHARE_DOC}/DOCS" -type f -exec chmod 444 {} \;
fi

