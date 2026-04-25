package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    User update(User newUser) throws ValidationException;

    Collection<User> findAll();

    Optional<User> getUser(long id);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);
}
