package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private InMemoryUserStorage inMemoryUserStorage;

    public User create(User user) throws ValidationException {
        validateUser(user);
        User newUser = inMemoryUserStorage.save(user);
        log.info("Пользователь успешно добавлен - {}", newUser);
        return newUser;
    }

    public User update(User newUser) throws ValidationException {
        User user = inMemoryUserStorage.update(newUser);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
        }
        validateUser(newUser);
        log.debug("Создаем объект копию User обновляемого пользователя");
        log.info("Пользователь успешно обновлен - {}", newUser);
        return user;
    }

    public void validateUser(User user) throws ValidationException {
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

    public Collection<User> findAll() {
        log.debug("Использован метод по получению всех пользователей");
        return inMemoryUserStorage.findAll();
    }

    public User addFriend(long id, long friendId) throws ValidationException {
        log.info("Запрос на добавление друга: пользователь {} хочет добавить друга {}", id, friendId);
        User user = inMemoryUserStorage.getUser(id);
        User userFriend = inMemoryUserStorage.getUser(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        log.debug("Валидация пользователя {}", id);
        validateUser(user);
        log.debug("Валидация пользователя {}", friendId);
        validateUser(userFriend);
        log.info("Добавление друга {} пользователю {}", friendId, id);
        user.getFriendsId().add(friendId);
        userFriend.getFriendsId().add(id);
        update(userFriend);
        log.info("Друг {} успешно добавлен пользователю {}", friendId, id);
        return update(user);
    }

    public User deleteFriend(long id, long friendId) throws ValidationException {
        log.info("Запрос на удаление друга: пользователь {} хочет удалить друга {}", id, friendId);

        User user = inMemoryUserStorage.getUser(id);
        User userFriend = inMemoryUserStorage.getUser(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        if (!user.getFriendsId().contains(friendId)) {
            log.debug("Пользователи {} и {} не являются друзьями, удалять нечего", id, friendId);
            return user;
        }
        log.debug("Валидация пользователя {}", id);
        validateUser(user);
        log.debug("Валидация пользователя {}", friendId);
        validateUser(userFriend);

        log.info("Удаление друга {} у пользователя {}", friendId, id);
        user.getFriendsId().remove(friendId);
        userFriend.getFriendsId().remove(id);

        update(userFriend);
        User updatedUser = update(user);

        log.info("Друг {} успешно удален у пользователя {}", friendId, id);
        return updatedUser;
    }

    public Set<User> getFriendsList(long id) throws ValidationException {

        User user = inMemoryUserStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user.getFriendsId().stream()
                .map(id1 -> inMemoryUserStorage.getUser(id1))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(long id, long otherId) throws ValidationException {
        log.info("Поиск общих друзей: пользователь {} и пользователь {}", id, otherId);

        User user = inMemoryUserStorage.getUser(id);
        User otherUser = inMemoryUserStorage.getUser(otherId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (otherUser == null) {
            throw new NotFoundException("Пользователь с id " + otherId + " не найден");
        }
        Set<User> friends1 = getFriendsList(id);
        Set<User> friends2 = getFriendsList(otherId);

        log.debug("У пользователя {} найдено {} друзей", id, friends1.size());
        log.debug("У пользователя {} найдено {} друзей", otherId, friends2.size());

        Set<User> commonFriends = new HashSet<>(friends1);
        commonFriends.retainAll(friends2);

        log.info("Найдено {} общих друзей у пользователей {} и {}", commonFriends.size(), id, otherId);

        return commonFriends;
    }
}
