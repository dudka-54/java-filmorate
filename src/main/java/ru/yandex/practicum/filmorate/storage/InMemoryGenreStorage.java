package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryGenreStorage implements GenreStorage {

    private final HashMap<Integer, Genre> genreMap;

    public InMemoryGenreStorage() {
        genreMap = new HashMap<>(java.util.Map.of(
                1, new Genre(1, "COMEDY"),
                2, new Genre(2, "DRAMA"),
                3, new Genre(3, "CARTOON"),
                4, new Genre(4, "THRILLER"),
                5, new Genre(5, "DOCUMENTARY"),
                6, new Genre(6, "ACTION")));
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreMap.values().
                stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Genre> getGenreOnId(int id) {
        return Optional.ofNullable(genreMap.get(id));
    }

    @Override
    public List<Integer> getAllIds() {
        return genreMap.values().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
    }
}
