package ru.qwonix.empioner.service.entity.id;

import java.util.UUID;

public record ImageId(UUID value) implements Id<UUID> {
}
