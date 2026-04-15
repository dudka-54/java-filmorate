package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryGenreStorage implements GenreStorage {

    List<Genre> genreList;

    public InMemoryGenreStorage() {
        genreList = List.of(
                new Genre(1, "COMEDY"),
                new Genre(2, "DRAMA"),
                new Genre(3, "CARTOON"),
                new Genre(4, "THRILLER"),
                new Genre(5, "DOCUMENTARY"),
                new Genre(6, "ACTION"));
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreList.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Genre> getGenreOnId(int id) {
        return Optional.ofNullable(genreList.get(id));
    }

    public List<Integer> getAllIds(){
        return genreList.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
    }
}
