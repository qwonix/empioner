package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.bot.config.UUIDIdCoercing;

import java.util.UUID;

@DgsScalar(name = "MovieId")
public class MovieIdCoercing extends UUIDIdCoercing<MovieId> {
    @Override
    public MovieId parseValue(UUID input) {
        return new MovieId(input);
    }
}
