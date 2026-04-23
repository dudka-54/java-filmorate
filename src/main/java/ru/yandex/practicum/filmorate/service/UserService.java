package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    protected void validateUser(User user) throws ValidationException {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Email не должен быть пустым или содержать только пробелы");
            }
            if (!(user.getEmail().contains(String.valueOf('@')))) {
                throw new ValidationException("Email должен содержать символ - @ ");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new ValidationException("Логин не может быть пустым");
            }
            if (user.getBirthday() == null) {
                throw new ValidationException("Дата рождения должна быть указана");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
    }

    public User create(User user) throws ValidationException {
        validateUser(user);

        User newUser = userStorage.save(user);
        log.info("Пользователь успешно добавлен - {}", newUser);
        return newUser;
    }

    public User update(User newUser) throws ValidationException {
        User existingUser = userStorage.getUser(newUser.getId());
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
        }
        validateUser(newUser);

        User updatedUser = userStorage.update(newUser);
        log.info("Пользователь успешно обновлён - {}", updatedUser);
        return updatedUser;
    }


    public Collection<User> findAll() {
        log.info("Использован метод по получению всех пользователей");
        return userStorage.findAll();
    }

    public User addFriend(long id, long friendId) throws ValidationException {
        log.info("Запрос на добавление друга: пользователь {} хочет добавить друга {}", id, friendId);

        User user = userStorage.getUser(id);
        User userFriend = userStorage.getUser(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        if (id == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
        if (user.getFriends().contains(friendId)) {
            throw new ValidationException("Пользователь уже в друзьях");
        }

        validateUser(user);
        validateUser(userFriend);

        user.getFriends().add(friendId);
        userStorage.addFriend(id, friendId);

        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
        return userStorage.update(user);
    }

    public User deleteFriend(long id, long friendId) throws ValidationException {
        log.info("Запрос на удаление друга: пользователь {} хочет удалить пользователя {} из списка друзей", id, friendId);

        User user = userStorage.getUser(id);
        User userFriend = userStorage.getUser(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        if (!user.getFriends().contains(friendId)) {
            log.debug("Пользователи {} и {} не являются друзьями, удалять нечего", id, friendId);
            return user;
        }
        validateUser(user);
        validateUser(userFriend);

        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(id);

        userStorage.deleteFriend(id, friendId);
        userStorage.deleteFriend(friendId, id);

        userStorage.update(userFriend);
        User updatedUser = userStorage.update(user);

        log.info("Друг {} успешно удален у пользователя {}", friendId, id);
        return updatedUser;
    }

    public Set<User> getFriendsSet(long id) throws ValidationException {
        log.info("Вызван метод на получение множества друзей id - {}", id);
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriendsSet(long id, long otherId) {
        log.info("метод получения множества общих друзей {} и {}", id, otherId);
        Set<User> friends1 = getFriendsSet(id);
        Set<User> friends2 = getFriendsSet(otherId);

        friends1.retainAll(friends2);
        return friends1;
    }
}
