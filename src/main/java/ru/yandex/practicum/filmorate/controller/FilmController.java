package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<Film>> findAll() {
        log.info("Выбран метод GET - получение списка всех фильмов");
        Collection<Film> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) throws ValidationException {
        log.debug("Получен запрос на создание фильма: {}", film);
        Film created = filmService.create(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film newFilm) throws ValidationException {
        log.debug("Получен запрос на обновление фильма: {}", newFilm);
        Film updated = filmService.update(newFilm);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long id, @PathVariable long userId) throws ValidationException {
        Film film = filmService.addLike(id, userId);
        return ResponseEntity.ok(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long id, @PathVariable long userId) throws ValidationException {
        Film film = filmService.deleteLike(id, userId);
        return ResponseEntity.ok(film);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        List<Film> popularFilms = filmService.getPopular(count);
        return ResponseEntity.ok(popularFilms);
    }
}

