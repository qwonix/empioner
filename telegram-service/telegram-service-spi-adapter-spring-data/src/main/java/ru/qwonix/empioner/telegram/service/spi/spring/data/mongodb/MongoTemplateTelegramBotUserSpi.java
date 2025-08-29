package ru.qwonix.empioner.telegram.service.spi.spring.data.mongodb;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.entity.*;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.service.spi.TelegramBotUserSpi;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MongoTemplateTelegramBotUserSpi implements TelegramBotUserSpi {

    private static final String USER_COLLECTION = "user";
    private static final String ACTIVITY_COLLECTION = "activity";

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return mongoTemplate.query(TelegramBotUserDetails.class)
                .inCollection(USER_COLLECTION)
                .matching(query(where("id").is(id)))
                .one()
                .map(TelegramBotUserDetails::id)
                .map(TelegramBotUser::new);
    }

    @Override
    public TelegramBotUser registerNewUser(TelegramBotUserDetails user) {
        mongoTemplate.insert(TelegramBotUserDetails.class)
                .inCollection(USER_COLLECTION)
                .one(user);
        return new TelegramBotUser(user.id());
    }

    @Override
    public void addActivity(TelegramBotUserId id) {
        mongoTemplate.insert(Activity.class)
                .inCollection(ACTIVITY_COLLECTION)
                .one(new Activity(id.value(), Instant.now()));
    }

    @Override
    public void updateStatus(TelegramBotUserId id, UserStatus status) {
        UpdateResult first = mongoTemplate.update(TelegramBotUserDetails.class)
                .inCollection(USER_COLLECTION)
                .matching(query(where("id").is(id)))
                .apply(update("status", status))
                .first();
        if (!first.wasAcknowledged()) {
            log.error("User with id {} not found, can't update status", id);
        }
    }

    @Override
    public void makeAdmin(TelegramBotUserId id) {
        UpdateResult result = mongoTemplate.update(TelegramBotUserDetails.class)
                .inCollection(USER_COLLECTION)
                .matching(query(where("id").is(id)))
                .apply(new Update().addToSet("role", UserRole.ADMIN))
                .first();
        if (!result.wasAcknowledged()) {
            log.error("User with id {} not found, can't make admin", id);
        }
    }
}
