package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final LocalDate birthDayMovie = LocalDate.of(1895, 12, 28);

    private final InMemoryFilmStorage inMemoryFilmStorage;

    private final InMemoryUserStorage inMemoryUserStorage;

    private final UserService userService;

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
        Film savedFilm = inMemoryFilmStorage.save(film);
        log.info("Фильм успешно добавлен - {}", film);
        return savedFilm;
    }

    public Film update(Film newFilm) throws ValidationException {
        Film existingFilm = inMemoryFilmStorage.getFilm(newFilm.getId());
        if (existingFilm == null) {
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }
        validateFilm(newFilm);
        Film film = inMemoryFilmStorage.update(newFilm);
        log.debug("Создаем объект копию oldFilm обновляемого фильма");
        log.info("Фильм успешно обновлен - {}", newFilm);
        return film;
    }

    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getFilms().values();
    }

    public Film addLike(long filmId, long userId) throws ValidationException {
        Film film = inMemoryFilmStorage.getFilm(filmId);
        User user = inMemoryUserStorage.getUser(userId);
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
        log.info("Лайк успешно поставлен");
        return film;
    }

    public Film deleteLike(long filmId, long userId) throws ValidationException {
        Film film = inMemoryFilmStorage.getFilm(filmId);
        User user = inMemoryUserStorage.getUser(userId);
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
        log.info("Лайк успешно убран");

        return film;
    }

    public List<Film> getPopular(Integer count) {
        return inMemoryFilmStorage.getFilms().values().stream()
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
