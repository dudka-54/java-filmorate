package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryMpaStorage implements MpaStorage {

    private final List<Mpa> mpaList = new ArrayList<>();

    public InMemoryMpaStorage() {
        mpaList.add(new Mpa(1, "G"));
        mpaList.add(new Mpa(2, "PG"));
        mpaList.add(new Mpa(3, "PG-13"));
        mpaList.add(new Mpa(4, "R"));
        mpaList.add(new Mpa(5, "NC-17"));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return mpaList.stream()
                .sorted(Comparator.comparing(Mpa::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Mpa> getMpaOnId(int id) {
        return Optional.ofNullable(mpaList.get(id));
    }

    public List<Integer> getAllIds(){
        return mpaList.stream()
                .map(Mpa::getId)
                .collect(Collectors.toList());
    }
}
