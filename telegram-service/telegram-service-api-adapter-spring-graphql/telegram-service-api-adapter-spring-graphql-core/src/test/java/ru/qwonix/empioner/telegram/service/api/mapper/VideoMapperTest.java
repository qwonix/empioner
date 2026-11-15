package ru.qwonix.empioner.telegram.service.api.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.qwonix.empioner.telegram.entity.Video;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VideoMapperTest {
    private final VideoMapper mapper = Mappers.getMapper(VideoMapper.class);

    @Test
    void presentsOldQualityNamesConsistently() {
        assertEquals("Full HD", mapper.toInput(video("FullHD")).getDescription());
        assertEquals("Full HD", mapper.toInput(video("1080p")).getDescription());
        assertEquals("SD", mapper.toInput(video("480p")).getDescription());
        assertEquals("SD", mapper.toInput(video("SD")).getDescription());
        assertEquals("4K", mapper.toInput(video("4K")).getDescription());
    }

    private Video video(String label) {
        return new Video(null, null, label, null, null, 1, true);
    }
}

