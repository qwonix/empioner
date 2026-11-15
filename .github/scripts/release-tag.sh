#!/usr/bin/env bash
set -euo pipefail

# This script runs only for a merged PR, after checkout at its merge commit.
git fetch --force --tags origin
release_commit="$(git rev-parse HEAD)"
latest="$(git tag -l 'v*' | grep -E '^v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$' | sort -V | tail -n 1 || true)"
if [[ -z "$latest" ]]; then
  echo "No vMAJOR.MINOR.PATCH tag found; refusing to choose a starting version." >&2
  exit 1
fi

# An older queued merge or a retry must never deploy over a newer tagged commit.
latest_commit="$(git rev-list -n 1 "$latest")"
if ! git merge-base --is-ancestor "$latest_commit" "$release_commit"; then
  echo "Latest release $latest is not an ancestor of this merge; refusing to deploy." >&2
  exit 1
fi

existing="$(git tag --points-at "$release_commit" | grep -E '^v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$' | sort -V | tail -n 1 || true)"
if [[ -n "$existing" ]]; then
  echo "Commit already tagged $existing; reusing it."
  exit 0
fi

version="${latest#v}"
major="${version%%.*}"
rest="${version#*.}"
minor="${rest%%.*}"
next="v${major}.$((10#$minor + 1)).0"
git tag "$next" "$release_commit"
git push origin "refs/tags/$next"
echo "Created $next for $release_commit"
