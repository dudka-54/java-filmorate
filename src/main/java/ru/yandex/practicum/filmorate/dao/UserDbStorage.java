package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

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

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT *" +
                "FROM users" +
                "ORDER BY id";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public User getUser(long id) {
        String sql = "SELECT * " +
                "FROM users" +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
    }

    public void addFriend(long id, long friendId) {
        String sql = "INSERT INTO friendships(user_id, friend_id, status)" +
                "VALUES (?, ?, 'PENDING')";
        jdbcTemplate.update(sql, id, friendId);
    }

    public void confirmFriend(long id, long friendId) {
        String sql = "UPDATE friendships" +
                    "SET status = 'CONFIRMED'" +
                    "WHERE id = ? AND friend_id = ?";
        jdbcTemplate.update(sql,id, friendId);
    }
}
