package ru.qwonix.empioner.telegram.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "bot.token=123456:fake-test-token",
        "bot.username=test_bot",
        "bot.config.image.placeholder=00000000-0000-0000-0000-000000000001",
        "bot.config.show.show.image_id=00000000-0000-0000-0000-000000000002",
        "bot.config.show.series.image_id=00000000-0000-0000-0000-000000000003",
        "bot.config.show.movie.image_id=00000000-0000-0000-0000-000000000004"
})
class EmpionerBotApplicationTests {

    // Keep the real application and GraphQL auto-configuration, without contacting Telegram.
    @MockitoBean
    TelegramBotInitializer telegramBotInitializer;

    @Autowired
    GraphQlSource graphQlSource;

    @Autowired
    ObjectMapper json;

    @Test
    void contextLoadsWithLongScalar() {
        assertNotNull(graphQlSource.schema().getType("Long"));
    }

    @Test
    void deserializesRolesAndTreatsMissingRolesAsNonAdmin() throws Exception {
        var admin = json.readValue("{\"id\":\"123456\",\"roles\":[\"ADMIN\"]}", TelegramBotUser.class);
        var legacy = json.readValue("{\"id\":\"123456\"}", TelegramBotUser.class);
        assertTrue(admin.isAdmin());
        assertFalse(legacy.isAdmin());
    }
}
