package ru.qwonix.empioner.telegram.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.service.dao.ShowDao;
import ru.qwonix.empioner.telegram.service.entity.Show;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;
import ru.qwonix.empioner.telegram.service.service.ShowService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShowServiceImpl implements ShowService {

    private final ShowDao showDao;

    @Override
    public Optional<Show> findById(ShowId showId) {
        return showDao.findById(showId);
    }

    @Override
    public List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page) {
        return showDao.findAllOrderByNumberWithLimitAndPage(limit, page);
    }
}
