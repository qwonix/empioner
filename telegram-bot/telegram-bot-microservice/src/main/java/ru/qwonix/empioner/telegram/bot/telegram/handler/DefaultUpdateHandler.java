package ru.qwonix.empioner.telegram.bot.telegram.handler;

import lombok.RequiredArgsConstructor;
import ru.qwonix.empioner.telegram.bot.config.UpdateProcessingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import ru.qwonix.empioner.telegram.bot.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.bot.api.VideoApi;
import ru.qwonix.empioner.telegram.bot.telegram.config.ChatCommandAnnotationBeanPostProcessor;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.EpisodeVideoReplacementService;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultUpdateHandler implements UpdateHandler {

    private final ChatCommandAnnotationBeanPostProcessor chatCommandAnnotationBeanPostProcessor;
    private final TelegramBotUserApi telegramBotUserApi;
    private final CallbackHandler callbackHandler;
    private final VideoApi videoApi;
    private final TelegramBotExecutionService executionService;
    private final EpisodeVideoReplacementService replacementService;

    @Override
    public void onCallback(Update update, TelegramBotUser user) {
        try {
            callbackHandler.onCallback(update, user);
            log.atDebug().log("Callback handled successfully");
        } catch (Exception e) {
            UpdateProcessingContext.failed();
            log.atError()
                    .setCause(e)
                    .log("Error while handling callback");
        }
    }

    @Override
    public void onPhoto(Update update, TelegramBotUser user) {
        log.atInfo().log("Received photo message");

        try {
            log.atWarn()
                    .log("Photo handling not implemented yet");
        } catch (Exception e) {
            UpdateProcessingContext.failed();
            log.atError()
                    .setCause(e)
                    .log("Error while processing photo message");
        }
    }

    @Override
    public void onText(Update update, TelegramBotUser user) {
        String text = update.getMessage().getText();

        log.atInfo()
                .log("Received text message");

        try {
            if (text.startsWith("/")) {
                String[] allArgs = text.split(" ");
                String command = allArgs[0].toLowerCase();
                String[] commandArgs = Arrays.copyOfRange(allArgs, 1, allArgs.length);

                log.atDebug()
                        .addKeyValue("command", command)
                        .addKeyValue("argsCount", commandArgs.length)
                        .log("Processing chat command");

                chatCommandAnnotationBeanPostProcessor.handle(user, command, commandArgs);

                log.atInfo()
                        .addKeyValue("command", command)
                        .log("Command handled successfully");
            } else {
                log.atDebug().log("Text message ignored (not a command)");
            }
        } catch (Exception e) {
            UpdateProcessingContext.failed();
            log.atError()
                    .setCause(e)
                    .log("Error while processing text message");
        }
    }

    @Override
    public void onUserStatusChanged(Update update, TelegramBotUser user) {
        ChatMemberUpdated memberUpdate = update.getMyChatMember();
        String newStatus = memberUpdate.getNewChatMember().getStatus();

        log.atInfo()
                .addKeyValue("newStatus", newStatus)
                .log("User status changed");

        try {
            switch (newStatus) {
                case "kicked" -> {
                    telegramBotUserApi.setStatus(user.id(), UserStatus.KICKED);
                    log.atInfo()
                            .log("User marked as KICKED");
                }
                case "member" -> {
                    telegramBotUserApi.setStatus(user.id(), UserStatus.MEMBER);
                    log.atInfo()
                            .log("User marked as MEMBER");
                }
                default -> log.atWarn()
                        .addKeyValue("status", newStatus)
                        .log("Unknown user status received");
            }
        } catch (Exception e) {
            UpdateProcessingContext.failed();
            log.atError()
                    .setCause(e)
                    .addKeyValue("status", newStatus)
                    .log("Error while updating user status");
        }
    }

    @Override
    public void onVideo(Update update, TelegramBotUser user) {
        Video video = update.getMessage().getVideo();
        String replacementStatus;
        try {
            replacementStatus = replacementService.replace(update.getMessage(), user);
        } catch (Exception e) {
            UpdateProcessingContext.failed();
            log.error("Failed to replace episode video", e);
            replacementStatus = "Не удалось обновить видео. Проверьте данные и повторите попытку.";
        }
        executionService.replyWithVideoFileIds(update.getMessage(), user, replacementStatus);

        log.atInfo()
                .log("Received video message");

        try {
            videoApi.updateTelegramFileIdByTelegramFileUniqueId(
                    new TelegramFileUniqueId(video.getFileUniqueId()),
                    new TelegramFileId(video.getFileId())
            );

            log.atInfo()
                    .log("Video file info updated successfully");
        } catch (Exception e) {
            UpdateProcessingContext.failed();
            log.atError()
                    .setCause(e)
                    .log("Error while updating video file info");
        }
    }
}
