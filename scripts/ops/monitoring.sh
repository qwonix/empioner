#!/usr/bin/env bash
source "$(dirname "$0")/common.sh"
lock_operations
check_config
dc --profile observability up -d --no-deps --wait --wait-timeout 180 \
  prometheus loki docker-socket-proxy alloy grafana \
  postgres-exporter mongo-exporter redis-exporter node-exporter
echo 'Grafana: SSH tunnel to 127.0.0.1:3000. Configure/test notification delivery separately.'
