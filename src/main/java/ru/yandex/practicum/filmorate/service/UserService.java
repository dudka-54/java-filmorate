package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
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


    public UserDto create(UserRequest userRequest) throws ValidationException {
        User newUser = UserMapper.mapToUser(userRequest);
        validateUser(newUser);
        newUser = userStorage.save(newUser);
        log.info("Пользователь успешно добавлен - {}", newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    public UserDto update(UpdateUserRequest updateUserRequest) throws ValidationException {
        User updatedUser = userStorage.getUser(updateUserRequest.getId())
                .map(user -> UserMapper.updateUserFields(user, updateUserRequest))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        validateUser(updatedUser);
        userStorage.update(updatedUser);

        updatedUser = userStorage.getUser(updatedUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден после обновления"));

        log.info("Пользователь успешно обновлён - {}", updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }


    public Collection<UserDto> findAll() {
        log.info("Использован метод по получению всех пользователей");
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addFriend(long id, long friendId) throws ValidationException {
        log.info("Запрос на добавление друга: пользователь {} хочет добавить друга {}", id, friendId);

        User user = userStorage.getUser(id).orElseThrow(() -> new NotFoundException("Пользователь не найден с id " + id));
        User userFriend = userStorage.getUser(friendId).orElseThrow(() -> new NotFoundException("Пользователь не найден с id " + friendId));

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

        User updatedUser = userStorage.update(user);
        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public UserDto deleteFriend(long id, long friendId) throws ValidationException {
        log.info("Запрос на удаление друга: пользователь {} хочет удалить пользователя {} из списка друзей", id, friendId);

        User user = userStorage.getUser(id).orElseThrow(() -> new NotFoundException("Пользователь не найден с id " + id));
        User userFriend = userStorage.getUser(friendId).orElseThrow(() -> new NotFoundException("Пользователь не найден с id " + id));

        if (!user.getFriends().contains(friendId)) {
            log.debug("Пользователи {} и {} не являются друзьями, удалять нечего", id, friendId);
            return UserMapper.mapToUserDto(user);
        }
        validateUser(user);
        validateUser(userFriend);

        user.getFriends().remove(friendId);

        userStorage.deleteFriend(id, friendId);

        userStorage.update(userFriend);
        User updatedUser = userStorage.update(user);

        log.info("Друг {} успешно удален у пользователя {}", friendId, id);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public Set<UserDto> getFriendsSet(long id) throws ValidationException {
        log.info("Вызван метод на получение множества друзей id - {}", id);
        User user = userStorage.getUser(id).orElseThrow(() -> new NotFoundException("Пользователь не найден с id " + id));

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
    }

    public Set<UserDto> getCommonFriendsSet(long id, long otherId) {
        log.info("метод получения множества общих друзей {} и {}", id, otherId);
        Set<UserDto> friends1 = getFriendsSet(id);
        Set<UserDto> friends2 = getFriendsSet(otherId);

        friends1.retainAll(friends2);
        return friends1;
    }
}
