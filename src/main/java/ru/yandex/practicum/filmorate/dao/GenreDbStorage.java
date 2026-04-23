package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Primary
@Qualifier("GenreDbStorage")
@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT genre_id, genre_name FROM genres ORDER BY genre_id ";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public Optional<Genre> getGenreOnId(int id) {
        String sql = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ? ";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql, new GenreMapper(), id);
            return Optional.ofNullable(genre);
        } catch (NotFoundException e) {
            log.debug("Жанр с id={} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Integer> getAllIds() {
        String sql = "SELECT genre_id FROM genres ORDER BY genre_id ";
        return jdbcTemplate.queryForList(sql, Integer.class);
    }
}
