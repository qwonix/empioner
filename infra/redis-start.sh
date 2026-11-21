#!/bin/sh
set -eu
umask 077
case "$REDIS_USER" in
  ''|default|empioner_metrics|*[!a-zA-Z0-9_-]*) echo 'Invalid REDIS_USER' >&2; exit 1 ;;
esac
app_hash=$(printf '%s' "$REDIS_USER_PASSWORD" | sha256sum | cut -d ' ' -f 1)
monitor_hash=$(printf '%s' "$REDIS_EXPORTER_PASSWORD" | sha256sum | cut -d ' ' -f 1)
mkdir -p /tmp/empioner-redis
cat > /tmp/empioner-redis/users.acl <<EOF
user default off
user $REDIS_USER on #$app_hash ~* +@read +@write +@connection +info -flushall -flushdb -keys
user empioner_metrics on #$monitor_hash +ping +info +select +client +config|get +memory +slowlog +latency
EOF
chown -R redis:redis /tmp/empioner-redis
exec docker-entrypoint.sh redis-server \
  --aclfile /tmp/empioner-redis/users.acl \
  --appendonly yes --appendfsync everysec \
  --maxmemory 64mb --maxmemory-policy volatile-lru \
  --bind 0.0.0.0 --protected-mode yes
