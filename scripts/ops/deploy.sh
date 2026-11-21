#!/usr/bin/env bash
source "$(dirname "$0")/common.sh"
lock_operations
check_config
check_release_images
for name in postgres mongo redis bot service; do wait_healthy "$name"; done
old_bot=$(docker inspect --format '{{.Image}}' bot)
old_service=$(docker inspect --format '{{.Image}}' service)
dc pull bot service
# Stop the consumer before replacing the API. There must never be two pollers.
dc stop bot
if dc up -d --no-deps --wait --wait-timeout 240 service && \
   dc up -d --no-deps --wait --wait-timeout 240 bot; then
  save_release
else
  echo 'Release failed; restoring previous application images. DB migrations are NOT undone.' >&2
  dc stop bot
  BOT_IMAGE="$old_bot" SERVICE_IMAGE="$old_service" dc up -d --no-deps --wait --wait-timeout 240 service
  BOT_IMAGE="$old_bot" SERVICE_IMAGE="$old_service" dc up -d --no-deps --wait --wait-timeout 240 bot
  exit 1
fi
