package ru.qwonix.empioner.telegram.entity;

import java.util.Set;

public record TelegramBotUserDetails(Long id,
                                     String firstName,
                                     String lastName,
                                     String username,
                                     String languageCode,
                                     UserStatus status,
                                     Set<UserRole> roles) {
}
