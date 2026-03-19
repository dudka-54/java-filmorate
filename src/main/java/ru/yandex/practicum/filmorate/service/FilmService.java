package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final LocalDate birthDayMovie = LocalDate.of(1895, 12, 28);

    private final InMemoryFilmStorage inMemoryFilmStorage;


    public void validateFilm(Film film) throws ValidationException {
        try {
            if (film == null) {
                throw new ValidationException("Тело запроса не может быть пустым");
            }
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Длина описания не должна быть больше 200");
            }
            if (film.getReleaseDate().isBefore(birthDayMovie)) {
                throw new ValidationException("Дата релиза должна быть не раньше  28 декабря 1895 года");
            }
            if (film.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительным числом");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
    }

    public Film create(Film film) throws ValidationException {
        validateFilm(film);
        Film savedFilm = inMemoryFilmStorage.save(film);
        log.info("Фильм успешно добавлен - {}", film);
        return savedFilm;
    }

    public Film update(Film newFilm) throws ValidationException {
        validateFilm(newFilm);
        Film film = update(newFilm);
        log.debug("Создаем объект копию oldFilm обновляемого фильма");
        log.info("Фильм успешно обновлен - {}", newFilm);
        return film;
    }
}
