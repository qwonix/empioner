package ru.qwonix.empioner.telegram.bot.telegram.handler;

import org.junit.jupiter.api.Test;
import ru.qwonix.empioner.telegram.bot.api.EpisodeApi;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserRole;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCommandsTest {
    final EpisodeApi episodes = mock(EpisodeApi.class);
    final ChatCommandHandler commands = new ChatCommandHandler(null, episodes, null, null, null);
    final String[] args = {"00000000-0000-0000-0000-000000000001"};

    @Test
    void ordinaryUserCannotChangeAvailability() {
        var user = new TelegramBotUser(new TelegramBotUserId(123L));
        assertThrows(SecurityException.class, () -> commands.open(user, args));
        assertThrows(SecurityException.class, () -> commands.close(user, args));
        verifyNoInteractions(episodes);
    }

    @Test
    void administratorRetainsBothCommands() {
        var admin = new TelegramBotUser(new TelegramBotUserId(123L), Set.of(UserRole.ADMIN));
        commands.open(admin, args);
        commands.close(admin, args);
        verify(episodes).makeAvailable(any());
        verify(episodes).makeNotAvailable(any());
    }
}
