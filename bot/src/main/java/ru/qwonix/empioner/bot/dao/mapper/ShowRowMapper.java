package ru.qwonix.empioner.bot.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.bot.entity.Show;
import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.entity.id.ShowId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class ShowRowMapper implements RowMapper<Show> {
    @Override
    public Show mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String title = rs.getString("title");
        String description = rs.getString("description");
        UUID imageId = rs.getObject("preview_image_id", UUID.class);
        int priority = rs.getInt("priority");
        boolean isAvailable = rs.getBoolean("is_available");

        return new Show(
                new ShowId(id),
                title,
                description,
                new ImageId(imageId),
                priority,
                isAvailable);
    }
}
