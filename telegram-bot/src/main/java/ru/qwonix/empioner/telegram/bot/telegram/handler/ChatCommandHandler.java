package ru.qwonix.empioner.telegram.bot.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.telegram.bot.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.bot.service.BotSettingsService;
import ru.qwonix.empioner.telegram.bot.service.EpisodeService;
import ru.qwonix.empioner.telegram.bot.service.MessageService;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.ShowCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.ShowShowCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.config.BotService;
import ru.qwonix.empioner.telegram.bot.telegram.config.ChatCommand;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;
import ru.qwonix.empioner.telegram.bot.telegram.utils.ButtonOrientation;
import ru.qwonix.empioner.telegram.bot.telegram.utils.Utils;

import java.util.List;
import java.util.UUID;

import static ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService.escapeMarkdownMessage;

@Slf4j
@RequiredArgsConstructor
@BotService
public class ChatCommandHandler {

    public static final int FIRST_PAGE = 0;

    private final BotSettingsService botSettingsService;
    private final EpisodeService episodeService;
    private final MessageService messageService;
    private final TelegramClient bot;
    private final TelegramProperties telegramProperties;

    @ChatCommand("/start")
    public void start(TelegramBotUser user, String[] args) {
        List<InlineKeyboardButton> buttons;
        if (botSettingsService.isEnabledSingleShowMode()) {
            buttons = List.of(
                    Utils.createSwitchInlineQueryButton("Поиск (в разработке)"),
                    Utils.createCallbackDataButton(new ShowCallbackData(botSettingsService.getShowOfSingleShowMode()), "К шоу")
            );
        } else {
            buttons = List.of(
                    Utils.createSwitchInlineQueryButton("Поиск (в разработке)"),
                    Utils.createCallbackDataButton(new ShowShowCallbackData(FIRST_PAGE), "Все шоу")
            );
        }
        InlineKeyboardMarkup keyboard = Utils.createOneRowCallbackKeyboard(buttons, ButtonOrientation.HORIZONTAL);

        SendMessage message = SendMessage.builder()
                .text(escapeMarkdownMessage(telegramProperties.description()))
                .parseMode(ParseMode.MARKDOWNV2)
                .replyMarkup(keyboard)
                .chatId(user.id().value()).build();

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        messageService.deleteMessageId(user);
    }

    @ChatCommand("/open")
    public void open(TelegramBotUser user, String[] args) {
        if (args.length != 1) {
            return;
        }
        try {
            episodeService.makeAvailable(new EpisodeId(UUID.fromString(args[0])));
        } catch (IllegalArgumentException e) {
        }
    }

    @ChatCommand("/close")
    public void close(TelegramBotUser user, String[] args) {
        if (args.length != 1) {
            return;
        }
        try {
            episodeService.makeNotAvailable(new EpisodeId(UUID.fromString(args[0])));
        } catch (IllegalArgumentException e) {
        }
    }
}
