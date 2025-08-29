package ru.qwonix.empioner.telegram.service.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.ShowApi;
import ru.qwonix.empioner.telegram.service.spi.ShowSpi;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShowUseCase implements ShowApi {

    private final ShowSpi showDao;

    @Override
    public Optional<Show> findById(ShowId showId) {
        return showDao.findById(showId);
    }

    @Override
    public List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page) {
        return showDao.findAllOrderByNumberWithLimitAndPage(limit, page);
    }
}
