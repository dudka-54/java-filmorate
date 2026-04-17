package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

public class GenreDbStorage implements GenreStorage {
    @Override
    public List<Genre> getAllGenres() {
        return List.of();
    }

    @Override
    public Optional<Genre> getGenreOnId(int id) {
        return Optional.empty();
    }
}
