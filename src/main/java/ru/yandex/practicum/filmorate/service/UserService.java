package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
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
        validateUser(newUser);
        User user = inMemoryUserStorage.update(newUser);
        log.debug("Создаем объект копию User обновляемого пользователя");
        log.info("Пользователь успешно обновлен - {}", newUser);
        return user;
    }

    private void validateUser(User user) throws ValidationException {
        try {
            if (user.getId() == null) {
                throw new ValidationException("ID пользователя должен быть указан");
            }
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Email не должен быть пустым или содержать только пробелы");
            }
            if (!(user.getEmail().contains(String.valueOf('@')))) {
                throw new ValidationException("Email должен содержать символ - @ ");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
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
        return inMemoryUserStorage.findAll();
    }

    public User addFriend(long id, long friendId) throws ValidationException {
        User user = inMemoryUserStorage.getUser(id);
        User userFriend = inMemoryUserStorage.getUser(friendId);
        validateUser(user);
        validateUser(userFriend);
        user.getFriendsId().add(friendId);
        userFriend.getFriendsId().add(id);
        update(userFriend);
        return update(user);
    }

    public User deleteFriend(long id, long friendId) throws ValidationException {
        User user = inMemoryUserStorage.getUser(id);
        User userFriend = inMemoryUserStorage.getUser(friendId);
        validateUser(user);
        validateUser(userFriend);
        user.getFriendsId().remove(friendId);
        userFriend.getFriendsId().remove(id);
        update(userFriend);
        return update(user);
    }

    public Set<User> getFriendsList(long id) throws ValidationException {
        User user = inMemoryUserStorage.getUser(id);
        validateUser(user);
        return user.getFriendsId().stream()
                .map(id1 -> inMemoryUserStorage.getUser(id1))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
