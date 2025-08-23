package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.entity.VideoDetails;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.spi.VideoSpi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JdbcClientVideoSpi implements VideoSpi {

    private final JdbcClient jdbcClient;
    private final RowMapper<Video> mapper;

    @Override
    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return jdbcClient.sql("""
                        select v.*
                        from video v
                                 join public.video_group vg on vg.id = v.video_group_id
                        where vg.id = :videoGroupId
                        order by priority DESC
                            fetch first 1 rows only""")
                .param("videoGroupId", videoGroupId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public Optional<Video> findById(VideoId videoId) {
        return jdbcClient.sql("select * from video where id = :id")
                .param("id", videoId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public void updateTelegramFileIdByTelegramFileUniqueId(
            TelegramFileUniqueId telegramFileUniqueId,
            TelegramFileId telegramFileId) {
        jdbcClient.sql("""
                        update video set telegram_file_id = :telegramFileId
                        where telegram_file_unique_id = :telegramFileUniqueId
                        """)
                .param("telegramFileId", telegramFileId.value())
                .param("telegramFileUniqueId", telegramFileUniqueId.value())
                .update();
    }

    @Override
    public List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId) {
        return jdbcClient.sql("select * from video where video_group_id = :videoGroupId")
                .param("videoGroupId", videoGroupId.value())
                .query(mapper)
                .list();
    }

    @Override
    public VideoGroupId createVideoGroup() {
        return jdbcClient.sql("""
                        insert into video_group values (default)
                        RETURNING id
                        """)
                .query((rs, rowNum) ->
                        new VideoGroupId(UUID.fromString(rs.getString("id"))))
                .single();
    }


    @Override
    public VideoId createVideo(VideoGroupId videoGroupId, Video video) {
        return jdbcClient.sql("""
                        INSERT INTO public.video (
                            video_group_id,
                            description,
                            telegram_file_id,
                            telegram_file_unique_id,
                            priority,
                            is_available
                        ) 
                        VALUES (
                            :videoGroupId,
                            :description,
                            :telegramFileId,
                            :telegramFileUniqueId,
                            :priority,
                            :isAvailable
                        )
                        RETURNING id
                        """)
                .param("videoGroupId", videoGroupId.value())
                .param("description", video.description())
                .param("telegramFileId", video.telegramFileId().value())
                .param("telegramFileUniqueId", video.telegramFileUniqueId().value())
                .param("priority", video.priority())
                .param("isAvailable", video.isAvailable())
                .query((rs, rowNum) ->
                        new VideoId(UUID.fromString(rs.getString("id"))))
                .single();
    }

    @Override
    public void createVideoDetails(VideoDetails videoDetails) {
        jdbcClient.sql("""
                        INSERT INTO public.video_details (
                            video_id,
                            width,
                            height,
                            duration,
                            mime_type,
                            file_size,
                            file_name
                        ) VALUES (
                            :videoId,
                            :width,
                            :height,
                            :duration,
                            :mimeType,
                            :fileSize,
                            :fileName
                        )
                        """)
                .param("videoId", videoDetails.videoId().value())
                .param("width", videoDetails.width())
                .param("height", videoDetails.height())
                .param("duration", videoDetails.duration())
                .param("mimeType", videoDetails.mimeType())
                .param("fileSize", videoDetails.fileSize())
                .param("fileName", videoDetails.fileName())
                .update();
    }
}
