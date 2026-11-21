package ru.qwonix.empioner.telegram.service.metrics;

import com.mongodb.client.MongoCollection;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Aggregate identifiers on a schedule; neither identifiers nor profile fields are exposed as metrics. */
@Component
public class UserStatistics {
    private static final Logger log = LoggerFactory.getLogger(UserStatistics.class);
    private static final String ARCHIVE = "analytics_historical_users";
    private final MongoTemplate mongo;
    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>(new Snapshot(Double.NaN, Double.NaN, Double.NaN, 0));

    public UserStatistics(MongoTemplate mongo, MeterRegistry registry) {
        this.mongo = mongo;
        Gauge.builder("empioner.users.snapshot", snapshot, value -> value.get().current()).tag("source", "current").register(registry);
        Gauge.builder("empioner.users.snapshot", snapshot, value -> value.get().historical()).tag("source", "historical").register(registry);
        Gauge.builder("empioner.users.snapshot", snapshot, value -> value.get().unique()).tag("source", "combined_unique").register(registry);
        Gauge.builder("empioner.users.snapshot.updated.timestamp.seconds", snapshot, value -> value.get().timestamp()).register(registry);
    }

    @Scheduled(initialDelayString = "${empioner.user-statistics.initial-delay-ms:60000}", fixedDelayString = "${empioner.user-statistics.interval-ms:900000}")
    public void refresh() {
        try {
            if (!mongo.collectionExists(ARCHIVE)) {
                log.warn("Historical user statistics collection is missing; skipping refresh");
                return;
            }
            // A single aggregation prevents concurrent registrations from being counted twice.
            List<Document> archivePipeline = List.of(project(0, 1));
            List<Document> pipeline = List.of(
                    project(1, 0),
                    new Document("$unionWith", new Document("coll", ARCHIVE).append("pipeline", archivePipeline)),
                    new Document("$group", new Document("_id", "$_id")
                            .append("current", new Document("$max", "$current"))
                            .append("historical", new Document("$max", "$historical"))),
                    new Document("$group", new Document("_id", null)
                            .append("current", new Document("$sum", "$current"))
                            .append("historical", new Document("$sum", "$historical"))
                            .append("unique", new Document("$sum", 1)))
            );
            MongoCollection<Document> users = mongo.getDb().getCollection("user");
            Document result = users.aggregate(pipeline).maxTime(30, TimeUnit.SECONDS).first();
            if (result == null) result = new Document("current", 0).append("historical", 0).append("unique", 0);
            snapshot.set(new Snapshot(((Number) result.get("current")).doubleValue(),
                    ((Number) result.get("historical")).doubleValue(), ((Number) result.get("unique")).doubleValue(),
                    Instant.now().getEpochSecond()));
        } catch (Exception error) {
            log.warn("Could not refresh aggregate user statistics: {}", error.getClass().getSimpleName());
        }
    }

    private static Document project(int current, int historical) {
        return new Document("$project", new Document("_id", 1)
                .append("current", new Document("$literal", current))
                .append("historical", new Document("$literal", historical)));
    }

    private record Snapshot(double current, double historical, double unique, double timestamp) { }
}
