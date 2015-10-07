#!/bin/bash

ME="$(readlink -f "$0")"
SCRIPT_DIR="$(dirname "$ME")"

WORKING_DIR=__deploy_maven

if [[ "$1" == "" || "$2" == "" ]] ; then
  echo "Usage: $0 <distribution-zip> <repo-to-deploy> [<platform-artifact-id-postfix>]"
  exit 1
fi

DIST_ZIP="$(readlink -f "$1")"

VERSION=$(echo $1 | sed -e 's/sevenzipjbinding-\([0-9.]\+-[^-]\+\).*/\1/')
if [[ "$3" != "" ]] ; then
  NAME_POSTFIX=$(echo $1 | sed -e 's/sevenzipjbinding-[0-9.]\+-[^-]\+\(-.*\)\.zip$/\1/')
  ARTIFACT_ID_POSTFIX="$3"
else
  NAME_POSTFIX=""
  ARTIFACT_ID_POSTFIX=""
fi
ARTIFACT_FILE_NAME="sevenzipjbinding$NAME_POSTFIX"
TARGET_REPO="$2"

if [[ "$2" == "sonatype-nexus-snapshots" ]] ; then
  URL="https://oss.sonatype.org/content/repositories/snapshots/"
  VERSION=$VERSION-SNAPSHOT
elif [[ "$2" == "nexus-releases" ]] ; then
  URL="https://oss.sonatype.org/service/local/staging/deploy/maven2/"
else
  echo "Unknown repo: '$2'"
  exit 1
fi

echo "Version: $VERSION, Name: '$NAME_POSTFIX'"

rm -rf "$WORKING_DIR"
mkdir "$WORKING_DIR"

(
  cd "$WORKING_DIR"
  cat "$SCRIPT_DIR/maven-pom-template.xml" \
    | sed -e "s/{{dist-version}}/$VERSION/" \
    | sed -e "s/{{dist-artifactId-postfix}}/$ARTIFACT_ID_POSTFIX/" \
    | sed -e "s/{{dist-name-postfix}}/$NAME_POSTFIX/" \
    > $ARTIFACT_FILE_NAME.pom
  unzip "$DIST_ZIP"
  cp sevenzip*/lib/$ARTIFACT_FILE_NAME.jar .
  if [[ "$3" == "" ]] ; then
    cp sevenzip*/java-src.zip $ARTIFACT_FILE_NAME-sources.jar
    cp sevenzip*/javadoc.zip $ARTIFACT_FILE_NAME-javadoc.jar
  fi

  mvn gpg:sign-and-deploy-file \
    -Durl=$URL \
    -DrepositoryId=$TARGET_REPO \
    -DpomFile=$ARTIFACT_FILE_NAME.pom \
    -Dfile=$ARTIFACT_FILE_NAME.jar

  if [[ "$3" == "" ]] ; then
    mvn gpg:sign-and-deploy-file \
      -Durl=$URL \
      -DrepositoryId=$TARGET_REPO \
      -DpomFile=$ARTIFACT_FILE_NAME.pom \
      -Dfile=$ARTIFACT_FILE_NAME-sources.jar \
      -Dclassifier=sources

    mvn gpg:sign-and-deploy-file \
      -Durl=$URL \
      -DrepositoryId=$TARGET_REPO \
      -DpomFile=$ARTIFACT_FILE_NAME.pom \
      -Dfile=$ARTIFACT_FILE_NAME-javadoc.jar \
      -Dclassifier=javadoc
  fi
)
