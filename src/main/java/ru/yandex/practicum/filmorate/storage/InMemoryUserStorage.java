package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        user.setId(getNextId());
        log.trace("Устанавливаем новое значение поля id {}", user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) throws ValidationException {
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("Пользователь с id={} не найден", newUser.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        log.debug("Обновление пользователя id={}:", oldUser.getId());
        log.debug("  email: {} → {}", oldUser.getEmail(), newUser.getEmail());
        log.debug("  login: {} → {}", oldUser.getLogin(), newUser.getLogin());
        log.debug("  name: {} → {}", oldUser.getName(), newUser.getName());
        log.debug("  birthday: {} → {}", oldUser.getBirthday(), newUser.getBirthday());
        return oldUser;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    private long getNextId() {
        log.debug("Вызван метод getNextId для получения следующего id");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Текущее максимальное значение id - {}", currentMaxId);
        log.debug("Увеличиваем значение id перед использованием текущего значения в выражении");
        return ++currentMaxId;
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    //Не писал реализацию данных методов так как InMemoryUserStorage рудимент для показательной работы @Qualifier

    @Override
    public void addFriend(long id, long friendId) {

    }

    @Override
    public void deleteFriend(long id, long friendId) {

    }
}
