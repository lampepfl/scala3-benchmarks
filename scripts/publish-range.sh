#!/bin/sh
# Args:
# $1: Scala 3 repository
# $2: Commit, oldest in the range, inclusive
# $3: Commit, newest of the range, inclusive

if [ $# -ne 3 ]; then
  echo "Usage: $0 <scala3-repo> <oldest-inclusive> <newest-inclusive>" >&2
  exit 1
fi
repo="$1"
oldest="$2"
newest="$3"

if [ -z "$(git -C "$repo" status --porcelain)" ]; then 
  : # Working directory clean
else 
  echo "There are uncommitted changes, aborting" >&2
  exit 2
fi

# $1: ref
commit_of() {
  git -C "$repo" rev-parse "$1"
}
# $1: ref
set_commit() {
  git -C "$repo" checkout "$1" 1>/dev/null 2>/dev/null
}

scala_build() {
  cd "$repo"
    BENCHMARKBUILD=yes sbt clean community-build/prepareCommunityBuild 1>&2
  cd '-' >/dev/null
}
scala_version() {
  cat "$repo/community-build/scala3-bootstrapped.version"
}

current="$(commit_of 'HEAD')"
oldest="$(commit_of "$oldest")" # we might have gotten short commit IDs, so we must expand them
newest="$(commit_of "$newest")"

set_commit "$newest"
while true; do
  scala_build
  printf "%s %s\n" "$(commit_of 'HEAD')" "$(scala_version)"

  [ "$(commit_of 'HEAD')" != "$oldest" ] || break
  set_commit 'HEAD~1'
done
set_commit "$current"
