package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

public class MpaDbStorage implements MpaStorage {
    @Override
    public List<Mpa> getAllMpa() {
        return List.of();
    }

    @Override
    public Optional<Mpa> getMpaOnId(int id) {
        return Optional.empty();
    }
}
