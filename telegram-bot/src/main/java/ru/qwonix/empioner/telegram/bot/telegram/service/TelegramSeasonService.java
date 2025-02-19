package ru.qwonix.empioner.telegram.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.bot.service.EpisodeService;
import ru.qwonix.empioner.telegram.bot.service.SeasonService;
import ru.qwonix.empioner.telegram.bot.service.SeriesService;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.DeleteMessageCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.EpisodeCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.SeasonCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.SeriesCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;
import ru.qwonix.empioner.telegram.bot.telegram.utils.Utils;

import java.util.List;
import java.util.Optional;

import static ru.qwonix.empioner.telegram.bot.telegram.handler.ChatCommandHandler.FIRST_PAGE;

@Service
@RequiredArgsConstructor
public class TelegramSeasonService {
    private final SeriesService seriesService;
    private final SeasonService seasonService;
    private final EpisodeService episodeService;
    private final TelegramProperties telegramProperties;

    public String createText(Season season) {
        Optional<Series> optionalSeries = seriesService.findById(season.seriesId());
        if (optionalSeries.isEmpty()) {
            return "";
        }
        Series series = optionalSeries.get();
        int episodesCount = episodeService.countAllBySeasonId(season.id());
        int availableEpisodesCount = episodeService.countAllAvailableBySeasonId(season.id());

        return """
                *%s –* `%s сезон`
                                
                _%s_
                                
                *Количество эпизодов*: `%d` / *%s*""".formatted(series.title(),
                season.number(),
                season.description(),
                availableEpisodesCount,
                episodesCount);
    }


    public InlineKeyboardMarkup createKeyboard(Season season, final int page) {
        final int totalEpisodesCountInSeason = episodeService.countAllBySeasonId(season.id());
        final int keyboardButtonsLimit = telegramProperties.keyboardButtonsMax();
        final int pagesCount = (int) Math.ceil(totalEpisodesCountInSeason / (double) keyboardButtonsLimit);

        List<Utils.Button> buttons =
                episodeService.findAllBySeasonIdOrderByNumberWithLimitAndPage(season.id(), keyboardButtonsLimit, page)
                        .stream().map(episode ->
                                new Utils.Button(season.number() + "×" + episode.number() + " «" + episode.title() + "»",
                                        new EpisodeCallbackData(episode.id())))
                        .toList();

        List<InlineKeyboardRow> keyboard = Utils.createControlButtons(
                buttons,
                new SeasonCallbackData(season.id(), page - 1),
                new SeasonCallbackData(season.id(), page + 1),
                page,
                pagesCount
        );

        keyboard.add(new InlineKeyboardRow(
                Utils.createCallbackDataButton(new SeriesCallbackData(season.seriesId(), FIRST_PAGE), "Назад"),
                Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "На главную")));

        return new InlineKeyboardMarkup(keyboard);
    }


    public Optional<Season> findById(SeasonId seasonId) {
        return seasonService.findById(seasonId);
    }

    public List<Season> findAll(Series series) {
        return seasonService.findAllBySeriesIdOrderByNumberWithLimitAndPage(series.id(), 10, 0);
    }
}
