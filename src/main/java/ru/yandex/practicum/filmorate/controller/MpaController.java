package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {

    private MpaService mpaService;

    @GetMapping
    public ResponseEntity<List<Mpa>> getAllMpa() {
        List<Mpa> mpaList = mpaService.getAllMpa();
        return ResponseEntity.ok(mpaList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mpa> getMpaOnId(@PathVariable int id) {
        Mpa mpa = mpaService.getMpaOnId(id);
        return ResponseEntity.ok(mpa);
    }
}
