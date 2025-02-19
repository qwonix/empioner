package ru.qwonix.empioner.telegram.service.dao.impl;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.entity.Activity;
import ru.qwonix.empioner.telegram.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.service.dao.TelegramBotUserDao;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TelegramBotUserDaoMongo implements TelegramBotUserDao {

    private static final String USER_COLLECTION = "user";
    private static final String ACTIVITY_COLLECTION = "activity";

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return mongoTemplate.query(TelegramBotUserDetails.class)
                .inCollection(USER_COLLECTION)
                .matching(query(where("id").is(id.value())))
                .one()
                .map(TelegramBotUserDetails::id)
                .map(TelegramBotUserId::new)
                .map(TelegramBotUser::new);
    }

    @Override
    public TelegramBotUser registerNewUser(TelegramBotUserDetails user) {
        mongoTemplate.insert(TelegramBotUserDetails.class)
                .inCollection(USER_COLLECTION)
                .one(user);
        return new TelegramBotUser(new TelegramBotUserId(user.id()));
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
                .matching(query(where("id").is(id.value())))
                .apply(update("status", status))
                .first();
        if (!first.wasAcknowledged()) {
            log.error("User with id {} not found, can't update status", id);
        }
    }
}
