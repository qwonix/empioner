#!/bin/sh
set -eu
psql --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" --set ON_ERROR_STOP=1 \
  --set monitor_password="$POSTGRES_EXPORTER_PASSWORD" <<'SQL'
SELECT 'CREATE ROLE empioner_metrics LOGIN' WHERE NOT EXISTS
  (SELECT 1 FROM pg_roles WHERE rolname = 'empioner_metrics') \gexec
ALTER ROLE empioner_metrics WITH PASSWORD :'monitor_password';
GRANT pg_monitor TO empioner_metrics;
GRANT CONNECT ON DATABASE :"DBNAME" TO empioner_metrics;
SQL
