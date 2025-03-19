package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class EpisodeRowMapper implements RowMapper<Episode> {

    @Override
    public Episode mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String title = rs.getString("title");
        String description = rs.getString("description");
        int number = rs.getInt("number");
        UUID previousEpisodeId = rs.getObject("previous_episode_id", UUID.class);
        UUID nextEpisodeId = rs.getObject("next_episode_id", UUID.class);
        UUID seasonId = rs.getObject("season_id", UUID.class);
        UUID imageId = rs.getObject("preview_image_id", UUID.class);
        UUID videoGroupId = rs.getObject("video_group_id", UUID.class);
        boolean isAvailable = rs.getBoolean("is_available");

        return new Episode(
                new EpisodeId(id),
                title,
                description,
                number,
                previousEpisodeId == null ? null : new EpisodeId(previousEpisodeId),
                nextEpisodeId == null ? null : new EpisodeId(nextEpisodeId),
                new VideoGroupId(videoGroupId),
                new SeasonId(seasonId),
                imageId == null ? null : new ImageId(imageId),
                isAvailable
        );
    }
}

