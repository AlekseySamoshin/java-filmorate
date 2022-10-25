package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms(){
        log.info("Получен запрос на получение списка фильмов");
        return filmService.getFilms().values();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable int id){
        log.info("Получен запрос на получение фильма");
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "0") int count) {
        return filmService.getMostPopular(count);
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен запрос на добавление фильма.");
        filmService.validate(film);
        film = filmService.addFilm(film);
        log.info("Добавлен фильм " + film.toString());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос на обновление данных фильма.");
        if (!filmService.getFilms().containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        filmService.validate(film);
        film = filmService.updateFilm(film);
        log.info("Обновлены данные фильма " + film.toString());
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }
}
