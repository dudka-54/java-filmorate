package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final LocalDate birthDayMovie = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final UserService userService;

    private final MpaService mpaService;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       UserService userService,
                       MpaService mpaService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
        this.mpaService = mpaService;
    }


    public void validateFilm(Film film) throws ValidationException {
        try {
            if (film == null) {
                throw new ValidationException("Тело запроса не может быть пустым");
            }
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым");
            }
            if (film.getDescription() == null || film.getDescription().length() > 200) {
                if (film.getDescription() == null) {
                    throw new ValidationException("Описание не может быть пустым");
                }
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
        Mpa mpa = film.getMpa();
        Mpa fullMpa = mpaService.getMpaOnId(mpa.getId());
        film.setMpa(fullMpa);
        Film savedFilm = filmStorage.save(film);
        log.info("Фильм успешно добавлен - {}", film);
        return savedFilm;
    }

    public Film update(Film newFilm) throws ValidationException {
        Film existingFilm = filmStorage.getFilm(newFilm.getId());
        if (existingFilm == null) {
            throw new ValidationException("Фильм с id " + newFilm.getId() + " не найден");
        }
        validateFilm(newFilm);

        if (newFilm.getMpa() != null && newFilm.getMpa().getId() != null) {
            Mpa fullMpa = mpaService.getMpaOnId(newFilm.getMpa().getId());
            newFilm.setMpa(fullMpa);
        } else {
            newFilm.setMpa(existingFilm.getMpa());
        }
        newFilm.setLikes(existingFilm.getLikes());
        if (newFilm.getGenres() == null) {
            newFilm.setGenres(existingFilm.getGenres());
        }
        Film film = filmStorage.update(newFilm);
        log.info("Фильм успешно обновлен - {}", newFilm);
        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms().values();
    }

    public Film getFilm(long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    public Film addLike(long filmId, long userId) throws ValidationException {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Фильму " + film.getName() + " уже поставлен лайк пользователем - " + user.getName());
        }
        log.debug("Получен объект User {} по id {}", user.getName(), userId);
        log.debug("Получен объект Film {} по id {}", film.getName(), filmId);
        validateFilm(film);
        userService.validateUser(user);
        film.getLikes().add(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Лайк успешно поставлен");
        return film;
    }

    public Film deleteLike(long filmId, long userId) throws ValidationException {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        validateFilm(film);
        userService.validateUser(user);
        log.debug("Получен объект User {} по id {}", user.getName(), userId);
        log.debug("Получен объект Film {} по id {}", film.getName(), filmId);
        if (!film.getLikes().contains(userId)) {
            log.debug("Попытка удалить несуществующий лайк от пользователя {} к фильму {}", userId, filmId);
            return film;
        }
        film.getLikes().remove(userId);
        filmStorage.deleteLike(filmId, userId);
        log.info("Лайк успешно убран");

        return film;
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getFilms().values().stream()
                .sorted((f1, f2) -> {
                    try {
                        return Integer.compare(getCountLikes(f2), getCountLikes(f1));
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    private Integer getCountLikes(Film film) throws ValidationException {
        log.debug("Использован метод по получению количества лайков под фильмом{}", film.getName());
        validateFilm(film);
        return (int) film.getLikes().size();
    }
}
