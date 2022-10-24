package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private int generatedId;
    private HashMap<Integer, Film> films;
    FilmController filmController;
    Film film1;
    Film film2;
    Film wrongFilm;

    @BeforeEach
    void prepare() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new UserService(new InMemoryUserStorage())));
        films = new HashMap<>();
        generatedId = 0;
        film1 = new Film();
        film1.setName("Terminator");
        film1.setDescription("I'll be back!");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(1984, 10, 26));
        film2 = new Film();
        film2.setName("Titanic");
        film2.setDescription("One more way to find Nemo");
        film2.setDuration(195);
        film2.setReleaseDate(LocalDate.of(1997, 11, 1));
        wrongFilm = new Film();
        wrongFilm.setName("");
        wrongFilm.setDescription("");
        wrongFilm.setDuration(-1);
        wrongFilm.setReleaseDate(LocalDate.of(1800, 1, 1));
    }

    @Test
    void addFilm() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        assertEquals(1, film1.getId(), "Фильмам присваивается неверный id");
        assertEquals(2, film2.getId(), "Фильмам присваивается неверный id");
        try {
            filmController.addFilm(wrongFilm);
        } catch (WrongDataException exception) {
            assertEquals("Не указано название! Некорректрая дата выхода! Длительность фильма не может быть меньше 0! ", exception.getMessage());
        }
    }

    @Test
    void updateFilm() {
        filmController.addFilm(film1);
        film2.setId(1);
        filmController.updateFilm(film2);
        assertEquals(filmController.getFilms().toArray()[0], film2, "Фильмы обновляются неправильно");
    }

    @Test
    void getFilms() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        HashMap<Integer, Film> collection = new HashMap<>();
        collection.put(1, film1);
        collection.put(2, film2);
        assertEquals(filmController.getFilms().toArray()[0], collection.values().toArray()[0], "Список фильмов получен неправильно");

    }

    @Test
    void validate() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i <= 200; i++) {
            str.append("a");
        }
        wrongFilm.setDescription(str.toString());
        try {
            filmController.addFilm(wrongFilm);
        } catch (WrongDataException exception) {
            assertEquals("Не указано название! Слишком длинное описание! Некорректрая дата выхода! Длительность фильма не может быть меньше 0! ", exception.getMessage());
        }
    }
}