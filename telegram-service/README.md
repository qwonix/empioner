# Создание серии через GraphQL

Мутация создаёт недоступную серию и пустую группу видео. Для `description`
можно передать пустую строку. Номер должен быть положительным и уникальным
внутри сезона, включая недоступные серии.

```graphql
mutation CreateEpisode($input: CreateEpisodeInput!) {
  createEpisode(input: $input) {
    id
    seasonId
    title
    description
    number
    videoGroupId
    isAvailable
  }
}
```

Пример variables:

```json
{
  "input": {
    "seasonId": "00000000-0000-0000-0000-000000000001",
    "title": "Серия 1",
    "description": "Описание",
    "number": 1
  }
}
```

При неизвестном сезоне, занятом номере или некорректных данных мутация
возвращает ошибку GraphQL. Группа, серия и проверка сохраняются в одной
транзакции; для конкурентных запросов создания сезон блокируется.

Новая серия не подключается к цепочке предыдущей и следующей серий;
публикация и распубликация тоже не изменяют эти связи.

# Видео эпизода

`addVideoToEpisode` добавляет новый вариант в существующую группу эпизода;
вызов можно повторять для разных качеств. Доступны и недоступные серии.

```graphql
mutation AddVideo($episodeId: EpisodeId!, $input: CreateEpisodeVideoInput!) {
  addVideoToEpisode(episodeId: $episodeId, input: $input) {
    id videoGroupId description telegramFileId telegramFileUniqueId priority isAvailable
  }
}
```

```json
{
  "episodeId": "00000000-0000-0000-0000-000000000001",
  "input": {
    "description": "1080p",
    "telegramFileId": "BAAC...",
    "telegramFileUniqueId": "AgAD...",
    "priority": 2,
    "isAvailable": true,
    "details": {
      "width": 1920, "height": 1080, "duration": 1500,
      "mimeType": "video/mp4", "fileSize": 3000000000, "fileName": "episode.mp4"
    }
  }
}
```

`details` необязателен; если передан, необходимы все его поля. Размер файла
в байтах — 64-битное число, длительность в секундах. Оба Telegram ID должны
быть непустыми, не более 100 символов (ограничение текущей схемы БД).
`description` — непустая подпись качества для кнопки в боте. Приоритет — целое
число: более высокий выбирается первым как основной вариант.

Изменить отдельные свойства конкретного видео можно с помощью `updateVideo`:

```graphql
mutation UpdateVideo($id: VideoId!, $input: UpdateVideoInput!) {
  updateVideo(id: $id, input: $input) { id videoGroupId description priority isAvailable }
}
```

Пример variables: `{ "id": "UUID_ВИДЕО", "input": { "description": "4K", "priority": 3 } }`.
Необходимо передать хотя бы одно поле; отсутствующие или null-поля не меняются.
Если передать `details`, весь набор метаданных добавляется или заменяется.
Удаление метаданных и перенос видео в другую группу этой мутацией не поддерживаются.

`videoDetailsById(id: VideoId!)` возвращает метаданные или null, если они не
задавались. Существующий запрос `videosByVideoGroupId` возвращает все варианты
для управления, в том числе скрытые. Бот показывает только доступные варианты,
а `maxPriorityVideo` выбирает доступное видео с наибольшим приоритетом.

Добавление видео не публикует эпизод и не устанавливает ссылки на соседние серии.

# Публикация и распубликация

```graphql
mutation Publish($id: EpisodeId!) { publishEpisode(id: $id) }
```

```graphql
mutation Unpublish($id: EpisodeId!) { unpublishEpisode(id: $id) }
```

Variables: `{ "id": "UUID_ЭПИЗОДА" }`. Операции возвращают `true`, если
эпизод существует (в том числе при повторном вызове), и `false`, если нет.
При публикации `isAvailable` становится `true`, при распубликации — `false`.
Доступные серии видны через `episodeById` и `episodesBySeasonId`; недоступные
исключаются. Наличие видео не проверяется. Предыдущая и следующая серии не
изменяются; логика связей будет определена отдельно.
Существующая мутация `changeAvailable` также остаётся доступной.
