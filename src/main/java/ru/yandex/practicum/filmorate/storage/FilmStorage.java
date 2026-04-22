package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    public Film save(Film film);

    public Film update(Film newFilm) throws ValidationException;

    Film getFilm(long id);

    Map<Long, Film> getFilms();
    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
}
