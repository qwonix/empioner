package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.entity.UserRole;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserDetailsInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserInput;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TelegramBotUserMapper {
    TelegramBotUserInput toInput(TelegramBotUser botUser);

    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "admin", ignore = true)
    TelegramBotUserDetails to(TelegramBotUserDetailsInput userDetails, Set<UserRole> roles);
}
