package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public final class FilmMapper {

    public static Film mapToFilm(FilmRequest filmRequest) {
        Film film = new Film();
        film.setName(filmRequest.getName());
        film.setDescription(filmRequest.getDescription());
        film.setReleaseDate(filmRequest.getReleaseDate());
        film.setDuration(filmRequest.getDuration());


        film.setLikes(new HashSet<>());
        return film;
    }

    public static FilmDto mapToDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        if (film.getMpa() != null) {
            filmDto.setMpa(new Mpa(film.getMpa().getId(), film.getMpa().getName()));
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> genreDtos = film.getGenres();
            filmDto.setGenres(genreDtos);
        } else {
            filmDto.setGenres(new ArrayList<>());
        }
        filmDto.setLikes(film.getLikes());
        filmDto.setId(film.getId());
        return filmDto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }

        return film;
    }
}
