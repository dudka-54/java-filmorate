package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Primary
@Qualifier("UserDbStorage")
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES(?, ?, ?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKeyAs(Long.class));
        user.setFriendships(new HashMap<>());
        log.info("Создан пользователь с id={}", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) throws ValidationException {

        String sql = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ? ";

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
        String sql = "SELECT * " +
                "FROM users " +
                "ORDER BY id ";
        List<User> userList = jdbcTemplate.query(sql, new UserMapper());
        userList.forEach(user -> {
            user.setFriendships(loadFriendshipsStatuses(user.getId()));
        });
        return userList;
    }

    @Override
    public User getUser(long id) {
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE id = ? ";
        User user = jdbcTemplate.queryForObject(sql, new UserMapper(), id);
        user.setFriendships(loadFriendshipsStatuses(id));
        return user;
    }

    @Override
    public void addFriend(long id, long friendId) {
        String sql = "INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public void confirmFriend(long id, long friendId) {
        String sql = "UPDATE friendships " +
                "SET status = 'CONFIRMED' " +
                "WHERE user_id = ? AND friend_id = ? ";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public void deleteFriend(long id, long friendId){
        String sql = "DELETE " +
                    "FROM friendships " +
                    "WHERE user_id = ? AND friend_id = ? ";
        jdbcTemplate.update(sql, id, friendId);
    }

    private Map<Long, FriendshipStatus> loadFriendshipsStatuses(long userId){
        String sql = "SELECT friend_id, status FROM friendships WHERE user_id = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
        Map<Long, FriendshipStatus> map = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Long friendId = (Long) row.get("friend_id");
            String statusStr = (String) row.get("status");
            map.put(friendId, FriendshipStatus.valueOf(statusStr));
        }

        return map;
    }
}
