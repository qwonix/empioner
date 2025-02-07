package ru.qwonix.empioner.telegram.bot.dao.entity;

import ru.qwonix.empioner.telegram.bot.entity.UserStatus;

public record TelegramBotUserDetails(Long id,
                                     String firstName,
                                     String lastName,
                                     String username,
                                     String languageCode,
                                     UserStatus status) {
}
