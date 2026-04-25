package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film newFilm) throws ValidationException;

    Optional<Film> getFilm(long id);

    Map<Long, Film> getFilms();

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);
}
