# Observability на 8 ГБ

## Запуск и доступ

После готовности баз и приложений:

```bash
bash scripts/ops/monitoring.sh
ssh -L 3000:127.0.0.1:3000 deploy@VPS
```

Открыть `http://localhost:3000`. Пароль задаётся через `GRAFANA_ADMIN_PASSWORD`;
на существующем Grafana volume переменная НЕ заменяет пароль уже созданного
пользователя: сменить его штатным механизмом Grafana. Самостоятельно открывать
3000 наружу без HTTPS и контроля доступа не требуется.

Профиль `observability` включает Prometheus, Loki, Alloy, Grafana, экспортеры БД,
node_exporter и Docker socket proxy. Без профиля базовые сервисы работают отдельно.
Обычный application deploy не пересоздаёт базы или monitoring. Изменения конфигов
инфраструктуры применять отдельным запуском и контролировать необходимость reload/
recreate; смена пароля в env не переинициализирует пользователей существующих БД.

## Память и диск

Сумма жёстких лимитов всех 14 контейнеров — 6048 MiB (5.91 GiB).
Это верхние лимиты, не зарезервированная память и не измеренный working set.
Оставшиеся примерно 2 GiB нужны ОС, Docker и запасу. Для JVM heap меньше
container limit: bot 384/768 MiB, service 512/1024 MiB. Не увеличивать Xmx
до значения mem_limit: есть direct buffers, metaspace и потоки.

Mongo WiredTiger cache 0.25 GiB при лимите 768 MiB; PostgreSQL shared_buffers
128 MiB и Hikari pool 5. Redis maxmemory 64 MiB, container 256 MiB, политика
volatile-lru: TTL message IDs могут вытесняться раньше 48 ч под нагрузкой. Они не
являются каталогом или пользовательскими ролями. Наблюдать evictions и UX.

Prometheus хранит 15 дней, size retention 5 GB (WAL/head и служебные расходы
нуждаются в дополнительном месте). Loki compactor удаляет логи старше 7 дней;
удаление асинхронное и не является жёсткой дисковой квотой. Docker local logging:
10 MB × 3 файла на контейнер. Disk alerts и запас свободного места всё равно нужны.

Новые volumes: `empioner_prometheus_data`, `empioner_loki_data`,
`empioner_alloy_data`. Grafana сохраняет прежний `empioner_grafana-storage`.
В старой Loki volumes были смонтированы не по фактическим путям. Если нужна старая
история, экспортировать реальные `/loki` данные старого контейнера до удаления;
новый volume сам её не найдёт. Старый Prometheus мог использовать anonymous volume.

## Дашборд и сигналы

Provisioned dashboard `Empioner · Production` включает application outcomes и
latency, Telegram API, JVM, память/CPU/диск хоста, DB health и логи. CPU приложений
показывается по `process_cpu_usage`: JVM RSS в текущей конфигурации не публикуется.
Отдельный дашборд `Empioner · Product` показывает регистрации, операции выдачи видео,
их ошибки и обработку событий. Host exporter
не использует host network: сетевые collectors отключены, чтобы не показывать
сетевую статистику контейнера как статистику VPS. CPU/RAM/disk читаются с хоста.
Полные container working-set/OOM metrics через cAdvisor пока не добавлены.

Метрики бота:

- `empioner_updates_received_total{type}` — получение, не успешная обработка;
- `empioner_updates_processed_total{type,outcome}` — success/error/partial/ignored;
- `empioner_update_processing_seconds_{bucket,count,sum}` — вся обработка update;
- `empioner_telegram_api_requests_total{method,outcome,error_type}` — synchronous execute;
- `empioner_telegram_api_request_seconds_{count,sum}` — время Telegram API;
- `empioner_worker_inflight` и `empioner_worker_started_timestamp_seconds` — зависание обработки;
- `empioner_users_registered_total` — первые регистрации в текущей базе после установки
  метрики; старый пользователь, вернувшийся из исторической выгрузки, может попасть в счётчик;
- `empioner_users_snapshot{source}` — периодический подсчёт текущих пользователей,
  идентификаторов старой выгрузки и их объединения без повторов;
- `empioner_users_snapshot_updated_timestamp_seconds` — время последнего успешного подсчёта;
- `empioner_video_delivery_total{operation,outcome}` — синхронные отправки и изменения
  видео через Telegram API, с результатом success/error;
- старый `telegram_updates_total{status}` теперь тоже фиксирует реальный итог.

Вложенные handlers, которые восстанавливаются после исключений, отмечают partial;
контекст очищается между updates. Смена потоков потребует явного переноса контекста.
Async Telegram methods wrapper не измеряет: текущий код использует sync execute.
Успешный API-вызов означает принятие Telegram, а не просмотр видео пользователем.
Изменение качества и повторная отправка могут создать несколько операций для одного
просмотра. Счётчики не восстанавливают историю до установки и значения `increase`
приблизительны из-за интервалов сбора и возможных перезапусков. Метрики не содержат
идентификаторы пользователей или видео. Для уникальных активных пользователей за
24 часа/7 дней нужна отдельная периодическая агрегация `activity` с индексом по
времени; не выполнять полный подсчёт коллекции при каждом сборе Prometheus.
Отсутствие updates ночью нормально. Отдельного heartbeat успешных пустых polling
циклов здесь нет: health Spring не доказывает исправность long polling.

Историческая коллекция `analytics_historical_users` содержит только BSON long `_id`
и не является рабочей коллекцией пользователей. Импорт выполняется скриптом
`python3 scripts/ops/import-historical-users.py /путь/к/защищённой/user.json`;
он нормализует два старых формата ID, удаляет повторы, допускает безопасное
повторение того же импорта и отказывается смешивать другой набор с существующим.
Исходный экспорт хранить отдельно с ограниченным доступом. При стандартном
`mongodump` коллекция попадает в резервную копию MongoDB. Метрика объединения
обновляется примерно раз в 15 минут; при ошибке сохраняет последнее значение,
поэтому проверяйте время успешного обновления. Она означает пользователей,
встречавшихся в этих двух источниках, а не активную аудиторию. В выгрузке нет
дат регистрации, поэтому восстановить динамику прошлых регистраций нельзя.

Нельзя добавлять user/chat/update IDs, текст сообщений и UUID каталога в labels.
Полный текст сообщений и file IDs убраны из обычных логов. UUID в MDC остаётся
correlation ID, distributed tracing не включён; ложная ссылка на trace backend
из datasource удалена. Tracing/Tempo и переход Mongo→PostgreSQL — отдельные задачи.

Grafana получает только Prometheus/Loki. Нет root-доступа Grafana к рабочим БД
или зависимости от платного MongoDB datasource. У экспортеров отдельные identities.
Alloy обращается к Docker через proxy с запрещёнными write requests. Read endpoints
всё ещё дают доступ к логам/metadata контейнеров; proxy не является полной sandbox.

## Уведомления и внешняя проверка

Prometheus загружает правила из `alerts.yml`: scrape/DB down, память <15%, диск
<20%, ошибки >5% при достаточной выборке, p95 >2 с, зависший update и Watchdog.
Пороговые значения — начальные и требуют baseline. Правила и firing-состояния
видны через Prometheus datasource / панель ALERTS.

**Доставка уведомлений не настроена автоматически:** получатель и канал не были
заданы. Перед production подключить Alertmanager с нужными receivers к этим
Prometheus rules, либо перенести нужные запросы в Grafana-managed alert rules и
настроить Grafana contact point. Наличие Prometheus rules само по себе не включает
отправку сообщений из Grafana. Выполнить test notification и настоящий тест отказа.

Watchdog должен поступать во ВНЕШНИЙ dead-man receiver. Если весь VPS выключен,
локальный Prometheus не сможет сообщить о своей остановке. Нужен запасной канал,
если основной канал — тот же Telegram. Контроль backup должен проверять успешную
выгрузку offsite, не только существование локального архива.

## Проверки

CI запускает реальные версии Prometheus promtool, Loki verify-config и Alloy
validate, проверяет DB auth/exporters, сохранение Loki при recreate, доставку
Docker-логов через proxy и round-trip PostgreSQL/Mongo/Redis backup/restore.
Это не нагрузочный тест продового VPS. После переноса собрать 1–2 недели baseline,
проверить пики startup/backup/query, отсутствие OOM, ≥20–25% MemAvailable и disk
growth. При нехватке сначала найти потребителя, затем менять бюджеты или RAM.
