package ru.qwonix.empioner.telegram.entity;

public record TelegramBotUserDetails(Long id,
                                     String firstName,
                                     String lastName,
                                     String username,
                                     String languageCode,
                                     UserStatus status) {
}
