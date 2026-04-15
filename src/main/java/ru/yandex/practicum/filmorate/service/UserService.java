package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
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
        if (user.getFriendships().containsKey(friendId)) {
            FriendshipStatus currentStatus = user.getFriendships().get(friendId);
            if (currentStatus == FriendshipStatus.PENDING) {
                throw new ValidationException("Заявка уже отправлена и ожидает подтверждения");
            } else if (currentStatus == FriendshipStatus.CONFIRMED) {
                throw new ValidationException("Пользователи уже являются друзьями");
            }
        }
        if (id == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
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
        user.getFriendships().put(friendId, FriendshipStatus.PENDING);
        userFriend.getFriendships().put(id, FriendshipStatus.PENDING);
        log.debug("Пользователь {} и пользователь {} попадают в списки friendsId со статусом PENDING",
                user, userFriend);
        update(userFriend);
        log.info("Пользователь успешно {} отправил запрос в друзья пользователю {}", id, friendId);
        return update(user);
    }

    public User confirmFriend(long id, long friendId){
        log.info("Запрос на добавление в друзья: отправитель={}, получатель={}", friendId, id);
        User user = inMemoryUserStorage.getUser(id);
        User userFriend = inMemoryUserStorage.getUser(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        if (!user.getFriendships().containsKey(friendId)) {
            throw new ValidationException("Нет заявки в друзья от пользователя " + friendId);
        }

        if (user.getFriendships().get(friendId) != FriendshipStatus.PENDING) {
            throw new ValidationException("Заявка уже обработана или пользователи уже друзья");
        }
        log.debug("Валидация пользователя {}", id);
        validateUser(user);
        log.debug("Валидация пользователя {}", friendId);
        validateUser(userFriend);
        userFriend.getFriendships().replace(id, FriendshipStatus.CONFIRMED);
        user.getFriendships().replace(friendId, FriendshipStatus.CONFIRMED);
        log.info("Пользователь {} и пользователь {} меняют статус с PENDING на CONFIRMED", id, userFriend);
        update(userFriend);
        return update(user);
    }

    public User deleteFriend(long id, long friendId) throws ValidationException {
        log.info("Запрос на удаление друга: пользователь {} хочет удалить пользователя {} из списка друзей", id, friendId);

        User user = inMemoryUserStorage.getUser(id);
        User userFriend = inMemoryUserStorage.getUser(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        if (!user.getFriendships().containsKey(friendId)) {
            log.debug("Пользователи {} и {} не являются друзьями, удалять нечего", id, friendId);
            return user;
        }
        log.debug("Валидация пользователя {}", id);
        validateUser(user);
        log.debug("Валидация пользователя {}", friendId);
        validateUser(userFriend);

        log.info("Удаление друга {} у пользователя {}", friendId, id);
        user.getFriendships().remove(friendId);
        userFriend.getFriendships().remove(id);

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
        return user.getFriendships().keySet().stream()
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
