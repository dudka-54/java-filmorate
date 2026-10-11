package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> findAll() {
        Collection<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserRequest userRequest) throws ValidationException {
        log.debug("Получен запрос на создание пользователя - {}", userRequest);
        UserDto created = userService.create(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping
    public ResponseEntity<UserDto> update(@RequestBody UpdateUserRequest newUserRequest) throws ValidationException {
        log.debug("Получен запрос на обновление пользователя - {}", newUserRequest);
        UserDto updated = userService.update(newUserRequest);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<UserDto> addFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        UserDto user = userService.addFriend(id, friendId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<UserDto> deleteFriend(@PathVariable long id, @PathVariable long friendId) throws ValidationException {
        UserDto user = userService.deleteFriend(id, friendId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<UserDto>> getFriendsList(@PathVariable long id) throws ValidationException {
        Set<UserDto> friends = userService.getFriendsSet(id);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<UserDto>> getCommonFriends(@PathVariable long id, @PathVariable long otherId) throws ValidationException {
        Set<UserDto> commonFriends = userService.getCommonFriendsSet(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }
}
