package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController {
    private GenreService genreService;

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> allGenres = genreService.getAllGenres();
        return ResponseEntity.ok(allGenres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreOnId(@PathVariable int id) {
        Genre genre = genreService.getGenreOnId(id);
        return ResponseEntity.ok(genre);
    }
}
