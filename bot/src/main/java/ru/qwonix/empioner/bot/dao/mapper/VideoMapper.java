package ru.qwonix.empioner.bot.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.bot.entity.Video;
import ru.qwonix.empioner.bot.entity.id.TelegramFileId;
import ru.qwonix.empioner.bot.entity.id.TelegramFileUniqueId;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.bot.entity.id.VideoId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class VideoMapper implements RowMapper<Video> {
    @Override
    public Video mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        UUID videoGroupId = rs.getObject("video_group_id", UUID.class);
        String description = rs.getString("description");
        String telegramFileId = rs.getString("telegram_file_id");
        String telegramFileUniqueId = rs.getString("telegram_file_unique_id");
        int priority = rs.getInt("priority");
        boolean isAvailable = rs.getBoolean("is_available");

        return new Video(new VideoId(id),
                new VideoGroupId(videoGroupId),
                description,
                new TelegramFileId(telegramFileId),
                new TelegramFileUniqueId(telegramFileUniqueId),
                priority,
                isAvailable);
    }
}
