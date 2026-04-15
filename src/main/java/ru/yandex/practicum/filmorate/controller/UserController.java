package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Collection<User>> findAll() {
        Collection<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) throws ValidationException {
        log.debug("Получен запрос на создание пользователя - {}", user);
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping
    public ResponseEntity<User> update(@RequestBody User newUser) throws ValidationException {
        log.debug("Получен запрос на обновление пользователя - {}", newUser);
        User updated = userService.update(newUser);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        User user = userService.addFriend(id, friendId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> confirmFriend(@PathVariable long id, @PathVariable long friendId){
        User user = userService.confirmFriend(id, friendId);
        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        User user = userService.deleteFriend(id, friendId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getFriendsList(@PathVariable long id) throws ValidationException {
        Set<User> friends = userService.getFriendsList(id);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriends(@PathVariable long id, @PathVariable long otherId) throws ValidationException {
        Set<User> commonFriends = userService.getCommonFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }
}
