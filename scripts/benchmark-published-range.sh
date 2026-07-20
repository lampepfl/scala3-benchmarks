#!/bin/sh
# $1: File with <commit version> lines
# $2: Filter for benchmarks

if [ $# -ne 2 ]; then
  echo "Usage: $0 <file logged by publish-range> <benchmark filter>" >&2
  exit 1
fi
input="$1"
filter="$2"

java_version="$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')"
java_major="${java_version%%.*}"
if [ $java_major -lt 25 ]; then
  echo "You need a JDK >= 25" >&2
  exit 2
fi

while read -r commit version
do
  cd ..
    sbt -Dcompiler.version="$version" "clean; bench / Jmh / run -gc true -foe true -prof comp -prof gc -rf json -rff $commit $filter" >&2
    perf="$(cat "bench/$commit" | grep '"score"' | head -n 1 | cut -d ':' -f 2)"
    printf "%s %s\n" "$commit" "$perf"
  cd - >/dev/null
done < "$input"
