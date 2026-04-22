package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    public MpaService(@Qualifier("MpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        log.info("Получение всех рейтингов MPA");
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaOnId(Integer id) {
        log.info("Получение всех рейтингов по id {}", id);
        if(!(mpaStorage.getAllIds().contains(id))){
            throw new NotFoundException("Такого id нет");
        }
        return mpaStorage.getMpaOnId(id)
                .orElseThrow(() -> {
                    log.warn("Рейтинг MPA с id {} не найден ", id);
                    return new NotFoundException("Рейтинг MPA с id " + id + " {} не найден ");
                });
    }

}
