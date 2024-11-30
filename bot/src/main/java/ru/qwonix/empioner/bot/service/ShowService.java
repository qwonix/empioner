package ru.qwonix.empioner.bot.service;

import ru.qwonix.empioner.bot.entity.Show;
import ru.qwonix.empioner.bot.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface ShowService {
    Optional<Show> findById(ShowId showId);

    List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page);
}
