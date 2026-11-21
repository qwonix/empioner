# Production на VPS и перенос данных

## До начала

Целевая конфигурация: Linux x86_64, 4 vCPU, 8 ГБ RAM, SSD/NVMe от 100–128 ГБ.
Это стартовый бюджет для небольшой нагрузки, а не гарантия пропускной способности.
Установить Docker Engine, Compose plugin >=2.24, Git, Bash, Python 3 и util-linux
(`flock`). Java и Gradle на VPS не нужны: приложения собираются в GitHub Actions.

Пользователь деплоя имеет доступ к Docker. Такой доступ фактически даёт
административные возможности: на этом runner нельзя выполнять недоверенные PR.
CI с тестами запускается на `ubuntu-latest`; на VPS выполняются только доверенные
production jobs. Не назначать production-метку другим runner.

В firewall провайдера разрешить SSH с нужных адресов. Приложения и базы не
публикуют host ports. Grafana доступна только через localhost/SSH. Docker API
proxy не публикуется и находится в отдельной внутренней сети.

## Каталоги и конфигурация

Пример подготовки администратором (заменить `deploy` своим пользователем):

```bash
sudo install -d -o deploy -g deploy -m 700 /etc/empioner /var/lib/empioner
sudo install -d -o deploy -g deploy -m 755 /opt/empioner
git clone https://github.com/qwonix/empioner.git /opt/empioner
cd /opt/empioner
git checkout master
install -m 600 .env.example /etc/empioner/prod.env
```

Отредактировать `/etc/empioner/prod.env`: заменить ВСЕ значения `replace-*`,
UUID изображений и параметры бота реальными значениями. Пароли можно генерировать
через `openssl rand -hex 32`. Значения с `$` заключать в одинарные кавычки.
Секреты не класть в runner checkout, Git, Docker build context или PR-логи.

`MONGODB_DATABASE` — фактическая прикладная БД старого сервера. В старой
конфигурации Spring без явного имени использовал `test`. `admin`/authSource не
является именем прикладной БД. Не выбирать другое имя без переноса данных.

Новые Mongo root/app/monitoring пользователи различаются. Init создаёт app и
monitoring users только на пустом volume. PostgreSQL init создаёт
`empioner_metrics` с `pg_monitor`. Redis использует отдельные ACL для bot и
экспортера; старый `REDIS_PASSWORD` больше не используется.

Скрипты читают Compose env-файл, но не исполняют его через `source`.
Пути переопределяются `EMPIONER_ENV_FILE` и `EMPIONER_STATE_DIR`.
Jobs runner используют постоянный `EMPIONER_PROJECT_DIR=/opt/empioner`, поэтому
bind mounts не зависят от временного checkout Actions. При изменении Compose,
скриптов или конфигураций мониторинга сначала обновить этот постоянный checkout
до проверенного коммита и отдельно применить изменения; обычный app deploy их
автоматически не накатывает.
Имена project и контейнеров сохранены: `empioner`, `postgres`, `mongo`, `redis`,
`service`, `bot`. Не переименовывать volumes во время переноса.

## Сборка первого релиза

Workflow **Docker** выполняет тесты и публикует образы. Для ручного запуска
оставить `deploy=false`. В registry взять digest каждой сборки и прописать:

```dotenv
BOT_IMAGE=qwonix/empioner-telegram-bot@sha256:ПОЛНЫЙ_DIGEST
SERVICE_IMAGE=qwonix/empioner-telegram-service@sha256:ПОЛНЫЙ_DIGEST
```

Ops-скрипты требуют полные digest, `latest` не допускается. Для закрытого registry
выполнить `docker login` под пользователем деплоя. При обычном CI-релизе digest
передаются из build outputs и сохраняются после успешного обновления в
`/var/lib/empioner/current.env`. Этот manifest перекрывает старые image-значения
из prod.env. `previous.env` сохраняет предыдущую успешно записанную пару.

## Первый запуск нового сервера

```bash
cd /opt/empioner
bash scripts/ops/bootstrap.sh dependencies
```

Поднимутся только PostgreSQL, MongoDB и Redis с ожиданием health. Пока НЕ
запускать service: сначала восстановить существующие данные и таблицы Liquibase.
На чистой установке без переноса этот шаг восстановления пропускается.

## Финальная копия на старом сервере

Перед окном переноса остановить/дождаться всех деплоев и приостановить старые
workflow. Получить код скриптов в отдельный checkout; новый Compose на старом
сервере пока не запускать. Скрипт backup работает по существующим именам
контейнеров и не требует нового prod.env.

Проверить место для копий и остановить все внешние записи в базы. Затем:

```bash
EMPIONER_STATE_DIR="$HOME/empioner-migration-state" \
  bash scripts/ops/backup.sh "$HOME/empioner-backups/final" --leave-stopped
```

Каталог `final` должен ещё не существовать. Скрипт остановит bot/service,
сделает PostgreSQL custom dump и отдельный globals dump, Mongo archive,
остановит Redis и сохранит ВЕСЬ `/data`, включая AOF manifest и файлы.
Запишет image refs/IDs и SHA-256. `--leave-stopped` оставляет старые приложения и
Redis остановленными даже при ошибке: разбирать ошибку и возобновлять работу
нужно осознанно. Без этого флага скрипт восстанавливает исходно запущенные
контейнеры; такой backup кратковременно прерывает работу бота.

Поддерживается старая Mongo без auth и новая с root credentials в container env.
Если auth на старой Mongo настраивалась вручную, передать `MONGODB_BACKUP_CONFIG`
с абсолютным путём к защищённому YAML-файлу Mongo Database Tools (`uri`, `password`).
Не передавать пароль в аргументах команд и не публиковать архивы: globals dump
содержит хэши паролей, Mongo — данные пользователей и роли.

Передать весь каталог backup через SSH/SCP на новый VPS и независимо сохранить
зашифрованную offsite-копию. Секреты и изменённые вне Git конфиги сохранить отдельно.

## Восстановление на новом сервере

```bash
cd /opt/empioner
bash scripts/ops/restore.sh /absolute/path/to/final --confirm-empty-target
```

Скрипт проверяет SHA-256, отсутствие работающих приложений и пустоту целевых
хранилищ. Восстанавливает PostgreSQL, прикладные Mongo namespaces и Redis.
Mongo admin/config/local не переносятся поверх новых пользователей, созданных
init-скриптами. Коллекция `user.roles` при этом переносится как прикладные данные.

PostgreSQL восстанавливается под новым `POSTGRES_USER` без старых owner/ACL.
Для текущего приложения это одна основная роль. Если вручную создавались другие
роли, grants, дополнительные PostgreSQL БД или extensions, перенести их отдельным
проверенным шагом: `postgres-globals.sql` сохранён, но автоматически не исполняется.
Скрипт не является универсальным инструментом миграции произвольного кластера БД.

При частичном провале restore скрипт НЕ удаляет уже восстановленное. Повторный
запуск в непустую базу будет отклонён. Переподготовка целевых volumes требует
отдельного решения оператора; исходный сервер и backup не менять.

Сравнить данные до/после: число записей основных таблиц, пользователей и activity,
роли ADMIN, каталог, `telegram_file_id`, актуальную прикладную БД Mongo и
`databasechangelog`. Проверить, что PostgreSQL 17, MongoDB 7.0.25 и Redis 7.4
совместимы с исходными образами. Не совмещать этот перенос с major upgrades.

## Переключение приложения

Старый bot должен быть остановлен: токен прежний, poller одновременно только один.
После проверки restore:

```bash
bash scripts/ops/bootstrap.sh apps --confirm-single-poller
```

Сначала запускается service и ожидается health, затем bot. Выполнить `/start`,
переходы по каталогу, выдачу видео, административные операции и отказ этих
операций обычному пользователю. Не создавать нового Telegram-бота: file IDs
относятся к существующему боту. Long polling не требует смены webhook/DNS.

Если требуется назначить первого администратора, сначала он отправляет `/start`,
затем на сервере выполняется `bash scripts/ops/admin.sh TELEGRAM_USER_ID`.
Скрипт меняет только существующего пользователя и не создаёт публичной mutation.

## Новый runner и следующие релизы

В GitHub зарегистрировать новый repository runner Linux x64 с custom label
`empioner-prod`, установить его как systemd service и дать доступ к Docker,
`/etc/empioner/prod.env`, `/var/lib/empioner`. До готовности нового VPS
`EMPIONER_DEPLOY_ENABLED` оставить unset/false. Старый runner остановить и
удалить/снять production label; прежний VPS сохранить для отката.

Workflow **Prepare production VPS** повторяет три явных фазы: dependencies,
apps (с подтверждением single poller), monitoring. Он работает только с master.
Первый bootstrap допустимо выполнить вручную до регистрации runner.

Для штатного автодеплоя установить repository variable
`EMPIONER_DEPLOY_ENABLED=true`. В repository secrets для сборки остаются
`DOCKERHUB_USERNAME` и `DOCKERHUB_TOKEN`. Runtime secrets теперь берутся из
защищённого файла на VPS; дублировать их во всех workflow не требуется.

При релизе `deploy.sh` проверяет зависимости, заранее тянет новые digest,
останавливает bot, обновляет service, затем bot. При health failure возвращает
предыдущие ЛОКАЛЬНЫЕ image IDs. Код/образы старого релиза не чистить автоматически.
Liquibase-миграции таким откатом не отменяются: использовать expand/contract и
отдельный backup перед несовместимыми изменениями. После восстановления новых
записей простой запуск старой БД приведёт к потере этих записей.

Старые workflow с `latest`, общим `compose stop` и `docker system prune -a`
удалены. Все ops-скрипты используют общий `flock`, чтобы deploy/backup/restore
не пересекались. Ошибка lock — причина дождаться операции, а не удалять lock-файл.

## Что проверить перед удалением старого VPS

- После перезагрузки нового VPS все нужные контейнеры возвращаются в healthy.
- Базы не доступны с внешнего адреса; GraphQL без credentials возвращает 401.
- После recreate контейнеров остаются история monitoring и прикладные данные.
- Выполнен отдельный restore в тестовую среду и измерен RTO.
- Backups отправляются offsite, есть контроль свежести и проверенный канал alerts.
- Старые и новые данные не расходятся; окно отката и его способ определены.

Не выполнять `docker compose down -v` на production для обычного обновления.

## Регулярные локальные копии

Пример systemd timer в `infra/systemd` запускает `backup.sh` ежедневно около
03:30 по Москве. Во время копирования bot/service и Redis кратковременно
останавливаются; используется общий с deploy lock. Установить при необходимости:

```bash
sudo install -m 644 infra/systemd/empioner-backup.{service,timer} /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now empioner-backup.timer
systemctl list-timers empioner-backup.timer
```

Копии сохраняются в `/var/lib/empioner/backups`, автоматического удаления нет.
Контролировать свободное место, результаты и восстановление; настроить отдельную
защищённую offsite-копию. Этот timer сам по себе не защищает от потери VPS.
