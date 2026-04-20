package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User save(User user);

    public User update(User newUser) throws ValidationException;

    public Collection<User> findAll();

    public User getUser(long id);
    }
