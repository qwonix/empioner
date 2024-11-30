package ru.qwonix.empioner.bot.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.bot.entity.Image;
import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.entity.id.TelegramFileId;
import ru.qwonix.empioner.bot.entity.id.TelegramFileUniqueId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class ImageRowMapper implements RowMapper<Image> {
    @Override
    public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID imageId = rs.getObject("id", UUID.class);
        String telegramFileId = rs.getString("telegram_file_id");
        String telegramFileUniqueId = rs.getString("telegram_file_unique_id");

        return new Image(new ImageId(imageId),
                new TelegramFileId(telegramFileId),
                new TelegramFileUniqueId(telegramFileUniqueId));
    }
}
