package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DbMpaStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final DbMpaStorage mpaStorage;

    @Autowired
    public MpaService(DbMpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getMpa() {
        return mpaStorage.getMpa();
    }

    public Mpa getMpaById(int id) {
        List<Mpa> mpaList = getMpa();
        for (Mpa mpa : mpaList) {
            if (mpa.getId() == id) {
                return mpa;
            }
        }
        String message = "MPA с id " + id + " не найден";
        log.warn(message);
        throw new NotFoundException(message);

    }
}
