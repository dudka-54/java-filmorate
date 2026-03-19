package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Getter
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film save(Film film) {
        film.setId(getNextId());
        log.trace("Устанавливаем новое значение поля id {}", film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) throws ValidationException {
        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.warn("Фильм с id={} не найден", newFilm.getId());
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        log.debug("Обновление фильма id={}:", oldFilm.getId());
        log.debug("  name: '{}' → '{}'", oldFilm.getName(), newFilm.getName());
        log.debug("  description: '{}' → '{}'",
                oldFilm.getDescription(), newFilm.getDescription());
        log.debug("  releaseDate: {} → {}",
                oldFilm.getReleaseDate(), newFilm.getReleaseDate());
        log.debug("  duration: {} → {}",
                oldFilm.getDuration(), newFilm.getDuration());
        log.info("Фильм успешно обновлен - {}", newFilm);
        return oldFilm;
    }

    private long getNextId() {
        log.debug("Вызван метод getNextId для получения следующего id");
        long currentMaxId = getFilms().keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Текущее максимальное значение id - {}", currentMaxId);
        log.debug("Увеличиваем значение id перед использованием текущего значения в выражении");
        return ++currentMaxId;
    }
}
