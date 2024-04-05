package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int generatedId;

    @Override
    public Film addFilm(Film film) {
        if (film.getId() == null) film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        films.remove(film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Ошибка обновления данных фильма: неверный id" + film.toString());
            throw new WrongDataException("Неверный id");
        }
        films.put(film.getId(), film);
        log.info("Обновлены данные фильма " + film.toString());
        return film;
    }

    @Override
    public HashMap<Integer, Film> getFilms() {
        return films;
    }

    private int generateId() {
        return ++generatedId;
    }
}
