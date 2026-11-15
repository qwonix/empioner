#!/usr/bin/env bash
set -euo pipefail
script="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/release-tag.sh"
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
git init --bare -q "$tmp/remote.git"
git init -q "$tmp/repo"
cd "$tmp/repo"
git config user.name "Release test"
git config user.email "release-test@example.invalid"
git remote add origin "$tmp/remote.git"
git commit --allow-empty -qm initial
old="$(git rev-parse HEAD)"
git tag v0.3.8
git push -q origin --tags
git commit --allow-empty -qm merge
bash "$script"
[[ "$(git tag --points-at HEAD)" == "v0.4.0" ]]
bash "$script"
[[ "$(git tag -l 'v*' | wc -l | tr -d ' ')" == "2" ]]
git commit --allow-empty -qm next
bash "$script"
[[ "$(git tag --points-at HEAD)" == "v0.5.0" ]]
git checkout -q --detach "$old"
if bash "$script"; then
  echo "An older commit must not be released." >&2
  exit 1
fi
