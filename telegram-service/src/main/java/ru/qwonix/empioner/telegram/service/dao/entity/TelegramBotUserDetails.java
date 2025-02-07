package ru.qwonix.empioner.telegram.service.dao.entity;

import ru.qwonix.empioner.telegram.service.entity.UserStatus;

public record TelegramBotUserDetails(Long id,
                                     String firstName,
                                     String lastName,
                                     String username,
                                     String languageCode,
                                     UserStatus status) {
}
