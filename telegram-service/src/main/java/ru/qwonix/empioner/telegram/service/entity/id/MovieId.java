package ru.qwonix.empioner.telegram.service.entity.id;

import java.util.UUID;

public record MovieId(UUID value) implements Id<UUID> {
}
