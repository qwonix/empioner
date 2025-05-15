package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Set;

public record TelegramBotUserDetails(TelegramBotUserId id,
                                     String firstName,
                                     String lastName,
                                     String username,
                                     String languageCode,
                                     UserStatus status,
                                     Set<UserRole> roles) {
}
