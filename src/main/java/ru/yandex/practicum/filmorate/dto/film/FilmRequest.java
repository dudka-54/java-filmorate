package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreIdRequest;
import ru.yandex.practicum.filmorate.dto.mpa.MpaIdResponse;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaIdResponse mpa;

    private List<GenreIdRequest> genres;
}
