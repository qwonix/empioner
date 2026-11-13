package ru.qwonix.empioner.telegram.bot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void contextLoadsWithLongScalar() {
        assertNotNull(graphQlSource.schema().getScalarType("Long"));
    }
}
