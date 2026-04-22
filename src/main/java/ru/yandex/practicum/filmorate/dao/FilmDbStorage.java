package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;


@Repository
@RequiredArgsConstructor
@Slf4j
@Primary
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
                "VALUES(?, ?, ?, ?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKeyAs(Long.class);
        film.setId(generatedId);
        saveGenres(film);

        film.setLikes(new HashSet<>());
        log.info("Создан фильм с id={}", generatedId);
        return film;
    }

    @Override
    public Film update(Film newFilm) throws ValidationException {
        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";

        int rowsUpdated = jdbcTemplate.update(sql,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        if (rowsUpdated == 0) {
            throw new ValidationException("Фильм с id=" + newFilm.getId() + " не найден");
        }
        saveGenres(newFilm);
        log.info("Обновлён пользователь с id={}", newFilm.getId());
        return newFilm;
    }

    @Override
    public Film getFilm(long id) {
        String sql = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE f.id = ? ";

        try {
            Film film = jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
            film.setGenres(loadGenres(id));
            film.setLikes(loadLikes(id));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public Map<Long, Film> getFilms() {
        String sql = "SELECT f.*, m.mpa_name " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id ";

        Map<Long, Film> films = new HashMap<>();
        for (Film film : jdbcTemplate.query(sql, new FilmMapper())) {
            film.setGenres(loadGenres(film.getId()));
            film.setLikes(loadLikes(film.getId()));
            films.put(film.getId(), film);
        }
        return films;
    }

    private Set<Genre> loadGenres(Long id) {
        String sql = "SELECT g.genre_id, g.genre_name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";

        List<Genre> genres = jdbcTemplate.query(sql, new GenreMapper(), id);
        return new HashSet<>(genres);
    }

    public void saveGenres(Film film) {
        String deleteGenresSql = "DELETE FROM film_genres " +
                "WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO film_genres(film_id, genre_id) " +
                "VALUES (?, ?)";
        Set<Genre> genres = new HashSet<>(film.getGenres());
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO film_likes(film_id, user_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Set<Long> loadLikes(long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        try {
            List<Long> likes = jdbcTemplate.queryForList(sql, Long.class, filmId);
            return new HashSet<>(likes);
        } catch (Exception e) {
            log.error("Ошибка при загрузке лайков для фильма id={}: {}", filmId, e.getMessage());
            return new HashSet<>();
        }
    }

    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }
}
