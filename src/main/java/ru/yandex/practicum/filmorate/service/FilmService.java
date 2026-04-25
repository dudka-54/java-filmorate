package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.dto.genre.GenreIdRequest;

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

    private final GenreService genreService;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       UserService userService,
                       MpaService mpaService,
                       GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
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

    public FilmDto create(FilmRequest filmRequest) throws ValidationException {
        Film film = FilmMapper.mapToFilm(filmRequest);

        if (filmRequest.getMpa() != null) {
            film.setMpa(mpaService.getMpaOnId(filmRequest.getMpa().getId()));
        }
        if (filmRequest.getMpa() == null) {
            throw new ValidationException("MPA рейтинг обязателен");
        }


        if (filmRequest.getGenres() != null && !filmRequest.getGenres().isEmpty()) {
            List<Genre> genres = filmRequest.getGenres().stream()
                    .map(GenreIdRequest::getId)
                    .map(genreService::getGenreOnId)
                    .collect(Collectors.toList());
            film.setGenres(genres);
        }

        film.setLikes(new HashSet<>());

        validateFilm(film);

        Film savedFilm = filmStorage.save(film);
        log.info("Фильм успешно добавлен - {}", filmRequest);
        return FilmMapper.mapToDto(savedFilm);
    }

    public FilmDto update(UpdateFilmRequest newFilm) throws ValidationException {
        Film updatingFilm = filmStorage.getFilm(newFilm.getId())
                .map(film -> FilmMapper.updateFilmFields(film, newFilm))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        if (newFilm.getMpaId() != null) {
            updatingFilm.setMpa(mpaService.getMpaOnId(newFilm.getMpaId()));
        }

        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            List<Genre> genres = newFilm.getGenres().stream()
                    .map(GenreIdRequest::getId)
                    .map(genreService::getGenreOnId)
                    .collect(Collectors.toList());
            updatingFilm.setGenres(genres);
        }

        validateFilm(updatingFilm);

        Film film = filmStorage.update(updatingFilm);
        log.info("Фильм успешно обновлен - {}", newFilm);
        return FilmMapper.mapToDto(film);
    }

    public Collection<FilmDto> getAllFilms() {
        return filmStorage.getFilms().values().stream()
                .map(FilmMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    public FilmDto getFilm(long id) {
        Film film = filmStorage.getFilm(id).orElseThrow(() -> new NotFoundException("Фильм не найден с id " + id));
        return FilmMapper.mapToDto(film);
    }

    public FilmDto addLike(long filmId, long userId) throws ValidationException {
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден с id " + filmId));
        User user = userStorage.getUser(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден с id " + userId));

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
        return FilmMapper.mapToDto(film);
    }

    public FilmDto deleteLike(long filmId, long userId) throws ValidationException {
        Film film = filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден с id " + filmId));
        User user = userStorage.getUser(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден с id " + userId));

        validateFilm(film);
        userService.validateUser(user);

        log.debug("Получен объект User {} по id {}", user.getName(), userId);
        log.debug("Получен объект Film {} по id {}", film.getName(), filmId);

        if (!film.getLikes().contains(userId)) {
            log.debug("Попытка удалить несуществующий лайк от пользователя {} к фильму {}", userId, filmId);
            return FilmMapper.mapToDto(film);
        }
        film.getLikes().remove(userId);
        filmStorage.deleteLike(filmId, userId);
        log.info("Лайк успешно убран");

        return FilmMapper.mapToDto(film);
    }

    public List<FilmDto> getPopular(Integer count) {
        return filmStorage.getFilms().values().stream()
                .sorted((f1, f2) -> {
                    try {
                        return Integer.compare(getCountLikes(f2), getCountLikes(f1));
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .limit(count)
                .map(FilmMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private Integer getCountLikes(Film film) throws ValidationException {
        log.debug("Использован метод по получению количества лайков под фильмом{}", film.getName());
        validateFilm(film);
        return film.getLikes().size();
    }
}
