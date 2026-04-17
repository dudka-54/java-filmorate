package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Qualifier
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday)" +
                "VALUES(?, ?, ?, ?)" +
                "RETURNING id";

        Long generatedId = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(generatedId);
        return user;
    }

    @Override
    public User update(User newUser) throws ValidationException {
        String sql = "UPDATE users" +
                "SET email = ?, login = ?, name = ?, birthday = ?" +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        log.info("Обновлён пользователь с id={}", newUser.getId());
        return newUser;
    }
}
