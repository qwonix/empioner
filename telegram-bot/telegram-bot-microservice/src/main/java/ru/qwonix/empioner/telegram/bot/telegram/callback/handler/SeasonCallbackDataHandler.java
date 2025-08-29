package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.SeasonCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramEpisodeService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramSeasonService;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class SeasonCallbackDataHandler implements CallbackDataHandler {

    private final TelegramSeasonService telegramSeasonService;
    private final TelegramEpisodeService telegramEpisodeService;
    private final TelegramBotExecutionService telegramBotExecutionService;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return SeasonCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        SeasonCallbackData seasonCallbackData = (SeasonCallbackData) callbackData;
        Optional<Season> optionalSeason = telegramSeasonService.findById(seasonCallbackData.seasonId());

        if (optionalSeason.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Такого сезона не существует. Попробуйте найти его заново.",
                    true
            );
            return;
        }

        Season season = optionalSeason.get();
        List<Episode> episodes = telegramEpisodeService.findAll(season.id());
        if (episodes.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Ни одного эпизода не найдено. Попробуйте заново немного позже.", true
            );
            return;
        }

        InlineKeyboardMarkup keyboard = telegramSeasonService.createKeyboard(season, seasonCallbackData.page());
        String text = telegramSeasonService.createText(season);

        telegramBotExecutionService.send(user, text, keyboard, season.imageId());
    }
}
