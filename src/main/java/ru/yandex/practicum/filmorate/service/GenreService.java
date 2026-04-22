package ru.yandex.practicum.filmorate.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("GenreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        log.info("Получение всех жанров");
        return genreStorage.getAllGenres();
    }

    public Genre getGenreOnId(Integer id) {
        log.info("Получение жанра по id {}", id);
        return genreStorage.getGenreOnId(id)
                .orElseThrow(() -> {
                    log.warn("id жанра {} не найдено ", id);
                    return new NotFoundException("id жанра {} не найдено " + id + " не найден ");
                });
    }
}

