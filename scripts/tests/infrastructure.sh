#!/usr/bin/env bash
set -Eeuo pipefail
trap 'echo "Infrastructure check failed at line $LINENO" >&2' ERR
[[ ${CI:-} == true ]] || { echo 'This destructive smoke test runs only in disposable CI' >&2; exit 1; }
ROOT=$(cd "$(dirname "$0")/../.." && pwd)
cd "$ROOT"
# Never attach this test to existing application containers.
for name in bot service postgres mongo redis prometheus loki grafana alloy; do
  if docker inspect "$name" >/dev/null 2>&1; then echo "Existing container $name; refusing smoke test" >&2; exit 1; fi
done
temporary=$(mktemp -d)
export EMPIONER_ENV_FILE="$ROOT/.env.example"
export EMPIONER_STATE_DIR="$temporary/state"
dc() { docker compose --env-file "$EMPIONER_ENV_FILE" -f "$ROOT/docker-compose.yml" -p empioner "$@"; }
cleanup() {
  status=$?
  if [[ $status != 0 ]]; then dc --profile observability logs --tail 80 >&2 || true; fi
  dc --profile observability down -v --remove-orphans >/dev/null 2>&1 || true
  docker rm -f bot service empioner-log-smoke >/dev/null 2>&1 || true
  rm -rf "$temporary"
}
trap cleanup EXIT
for script in infra/*.sh scripts/ops/*.sh; do bash -n "$script"; done
dc --profile observability config --quiet
dc --profile observability config --format json > "$temporary/compose.json"
python3 - "$temporary/compose.json" <<'PY'
import json, sys
services = json.load(open(sys.argv[1]))['services']
assert sum(int(s['mem_limit']) for s in services.values()) <= 6 * 1024**3
for name, service in services.items():
    assert service['logging']['driver'] == 'local'
    for port in service.get('ports', []):
        assert name == 'grafana' and port['host_ip'] == '127.0.0.1'
assert services['bot']['environment']['SPRING_DATA_REDIS_PASSWORD'] == 'replace-redis-app-password'
assert services['redis-exporter']['environment']['REDIS_PASSWORD'] == 'replace-redis-monitor-password'
assert not any(v['target'] == '/var/run/docker.sock' for v in services['alloy'].get('volumes', []))
PY
docker run --rm --entrypoint promtool -v "$ROOT/monitoring/prometheus:/etc/prometheus:ro" \
  prom/prometheus:v3.6.0 check config /etc/prometheus/prometheus.yml
docker run --rm -v "$ROOT/monitoring/loki/loki-config.yaml:/etc/loki/config.yaml:ro" \
  grafana/loki:3.5.5 -config.file=/etc/loki/config.yaml -verify-config=true
docker run --rm -e GRAFANA_LOKI_URL=http://loki:3100/loki/api/v1/push \
  -v "$ROOT/monitoring/alloy/config.alloy:/etc/alloy/config.alloy:ro" \
  grafana/alloy:v1.11.0 validate /etc/alloy/config.alloy
dc up -d --wait --wait-timeout 240 postgres mongo redis
if docker exec -e REDISCLI_AUTH=wrong redis redis-cli --user empioner -e ping; then exit 1; fi
if docker exec -e MONGODB_PASSWORD=wrong mongo mongosh --quiet --nodb /opt/empioner/mongo-health.js; then exit 1; fi
docker exec redis sh -ec 'REDISCLI_AUTH="$REDIS_USER_PASSWORD" redis-cli --user "$REDIS_USER" -e SET migration-probe preserved EX 3600'
docker exec redis sh -ec 'REDISCLI_AUTH="$REDIS_USER_PASSWORD" redis-cli --user "$REDIS_USER" -e INFO server | grep -q "^redis_version:"'
docker exec postgres sh -ec 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=1 -c "CREATE TABLE migration_probe(id integer); INSERT INTO migration_probe VALUES (42);"'
docker exec mongo mongosh --quiet --nodb --eval '
  const c = new Mongo("mongodb://" + encodeURIComponent(process.env.MONGODB_USER) + ":" + encodeURIComponent(process.env.MONGODB_PASSWORD) + "@127.0.0.1:27017/" + process.env.MONGODB_DATABASE);
  c.getDB(process.env.MONGODB_DATABASE).user.insertOne({_id:Long("123"),roles:["ADMIN"]});'
dc --profile observability up -d --no-deps prometheus loki docker-socket-proxy alloy grafana \
  postgres-exporter mongo-exporter redis-exporter node-exporter
fetch() { docker exec prometheus wget -qO- "$1"; }
until_ready() {
  for ((i=0; i<90; i++)); do if fetch "$1" >/dev/null 2>&1; then return; fi; sleep 2; done
  echo "Endpoint not ready: $1" >&2; exit 1
}
until_ready http://loki:3100/ready
until_ready http://grafana:3000/api/health
for pair in postgres-exporter:9187/metrics mongo-exporter:9216/metrics redis-exporter:9121/metrics node-exporter:9100/metrics; do until_ready "http://$pair"; done
fetch http://postgres-exporter:9187/metrics | grep '^pg_up 1' >/dev/null
mongo_metrics=$(fetch http://mongo-exporter:9216/metrics)
printf '%s\n' "$mongo_metrics" | grep -E '^mongodb_up(\{[^}]*\})? 1$' >/dev/null || {
  printf '%s\n' "$mongo_metrics" | grep '^mongodb_up' >&2 || true
  exit 1
}
fetch http://redis-exporter:9121/metrics | grep '^redis_up 1' >/dev/null
fetch http://node-exporter:9100/metrics | grep '^node_memory_MemTotal_bytes ' >/dev/null
payload=$(printf '{"streams":[{"stream":{"job":"ci"},"values":[["%s","persistence-smoke"]]}]}' "$(date +%s%N)")
docker exec prometheus wget -qO- --header='Content-Type: application/json' --post-data="$payload" http://loki:3100/loki/api/v1/push
dc up -d --force-recreate --no-deps loki
until_ready http://loki:3100/ready
fetch 'http://loki:3100/loki/api/v1/query_range?query=%7Bjob%3D%22ci%22%7D' | grep -q persistence-smoke
docker run -d --name empioner-log-smoke redis:7.4.0-alpine sh -c 'echo alloy-delivery-smoke; sleep 60' >/dev/null
for ((i=0; i<30; i++)); do
  if fetch 'http://loki:3100/loki/api/v1/query_range?query=%7Bcontainer%3D%22empioner-log-smoke%22%7D' | grep -q alloy-delivery-smoke; then break; fi
  sleep 2
done
fetch 'http://loki:3100/loki/api/v1/query_range?query=%7Bcontainer%3D%22empioner-log-smoke%22%7D' | grep -q alloy-delivery-smoke
# Lightweight writer stand-ins exercise the real backup script without Telegram.
docker run -d --name bot redis:7.4.0-alpine sleep 600 >/dev/null
docker run -d --name service redis:7.4.0-alpine sleep 600 >/dev/null
bash scripts/ops/backup.sh "$temporary/backup" --leave-stopped
dc --profile observability down -v --remove-orphans
docker rm bot service >/dev/null
dc up -d --wait --wait-timeout 240 postgres mongo redis
bash scripts/ops/restore.sh "$temporary/backup" --confirm-empty-target
[[ $(docker exec postgres sh -ec 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Atc "SELECT id FROM migration_probe"') == 42 ]]
[[ $(docker exec redis sh -ec 'REDISCLI_AUTH="$REDIS_USER_PASSWORD" redis-cli --user "$REDIS_USER" GET migration-probe') == preserved ]]
docker exec mongo mongosh --quiet --nodb --eval '
  const c = new Mongo("mongodb://" + encodeURIComponent(process.env.MONGODB_USER) + ":" + encodeURIComponent(process.env.MONGODB_PASSWORD) + "@127.0.0.1:27017/" + process.env.MONGODB_DATABASE);
  if (c.getDB(process.env.MONGODB_DATABASE).user.findOne({_id:Long("123")}).roles[0] !== "ADMIN") quit(1);'
if bash scripts/ops/restore.sh "$temporary/backup" --confirm-empty-target; then echo 'Restore must reject a populated target' >&2; exit 1; fi
echo 'Infrastructure smoke test passed'
