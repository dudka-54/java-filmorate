package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
@Qualifier
public class FilmDbStorage implements FilmStorage {

    JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
                "VALUES(?, ?, ?, ?, ?)";

        long generatedId = jdbcTemplate.queryForObject(sql,
                Long.class,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(generatedId);
        return film;
    }

    @Override
    public Film update(Film newFilm) throws ValidationException {
        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        log.info("Обновлён пользователь с id={}", newFilm.getId());
        return newFilm;
    }

    @Override
    public Film getFilm(long id) {
        String sql = "SELECT * " +
                    "FROM films" +
                    "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
    }

    public Set<Genre> saveGenres(Film film){
        String sql = "INSERT INTO film_genres(film_id, genre_id)" +
                        "VALUES ?, ?";
    }
}
