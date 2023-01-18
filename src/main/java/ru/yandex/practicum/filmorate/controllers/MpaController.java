package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.MpaService;

import java.util.Collection;

@RestController
@Slf4j
public class MpaController {
    MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public Collection<Mpa> getMpa(){
        log.info("Получен запрос на получение списка возрастных рейтингов");
        return mpaService.getMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa mpa(@PathVariable int id) {
        log.info("Получен запрос на получение возрастного рейтинга");
        return mpaService.getMpaById(id);
    }
}
