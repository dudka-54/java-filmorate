package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    public List<Mpa> getAllMpa();

    public Optional<Mpa> getMpaOnId(int id);
}
