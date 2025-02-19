package ru.qwonix.empioner.telegram.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.bot.service.EpisodeService;
import ru.qwonix.empioner.telegram.bot.service.SeasonService;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.*;
import ru.qwonix.empioner.telegram.bot.telegram.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.qwonix.empioner.telegram.bot.telegram.handler.ChatCommandHandler.FIRST_PAGE;

@Service
@RequiredArgsConstructor
public class TelegramEpisodeService {

    private final EpisodeService episodeService;
    private final SeasonService seasonService;

    public Optional<Episode> findById(EpisodeId episodeId) {
        return episodeService.findById(episodeId);
    }

    public List<Episode> findAll(SeasonId id) {
        return episodeService.findAllBySeasonIdOrderByNumberWithLimitAndPage(id, 10, 0);
    }

    public String createText(Episode episode) {
        Optional<Season> optionalSeason = seasonService.findById(episode.seasonId());
        if (optionalSeason.isEmpty()) {
            return null;
        }
        Season season = optionalSeason.get();

        return String.format("""
                _%s сезон %s серия_ – `%s`
                                
                _%s_
                                
                """, season.number(), episode.number(), episode.title(), episode.description());
    }

    public InlineKeyboardMarkup createKeyboard(Episode episode, List<Video> episodeVideos) {
        final List<InlineKeyboardRow> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> videoButtons = new ArrayList<>(episodeVideos.stream()
                .map(video -> Utils.createCallbackDataButton(
                        new EpisodeVideoCallbackData(video.id()), video.description())
                ).toList());

        keyboard.add(this.createEpisodeControlButtons(episode));
        keyboard.add(new InlineKeyboardRow(videoButtons));
        keyboard.add(new InlineKeyboardRow(
                Utils.createCallbackDataButton(new SeasonCallbackData(episode.seasonId(), FIRST_PAGE), "Назад"),
                Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "На главную")));

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardRow createEpisodeControlButtons(Episode currentEpisode) {
        EpisodeId previousEpisodeId = currentEpisode.previousEpisodeId();
        EpisodeId nextEpisodeId = currentEpisode.nextEpisodeId();
        Integer totalEpisodesCountInSeason = episodeService.countAllBySeasonId(currentEpisode.seasonId());

        InlineKeyboardButton previous;
        InlineKeyboardButton next;
        if (previousEpisodeId != null) {
            previous = Utils.createCallbackDataButton(new EpisodeCallbackData(previousEpisodeId), "‹");
        } else {
            previous = Utils.createCallbackDataButton(new EmptyCallbackData(), "×");
        }

        if (nextEpisodeId != null) {
            next = Utils.createCallbackDataButton(new EpisodeCallbackData(nextEpisodeId), "›");
        } else {
            next = Utils.createCallbackDataButton(new EmptyCallbackData(), "×");
        }

        InlineKeyboardButton current = Utils.createCallbackDataButton(
                new EmptyCallbackData(),
                currentEpisode.number() + "/" + totalEpisodesCountInSeason
        );
        return new InlineKeyboardRow(previous, current, next);
    }

    public Optional<Episode> findByVideoGroupId(VideoGroupId id) {
        return episodeService.findByVideoGroupId(id);
    }
}
