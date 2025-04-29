package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserDetailsInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserInput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TelegramBotUserMapper {
    TelegramBotUserInput toInput(TelegramBotUser botUser);

    TelegramBotUserDetails to(TelegramBotUserDetailsInput userDetails);
}
