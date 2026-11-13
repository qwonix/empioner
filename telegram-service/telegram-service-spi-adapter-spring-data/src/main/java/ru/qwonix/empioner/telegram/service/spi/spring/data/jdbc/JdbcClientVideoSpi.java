package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.exception.EpisodeNotFoundException;
import ru.qwonix.empioner.telegram.exception.VideoNotFoundException;
import java.sql.Types;
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
    @Transactional
    public Video createForEpisode(EpisodeId episodeId, Video video, VideoDetails details) {
        UUID group = jdbcClient.sql("select video_group_id from episode where id = :id and video_group_id is not null for update")
                .param("id", episodeId.value()).query(UUID.class).optional().orElseThrow(EpisodeNotFoundException::new);
        VideoGroupId groupId = new VideoGroupId(group);
        VideoId id = createVideo(groupId, video);
        if (details != null) upsertDetails(id, details);
        return findById(id).orElseThrow(VideoNotFoundException::new);
    }

    @Override
    @Transactional
    public Video update(VideoId id, Video patch, VideoDetails details) {
        Video updated = jdbcClient.sql("""
                        update video set description = coalesce(:description, description),
                          telegram_file_id = coalesce(:fileId, telegram_file_id),
                          telegram_file_unique_id = coalesce(:uniqueId, telegram_file_unique_id),
                          priority = coalesce(:priority, priority), is_available = coalesce(:available, is_available)
                        where id = :id returning *
                        """)
                .param("id", id.value()).param("description", patch.description(), Types.VARCHAR)
                .param("fileId", patch.telegramFileId() == null ? null : patch.telegramFileId().value(), Types.VARCHAR)
                .param("uniqueId", patch.telegramFileUniqueId() == null ? null : patch.telegramFileUniqueId().value(), Types.VARCHAR)
                .param("priority", patch.priority(), Types.INTEGER).param("available", patch.isAvailable(), Types.BOOLEAN)
                .query(mapper).optional().orElseThrow(VideoNotFoundException::new);
        if (details != null) upsertDetails(id, details);
        return updated;
    }

    private void upsertDetails(VideoId id, VideoDetails d) {
        jdbcClient.sql("""
                insert into video_details(video_id, width, height, duration, mime_type, file_size, file_name)
                values(:id, :width, :height, :duration, :mimeType, :fileSize, :fileName)
                on conflict (video_id) do update set width = excluded.width, height = excluded.height,
                  duration = excluded.duration, mime_type = excluded.mime_type,
                  file_size = excluded.file_size, file_name = excluded.file_name
                """)
                .param("id", id.value()).param("width", d.width()).param("height", d.height())
                .param("duration", d.duration()).param("mimeType", d.mimeType())
                .param("fileSize", d.fileSize()).param("fileName", d.fileName()).update();
    }

    @Override
    public Optional<VideoDetails> findDetails(VideoId id) {
        return jdbcClient.sql("select * from video_details where video_id = :id").param("id", id.value())
                .query((rs, row) -> new VideoDetails(id, rs.getInt("width"), rs.getInt("height"),
                        rs.getInt("duration"), rs.getString("mime_type"), rs.getLong("file_size"), rs.getString("file_name")))
                .optional();
    }

    @Override
    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return jdbcClient.sql("""
                        select v.*
                        from video v
                                 join public.video_group vg on vg.id = v.video_group_id
                        where vg.id = :videoGroupId and v.is_available = true
                        order by priority DESC, v.id
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
        return jdbcClient.sql("select * from video where video_group_id = :videoGroupId order by priority desc, id")
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
