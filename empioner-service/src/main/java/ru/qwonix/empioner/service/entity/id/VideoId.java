package ru.qwonix.empioner.service.entity.id;

import java.util.UUID;

public record VideoId(UUID value) implements Id<UUID> {
}
