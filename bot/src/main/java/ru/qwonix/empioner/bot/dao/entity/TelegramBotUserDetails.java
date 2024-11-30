package ru.qwonix.empioner.bot.dao.entity;

import ru.qwonix.empioner.bot.entity.UserStatus;

public record TelegramBotUserDetails(Long id,
                                     String firstName,
                                     String lastName,
                                     String username,
                                     String languageCode,
                                     UserStatus status) {
}
