package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.user.UserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;

public final class UserMapper {
    public static User mapToUser(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setBirthday(request.getBirthday());
        user.setFriends(new HashSet<>());
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        dto.setBirthday(user.getBirthday());
        dto.setFriends(user.getFriends());
        dto.setId(user.getId());
        return dto;

    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }
        return user;
    }

}
