package ru.qwonix.empioner.telegram.service.metrics;

import com.mongodb.client.MongoClients;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserStatisticsTest {
    @Container
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.25");

    @Test
    void combinesCurrentAndArchivedUsersWithoutCountingOverlapTwice() {
        MongoTemplate template = new MongoTemplate(MongoClients.create(mongo.getReplicaSetUrl()), "user_metrics");
        template.getCollection("user").insertMany(List.of(new Document("_id", 1L), new Document("_id", 2L)));
        template.getCollection("analytics_historical_users").insertMany(List.of(new Document("_id", 1L), new Document("_id", 3L)));
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        UserStatistics statistics = new UserStatistics(template, registry);
        assertTrue(Double.isNaN(registry.get("empioner.users.snapshot").tag("source", "combined_unique").gauge().value()));

        statistics.refresh();

        assertEquals(2, registry.get("empioner.users.snapshot").tag("source", "current").gauge().value());
        assertEquals(2, registry.get("empioner.users.snapshot").tag("source", "historical").gauge().value());
        assertEquals(3, registry.get("empioner.users.snapshot").tag("source", "combined_unique").gauge().value());
        assertTrue(registry.get("empioner.users.snapshot.updated.timestamp.seconds").gauge().value() > 0);
    }
}
