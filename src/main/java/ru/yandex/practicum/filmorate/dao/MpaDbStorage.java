package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Primary
@Qualifier("MpaDbStorage")
@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT mpa_id, mpa_name FROM mpa ORDER BY mpa_id ";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    @Override
    public Optional<Mpa> getMpaOnId(int id) {
        String sql = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = ? ";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql, new MpaMapper(), id);
            return Optional.ofNullable(mpa);
        } catch (NotFoundException e) {
            log.debug("Mpa с id={} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Integer> getAllIds() {
        String sql = "SELECT mpa_id FROM mpa ORDER BY mpa_id ";
        return jdbcTemplate.queryForList(sql, Integer.class);
    }
}
