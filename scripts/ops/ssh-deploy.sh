#!/usr/bin/env bash
set -euo pipefail

# This script is the forced command of the dedicated deployment SSH key.
# No user-supplied shell command is evaluated on the server.
IFS= read -r operation || { echo 'Missing operation' >&2; exit 1; }
case "$operation" in
  verify)
    [[ -r /etc/empioner/prod.env && -r /etc/empioner/image-owner && -w /var/lib/empioner ]] || {
      echo 'Deployment configuration is inaccessible' >&2; exit 1;
    }
    docker info >/dev/null
    echo 'SSH deployment access is ready'
    ;;
  deploy)
    IFS= read -r BOT_IMAGE || { echo 'Missing bot image' >&2; exit 1; }
    IFS= read -r SERVICE_IMAGE || { echo 'Missing service image' >&2; exit 1; }
    IFS= read -r owner < /etc/empioner/image-owner
    [[ "$owner" =~ ^[a-z0-9][a-z0-9_.-]*$ ]] || { echo 'Invalid registry owner' >&2; exit 1; }
    bot_prefix="$owner/empioner-telegram-bot@sha256:"
    service_prefix="$owner/empioner-telegram-service@sha256:"
    [[ "$BOT_IMAGE" == "$bot_prefix"* && "${BOT_IMAGE#"$bot_prefix"}" =~ ^[0-9a-f]{64}$ ]] || {
      echo 'Invalid bot image' >&2; exit 1;
    }
    [[ "$SERVICE_IMAGE" == "$service_prefix"* && "${SERVICE_IMAGE#"$service_prefix"}" =~ ^[0-9a-f]{64}$ ]] || {
      echo 'Invalid service image' >&2; exit 1;
    }
    export BOT_IMAGE SERVICE_IMAGE EMPIONER_PROJECT_DIR=/opt/empioner
    exec bash /opt/empioner/scripts/ops/deploy.sh
    ;;
  *) echo 'Unsupported operation' >&2; exit 1 ;;
esac
