package ru.qwonix.empioner.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.qwonix.empioner.bot.entity.Movie;
import ru.qwonix.empioner.bot.entity.Video;
import ru.qwonix.empioner.bot.entity.id.MovieId;
import ru.qwonix.empioner.bot.entity.id.ShowId;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.bot.service.MovieService;
import ru.qwonix.empioner.bot.service.VideoService;
import ru.qwonix.empioner.bot.telegram.callback.data.*;
import ru.qwonix.empioner.bot.telegram.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.qwonix.empioner.bot.telegram.handler.ChatCommandHandler.FIRST_PAGE;

@Service
@RequiredArgsConstructor
public class TelegramMovieService {

    private final MovieService movieService;
    private final VideoService videoService;

    public List<Movie> findAllByShowId(ShowId id) {
        return movieService.findAllByShowId(id);
    }

    public InlineKeyboardMarkup createKeyboard(ShowId showId, List<Movie> movies) {
        final List<InlineKeyboardRow> keyboard = new ArrayList<>();

        List<InlineKeyboardRow> inlineKeyboardRows = movies.stream()
                .map(movie -> Utils.createCallbackDataButton(new MovieCallbackData(movie.id()), movie.title()))
                .map(InlineKeyboardRow::new)
                .toList();

        InlineKeyboardRow inlineKeyboardButtons = new InlineKeyboardRow(
                Utils.createCallbackDataButton(new ShowCallbackData(showId), "Назад"),
                Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "На главную"));

        keyboard.addAll(inlineKeyboardRows);
        keyboard.add(inlineKeyboardButtons);

        return new InlineKeyboardMarkup(keyboard);
    }


    public Optional<Video> findVideoFor(Movie movie) {
        return videoService.findMaxPriorityInGroup(movie.videoGroupId());
    }

    public String createText(Movie movie) {
        return movie.title() + '\n' + movie.description();
    }

    public InlineKeyboardMarkup createKeyboard(Movie movie, List<Video> videos) {
        final List<InlineKeyboardRow> keyboard = new ArrayList<>();

        List<InlineKeyboardRow> inlineKeyboardRows = videos.stream()
                .map(video -> Utils.createCallbackDataButton(new MovieVideoCallbackData(video.id()), video.description()))
                .map(InlineKeyboardRow::new).toList();

        InlineKeyboardRow inlineKeyboardButtons = new InlineKeyboardRow(
                Utils.createCallbackDataButton(new ShowMovieCallbackData(movie.showId(), FIRST_PAGE), "Назад"),
                Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "На главную"));
        keyboard.addAll(inlineKeyboardRows);
        keyboard.add(inlineKeyboardButtons);

        return new InlineKeyboardMarkup(keyboard);
    }

    public Optional<Movie> findById(MovieId id) {
        return movieService.findById(id);
    }

    public Optional<Movie> findByVideoGroupId(VideoGroupId id) {
        return movieService.findByVideoId(id);
    }
}
