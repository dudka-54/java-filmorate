package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId){
        return userService.addFriend(id, friendId);
    }
}
