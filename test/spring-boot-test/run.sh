#!/bin/bash
# Build the Spring Boot test application against freshly built 7-Zip-JBinding jars, run its tests on the plain
# class path and then the packaged jar, where Spring Boot's own class loader loads the binding (issue #35).
#
# Usage:  run.sh <build directory>                      e.g. run.sh ../../../SevenZipJBinding.Debug
#         run.sh <sevenzipjbinding.jar> <platform jar>  e.g. run.sh .../sevenzipjbinding.jar .../sevenzipjbinding-Linux-amd64.jar
#
# JAVA_HOME must be a JDK 17 or newer (Spring Boot 4); unset, /usr/lib/jvm/java-17-openjdk-amd64 is used if present.
# Gradle downloads Spring Boot from Maven Central on the first run.
set -euo pipefail
cd "$(dirname "$0")"

die() { echo "ERROR: $*" >&2; exit 1; }

case $# in
    1)  BUILD_DIR="$(cd "$1" && pwd)" || die "no such directory: $1"
        CORE_JAR="$BUILD_DIR/jbinding-java/sevenzipjbinding.jar"
        PLATFORM_JAR="$(ls "$BUILD_DIR"/sevenzipjbinding-*.jar 2>/dev/null | grep -v -- '-tests.jar' | head -1)"
        [[ -n "$PLATFORM_JAR" ]] || die "no platform jar in $BUILD_DIR" ;;
    2)  CORE_JAR="$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
        PLATFORM_JAR="$(cd "$(dirname "$2")" && pwd)/$(basename "$2")" ;;
    *)  sed -n '2,8p' "$0"; exit 1 ;;
esac
[[ -f "$CORE_JAR" ]] || die "not a file: $CORE_JAR"
[[ -f "$PLATFORM_JAR" ]] || die "not a file: $PLATFORM_JAR"

if [[ -z "${JAVA_HOME:-}" && -d /usr/lib/jvm/java-17-openjdk-amd64 ]]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
fi
JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"
JAVA_VERSION="$("$JAVA" -XshowSettings:properties -version 2>&1 | sed -n 's/^ *java.specification.version = //p')"
[[ "${JAVA_VERSION%%.*}" -ge 17 ]] 2>/dev/null || die "JDK 17 or newer needed, $JAVA is $JAVA_VERSION"

echo "==> 7-Zip-JBinding: $CORE_JAR"
echo "==>                 $PLATFORM_JAR"
echo "==> Java:           $JAVA ($JAVA_VERSION)"

echo "==> Build, test on the plain class path and package"
./gradlew --no-daemon --console=plain clean test bootJar -PsevenZipJBindingJars="$CORE_JAR:$PLATFORM_JAR"

JAR=build/libs/sevenzipjbinding-spring-boot-test.jar
echo "==> The packaged jar carries exactly the given binding jars"
for jar in "$CORE_JAR" "$PLATFORM_JAR"; do
    cmp <(unzip -p "$JAR" "BOOT-INF/lib/$(basename "$jar")") "$jar" \
        || die "$(basename "$jar") inside $JAR differs from $jar"
done

echo "==> Run the packaged jar (Spring Boot class loader)"
"$JAVA" -jar "$JAR" --spring.profiles.active=selftest --expect-boot-loader > build/selftest.log 2>&1 || true
cat build/selftest.log
grep -q '^SPRING-BOOT-TEST-OK' build/selftest.log || die "the packaged jar didn't report SPRING-BOOT-TEST-OK"
echo "==> SPRING BOOT TEST PASSED"
