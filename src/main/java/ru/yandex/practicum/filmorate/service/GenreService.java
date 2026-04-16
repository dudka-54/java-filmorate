package ru.yandex.practicum.filmorate.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.InMemoryGenreStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreService {
    private final InMemoryGenreStorage inMemoryGenreStorage;

    public List<Genre> getAllGenres() {
        log.info("Получение всех жанров");
        return inMemoryGenreStorage.getAllGenres();
    }

    public Genre getGenreOnId(Integer id) {
        log.info("Получение жанра по id {}", id);
        if (!(inMemoryGenreStorage.getAllIds().contains(id))) {
            throw new NotFoundException("Такого id нет");
        }
        return inMemoryGenreStorage.getGenreOnId(id)
                .orElseThrow(() -> {
                    log.warn("Рейтинг MPA с id {} не найден ", id);
                    return new NotFoundException("Рейтинг MPA с id " + id + " {} не найден ");
                });
    }
}

