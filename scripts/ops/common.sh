#!/usr/bin/env bash
set -euo pipefail
ROOT=${EMPIONER_PROJECT_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}
[[ -f "$ROOT/docker-compose.yml" ]] || { echo "Missing compose file in $ROOT" >&2; exit 1; }
ENV_FILE=${EMPIONER_ENV_FILE:-/etc/empioner/prod.env}
STATE_DIR=${EMPIONER_STATE_DIR:-/var/lib/empioner}

die() { echo "$*" >&2; exit 1; }
lock_operations() {
  mkdir -p "$STATE_DIR"
  exec 9>"$STATE_DIR/operations.lock"
  flock -n 9 || die 'Another deploy/backup/restore is running'
}
dc() {
  [[ -r "$ENV_FILE" ]] || die "Missing environment file: $ENV_FILE"
  local files=(--env-file "$ENV_FILE")
  if [[ -r "$STATE_DIR/current.env" ]]; then files+=(--env-file "$STATE_DIR/current.env"); fi
  docker compose --project-directory "$ROOT" -f "$ROOT/docker-compose.yml" -p empioner "${files[@]}" "$@"
}
check_config() { dc --profile observability config --quiet; }
configured_images() {
  dc config --format json | python3 -c 'import json,sys; s=json.load(sys.stdin)["services"]; print(s["bot"]["image"]); print(s["service"]["image"])'
}
check_release_images() {
  mapfile -t RELEASE_IMAGES < <(configured_images)
  [[ ${#RELEASE_IMAGES[@]} == 2 ]] || die 'Cannot resolve application images'
  for image in "${RELEASE_IMAGES[@]}"; do
    [[ "$image" =~ ^[^[:space:]]+@sha256:[0-9a-f]{64}$ ]] || die 'Set both application images to repository@sha256:digest'
  done
}
save_release() {
  umask 077
  [[ ! -f "$STATE_DIR/current.env" ]] || cp "$STATE_DIR/current.env" "$STATE_DIR/previous.env"
  printf 'BOT_IMAGE=%s\nSERVICE_IMAGE=%s\n' "${RELEASE_IMAGES[0]}" "${RELEASE_IMAGES[1]}" > "$STATE_DIR/current.env.tmp"
  mv "$STATE_DIR/current.env.tmp" "$STATE_DIR/current.env"
}
wait_healthy() {
  local name=$1
  for ((i=0; i<90; i++)); do
    [[ $(docker inspect --format '{{.State.Health.Status}}' "$name" 2>/dev/null || true) != healthy ]] || return 0
    sleep 2
  done
  die "Container $name did not become healthy"
}
