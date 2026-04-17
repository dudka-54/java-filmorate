package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

public class FilmDbStorage implements FilmStorage {
    @Override
    public Film save(Film film) {
        return null;
    }

    @Override
    public Film update(Film newFilm) throws ValidationException {
        return null;
    }
}
