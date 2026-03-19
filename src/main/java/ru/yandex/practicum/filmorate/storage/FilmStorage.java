package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    public Film save(Film film);
    public Film update(Film newFilm) throws ValidationException;
}
