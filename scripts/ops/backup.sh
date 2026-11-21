#!/usr/bin/env bash
# Consistent small-installation backup: briefly pause all application writers.
source "$(dirname "$0")/common.sh"
lock_operations
umask 077
output=${1:-"$STATE_DIR/backups/$(date -u +%Y%m%dT%H%M%SZ)"}
leave_stopped=${2:-}
[[ -z "$leave_stopped" || "$leave_stopped" == --leave-stopped ]] || die 'Unknown backup option'
[[ ! -e "$output" ]] || die 'Backup destination must not exist'
mkdir -p "$(dirname "$output")"
mkdir -m 700 "$output"
output=$(cd "$output" && pwd)
resume=()
for name in bot service redis; do
  if [[ $(docker inspect --format '{{.State.Running}}' "$name") == true ]]; then resume+=("$name"); fi
done
cleanup() {
  if [[ "$leave_stopped" != --leave-stopped ]]; then
    for name in redis service bot; do
      if [[ " ${resume[*]} " == *" $name "* ]]; then docker start "$name" >/dev/null || true; fi
    done
  fi
}
trap cleanup EXIT
docker stop bot service >/dev/null
docker exec postgres sh -ec 'pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Fc -f /tmp/empioner.pg.dump; pg_dumpall -U "$POSTGRES_USER" --globals-only -f /tmp/empioner.pg.globals.sql'
docker cp postgres:/tmp/empioner.pg.dump "$output/postgres.dump" >/dev/null
docker cp postgres:/tmp/empioner.pg.globals.sql "$output/postgres-globals.sql" >/dev/null
# Support the old unauthenticated Mongo container as well as the new authenticated one.
if [[ -n ${MONGODB_BACKUP_CONFIG:-} ]]; then
  docker cp "$MONGODB_BACKUP_CONFIG" mongo:/tmp/empioner-tools.json >/dev/null
else
  docker exec mongo mongosh --quiet --nodb --eval '
  const root = process.env.MONGO_INITDB_ROOT_USERNAME;
  const uri = root ? "mongodb://" + encodeURIComponent(root) + "@127.0.0.1:27017/?authSource=admin" : "mongodb://127.0.0.1:27017";
  const config = root ? {uri, password: process.env.MONGO_INITDB_ROOT_PASSWORD} : {uri};
  require("fs").writeFileSync("/tmp/empioner-tools.json", JSON.stringify(config), {mode: 0o600});'
fi
docker exec mongo mongodump --config=/tmp/empioner-tools.json --archive=/tmp/empioner.mongo.gz --gzip
docker exec mongo sh -c 'rm -f /tmp/empioner-tools.json'
docker cp mongo:/tmp/empioner.mongo.gz "$output/mongo.archive.gz" >/dev/null
docker stop redis >/dev/null
redis_image=$(docker inspect --format '{{.Image}}' redis)
docker run --rm --network none --volumes-from redis:ro --entrypoint sh \
  -v "$output:/backup" "$redis_image" -ec 'tar -czf /backup/redis.tar.gz -C /data .'
docker inspect --format '{{.Name}} {{.Config.Image}} {{.Image}}' postgres mongo redis bot service > "$output/images.txt"
(cd "$output" && sha256sum postgres.dump postgres-globals.sql mongo.archive.gz redis.tar.gz images.txt > SHA256SUMS)
echo "Backup complete: $output"
echo 'Copy/encrypt this directory offsite. A local backup does not protect against VPS loss.'
