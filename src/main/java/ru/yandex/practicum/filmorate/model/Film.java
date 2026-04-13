package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    Genre genre;
    MPA mpa;

    Set<Long> likes = new HashSet<>();


}
