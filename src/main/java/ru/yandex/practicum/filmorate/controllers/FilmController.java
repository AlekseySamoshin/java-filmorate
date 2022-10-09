package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.WrongFilmDataException;
import ru.yandex.practicum.filmorate.exceptions.WrongUserDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {
    private int generatedId;
    private final HashMap<Integer, Film> films = new HashMap<Integer, Film>();

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен запрос на добавление фильма.");
        validate(film);
        if (film.getId() == null) film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм " + film.toString());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос на добавление/обновление данных фильма.");
        validate(film);
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Ошибка обновления данных фильма: неверный id" + film.toString());
            throw new WrongFilmDataException("Неверный id");
        }
        films.put(film.getId(), film);
        log.info("Обновлены данные фильма " + film.toString());
        return film;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms(){
        log.info("Получен запрос на получение списка фильмов");
        return films.values();
    }

    private int generateId() {
        return ++generatedId;
    }

    public void validate(Film film) throws WrongFilmDataException {
        StringBuilder message = new StringBuilder();
        if (film.getName().isBlank()) message.append("Не указано название! ");
        if (film.getDescription().length() > 200) message.append("Слишком длинное описание! ");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) message.append("Некорректрая дата выхода! ");
        if (film.getDuration() < 0) message.append("Длительность фильма не может быть меньше 0! ");
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации данных фильма: " + message.toString());
            throw new WrongFilmDataException(message.toString());
        }
    }
}
