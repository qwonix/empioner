package ru.qwonix.empioner.telegram.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.qwonix.empioner.telegram.bot.api.*;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EpisodeVideoReplacementService {
    private static final int PAGE_SIZE = 100;
    private static final Pattern CODE = Pattern.compile("(?<![0-9xX])([1-9][0-9]{2})(?![0-9pP]|\\s+[pP](?![a-zA-Z]))");
    private final ShowApi shows;
    private final SeriesApi series;
    private final SeasonApi seasons;
    private final EpisodeApi episodes;
    private final VideoApi videos;

    public String replace(Message message, TelegramBotUser user) {
        if (user == null || !user.isAdmin() || !Objects.equals(message.getChatId(), user.id().value())) return "";
        var video = message.getVideo();
        var name = video.getFileName();
        if (name == null || name.isBlank()) return "Видео не обновлено: Telegram не передал имя файла.";
        var matcher = CODE.matcher(name);
        if (!matcher.find()) return "Видео не обновлено: в имени не найден код серии.";
        var code = matcher.group(1);
        int seasonNumber = code.charAt(0) - '0';
        int episodeNumber = Integer.parseInt(code.substring(1));
        if (episodeNumber == 0) return "Видео не обновлено: номер серии должен быть больше нуля.";
        Integer height = video.getHeight();
        String quality;
        if (Objects.equals(height, 1080)) quality = "Full HD";
        else if (Objects.equals(height, 480)) quality = "SD";
        else return "Видео не обновлено: поддерживаются высоты 1080 и 480 пикселей.";
        if (video.getWidth() == null || video.getWidth() <= 0 || video.getDuration() == null
                || video.getDuration() <= 0 || video.getMimeType() == null || video.getMimeType().isBlank()
                || video.getFileSize() == null || video.getFileSize().longValue() <= 0
                || video.getFileId() == null || video.getFileUniqueId() == null) {
            return "Видео не обновлено: Telegram передал не все метаданные файла.";
        }
        List<Episode> matches = new ArrayList<>();
        for (int sp = 0; ; sp++) {
            var showPage = shows.findAllOrderByNumberWithLimitAndPage(PAGE_SIZE, sp);
            for (var show : showPage) {
                for (var item : series.findAllByShowId(show.id())) {
                    int seasonCount = seasons.countAllBySeries(item.id());
                    for (int p = 0; p * PAGE_SIZE < seasonCount; p++) {
                        for (var season : seasons.findAllBySeriesIdOrderByNumberWithLimitAndPage(item.id(), PAGE_SIZE, p)) {
                            if (!Objects.equals(season.number(), seasonNumber)) continue;
                            int episodeCount = episodes.countAllBySeasonId(season.id());
                            for (int ep = 0; ep * PAGE_SIZE < episodeCount; ep++) {
                                episodes.findAllBySeasonIdOrderByNumberWithLimitAndPage(season.id(), PAGE_SIZE, ep)
                                        .stream().filter(e -> Objects.equals(e.number(), episodeNumber)).forEach(matches::add);
                            }
                        }
                    }
                }
            }
            if (showPage.size() < PAGE_SIZE) break;
        }
        if (matches.size() != 1) return "Видео не обновлено: по коду " + code + " подходящих серий найдено " + matches.size() + ".";
        var episode = matches.getFirst();
        List<Video> existingVideos = videos.findAllByVideoGroupId(episode.videoGroupId());
        List<Video> candidates = existingVideos.stream()
                .filter(v -> quality.equals(normalizeQuality(v.description()))).toList();
        if (candidates.isEmpty()) {
            Integer priority = priorityForNewQuality(quality, existingVideos);
            if (priority == null) return "Видео не добавлено: не удалось определить приоритет качества.";
            videos.addVideoToEpisode(episode.id(), quality, priority, video);
            return "Добавлено: сезон " + seasonNumber + ", серия " + episodeNumber + ", " + quality + ".";
        }
        if (candidates.size() != 1) return "Видео не обновлено: вариантов качества " + quality + " найдено " + candidates.size() + ".";
        videos.replaceVideo(candidates.getFirst().id(), quality, video);
        return "Обновлено: сезон " + seasonNumber + ", серия " + episodeNumber + ", " + quality + ".";
    }

    private Integer priorityForNewQuality(String quality, List<Video> existing) {
        var available = existing.stream().filter(v -> Boolean.TRUE.equals(v.isAvailable()));
        if (quality.equals("SD")) {
            var priorities = available.map(Video::priority).filter(Objects::nonNull).mapToInt(Integer::intValue);
            var max = priorities.max();
            if (max.isEmpty()) return 2;
            return max.getAsInt() == Integer.MAX_VALUE ? null : max.getAsInt() + 1;
        }
        var priorities = available.filter(v -> "SD".equals(normalizeQuality(v.description())))
                .map(Video::priority).filter(Objects::nonNull).mapToInt(Integer::intValue);
        var min = priorities.min();
        if (min.isEmpty()) return 1;
        return min.getAsInt() == Integer.MIN_VALUE ? null : min.getAsInt() - 1;
    }

    public static String normalizeQuality(String label) {
        if (label == null) return "";
        String value = label.trim().replace(" ", "").toLowerCase(java.util.Locale.ROOT);
        return switch (value) {
            case "fullhd", "1080p" -> "Full HD";
            case "sd", "480p" -> "SD";
            default -> label;
        };
    }
}
