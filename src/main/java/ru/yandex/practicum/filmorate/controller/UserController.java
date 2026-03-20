package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        log.debug("Получен запрос на создание фильма - {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) throws ValidationException {
        log.debug("Получен запрос на обновление фильма - {}", newUser);
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        log.debug("Получен запрос на добавление в друзья");
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        log.debug("Получен запрос на удаление из друзей");
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriendsList(@PathVariable long id) throws ValidationException {
        log.debug("Получен зарос на получение списка друзей");
        return userService.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) throws ValidationException {
        log.debug("Получен запрос на получение списка общих друзей");
        return userService.getCommonFriends(id, otherId);
    }
}
