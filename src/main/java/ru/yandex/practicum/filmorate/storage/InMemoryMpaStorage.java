package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Integer, Mpa> mpaMap;

    public InMemoryMpaStorage() {
        mpaMap = new HashMap<>(Map.of(
                1, new Mpa(1, "G"),
                2, new Mpa(2, "PG"),
                3, new Mpa(3, "PG-13"),
                4, new Mpa(4, "R"),
                5, new Mpa(5, "NC-17")
        ));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return mpaMap.values().stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Mpa> getMpaOnId(int id) {
        return Optional.ofNullable(mpaMap.get(id));
    }

    public List<Integer> getAllIds() {
        return mpaMap.values().stream()
                .map(Mpa::getId)
                .sorted()
                .collect(Collectors.toList());
    }
}
