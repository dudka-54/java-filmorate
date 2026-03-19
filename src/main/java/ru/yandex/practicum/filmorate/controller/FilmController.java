package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Выбран метод GET - получение списка всех фильмов");
        return inMemoryFilmStorage.getFilms().values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        log.debug("Получен запрос на создание фильма: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) throws ValidationException {
        log.debug("Получен запрос на обновление фильма: {}", newFilm);
        return filmService.update(newFilm);
    }
}

