package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<FilmDto>> findAll() {
        log.info("Выбран метод GET - получение списка всех фильмов");
        Collection<FilmDto> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilm(@PathVariable long id) {
        log.info("Получен запрос на получение фильма {}", id);
        FilmDto film = filmService.getFilm(id);
        return ResponseEntity.ok(film);
    }

    @PostMapping
    public ResponseEntity<FilmDto> create(@RequestBody FilmRequest film) throws ValidationException {
        log.debug("Получен запрос на создание фильма: {}", film);
        FilmDto created = filmService.create(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<FilmDto> update(@RequestBody UpdateFilmRequest newFilm) throws ValidationException {
        log.debug("Получен запрос на обновление фильма: {}", newFilm);
        FilmDto updated = filmService.update(newFilm);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<FilmDto> addLike(@PathVariable long id, @PathVariable long userId) throws ValidationException {
        FilmDto film = filmService.addLike(id, userId);
        return ResponseEntity.ok(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<FilmDto> deleteLike(@PathVariable long id, @PathVariable long userId) throws ValidationException {
        FilmDto film = filmService.deleteLike(id, userId);
        return ResponseEntity.ok(film);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDto>> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        List<FilmDto> popularFilms = filmService.getPopular(count);
        return ResponseEntity.ok(popularFilms);
    }

}

