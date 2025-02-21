package ru.qwonix.empioner.telegram.bot.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.bot.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.bot.telegram.config.ChatCommandAnnotationBeanPostProcessor;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultUpdateHandler implements UpdateHandler {

    private final ChatCommandAnnotationBeanPostProcessor chatCommandAnnotationBeanPostProcessor;
    private final TelegramBotUserApi telegramBotUserApi;
    private final CallbackHandler callbackHandler;

    @Override
    public void onCallback(Update update, TelegramBotUser user) {
        callbackHandler.onCallback(update, user);
    }

    @Override
    public void onPhoto(Update update, TelegramBotUser user) {

    }

    @Override
    public void onText(Update update, TelegramBotUser user) {
        String text = update.getMessage().getText();
        log.info("user {} send text {}", user, text);
        if (update.getMessage().getText().startsWith("/")) {
            String[] allArgs = text.split(" ");
            String command = allArgs[0].toLowerCase();
            String[] commandArgs = Arrays.copyOfRange(allArgs, 1, allArgs.length);
            chatCommandAnnotationBeanPostProcessor.handle(user, command, commandArgs);
        }

    }

    @Override
    public void onUserStatusChanged(Update update, TelegramBotUser user) {
        ChatMemberUpdated myChatMember = update.getMyChatMember();
        switch (myChatMember.getNewChatMember().getStatus()) {
            case "kicked" -> telegramBotUserApi.setStatus(user.id(), UserStatus.KICKED);
            case "member" -> telegramBotUserApi.setStatus(user.id(), UserStatus.MEMBER);
        }
    }

    @Override
    public void onVideo(Update update, TelegramBotUser user) {

    }
}
