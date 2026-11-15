package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import java.util.Set;

public record TelegramBotUser(TelegramBotUserId id, Set<UserRole> roles) {
    public TelegramBotUser {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public TelegramBotUser(TelegramBotUserId id) {
        this(id, Set.of());
    }

    public boolean isAdmin() {
        return roles.contains(UserRole.ADMIN);
    }
}
