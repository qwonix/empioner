#!/usr/bin/env bash
source "$(dirname "$0")/common.sh"
lock_operations
check_config
case "${1:-}" in
  dependencies)
    dc up -d --wait --wait-timeout 240 postgres mongo redis
    echo 'Dependencies are ready. Restore data before starting applications.'
    ;;
  apps)
    [[ ${2:-} == --confirm-single-poller ]] || die 'Stop the old bot and pass --confirm-single-poller'
    check_release_images
    dc pull bot service
    dc up -d --no-deps --wait --wait-timeout 240 service
    dc up -d --no-deps --wait --wait-timeout 240 bot
    save_release
    ;;
  *) die 'Usage: bootstrap.sh dependencies | apps --confirm-single-poller' ;;
esac
