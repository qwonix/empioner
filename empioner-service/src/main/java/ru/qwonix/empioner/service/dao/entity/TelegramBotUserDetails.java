package ru.qwonix.empioner.service.dao.entity;

import ru.qwonix.empioner.service.entity.UserStatus;

public record TelegramBotUserDetails(Long id,
                                     String firstName,
                                     String lastName,
                                     String username,
                                     String languageCode,
                                     UserStatus status) {
}
