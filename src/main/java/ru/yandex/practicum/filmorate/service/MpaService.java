package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.InMemoryMpaStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MpaService {

    private final InMemoryMpaStorage inMemoryMpaStorage;

    public List<Mpa> getAllMpa() {
        log.info("Получение всех рейтингов MPA");
        return inMemoryMpaStorage.getAllMpa();
    }

    public Mpa getMpaOnId(int id) {
        log.info("Получение всех рейтингов по id {}", id);
        if(!(inMemoryMpaStorage.getAllIds().contains(id))){
            throw new NotFoundException("Такого id нет");
        }
        return inMemoryMpaStorage.getMpaOnId(id)
                .orElseThrow(() -> {
                    log.warn("Рейтинг MPA с id {} не найден ", id);
                    return new NotFoundException("Рейтинг MPA с id " + id + " {} не найден ");
                });
    }

}
