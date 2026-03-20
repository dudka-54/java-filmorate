package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    public User save(User user);

    public User update(User newUser) throws ValidationException;
}
