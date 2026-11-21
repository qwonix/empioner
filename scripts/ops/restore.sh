#!/usr/bin/env bash
source "$(dirname "$0")/common.sh"
lock_operations
check_config
[[ $# == 2 && "$2" == --confirm-empty-target ]] || die 'Usage: restore.sh BACKUP_DIRECTORY --confirm-empty-target'
input=$(cd "$1" && pwd)
(cd "$input" && sha256sum -c SHA256SUMS)
for name in bot service; do
  [[ $(docker inspect --format '{{.State.Running}}' "$name" 2>/dev/null || echo false) != true ]] || die 'Stop both applications before restore'
done
tables=$(docker exec postgres sh -ec 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Atc "select count(*) from information_schema.tables where table_schema = '\''public'\''"')
[[ "$tables" == 0 ]] || die 'PostgreSQL target is not empty; refusing to overwrite'
docker exec mongo mongosh --quiet --nodb --eval '
  const uri = "mongodb://" + encodeURIComponent(process.env.MONGO_INITDB_ROOT_USERNAME) + ":" + encodeURIComponent(process.env.MONGO_INITDB_ROOT_PASSWORD) + "@127.0.0.1:27017/?authSource=admin";
  const admin = new Mongo(uri).getDB("admin");
  const databases = admin.runCommand({listDatabases:1}).databases.filter(x => !["admin","local","config"].includes(x.name));
  for (const item of databases) if (admin.getSiblingDB(item.name).getCollectionNames().some(x => !x.startsWith("system."))) quit(1);' || die 'MongoDB target is not empty'
[[ $(docker exec redis sh -ec 'REDISCLI_AUTH="$REDIS_USER_PASSWORD" redis-cli --user "$REDIS_USER" --no-auth-warning DBSIZE') == 0 ]] || die 'Redis target is not empty'
docker cp "$input/postgres.dump" postgres:/tmp/empioner.pg.dump >/dev/null
docker exec postgres sh -ec 'pg_restore --exit-on-error --no-owner --no-acl -U "$POSTGRES_USER" -d "$POSTGRES_DB" /tmp/empioner.pg.dump'
docker cp "$input/mongo.archive.gz" mongo:/tmp/empioner.mongo.gz >/dev/null
docker exec mongo mongosh --quiet --nodb --eval '
  const uri = "mongodb://" + encodeURIComponent(process.env.MONGO_INITDB_ROOT_USERNAME) + "@127.0.0.1:27017/?authSource=admin";
  require("fs").writeFileSync("/tmp/empioner-tools.json", JSON.stringify({uri, password: process.env.MONGO_INITDB_ROOT_PASSWORD}), {mode:0o600});'
docker exec mongo mongorestore --config=/tmp/empioner-tools.json --archive=/tmp/empioner.mongo.gz --gzip \
  --nsExclude='admin.*' --nsExclude='config.*' --nsExclude='local.*' --stopOnError
docker exec mongo sh -c 'rm -f /tmp/empioner-tools.json'
docker stop redis >/dev/null
redis_image=$(docker inspect --format '{{.Image}}' redis)
docker run --rm --network none --volumes-from redis --entrypoint sh \
  -v "$input:/backup:ro" "$redis_image" -ec 'tar -xzf /backup/redis.tar.gz -C /data'
docker start redis >/dev/null
wait_healthy redis
echo 'Restore complete. Verify the actual Mongo application database, row counts and roles before bootstrap apps.'
echo 'Custom PostgreSQL global roles/grants are saved separately and require explicit review; they were not applied.'
