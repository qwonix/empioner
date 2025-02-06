package ru.qwonix.empioner.service.entity.id;

import java.util.UUID;

public record MovieId(UUID value) implements Id<UUID> {
}
