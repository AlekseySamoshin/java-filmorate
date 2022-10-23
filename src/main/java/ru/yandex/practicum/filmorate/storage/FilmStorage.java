package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {
    public Film addFilm(Film film);
    public Film deleteFilm(Film film);
    public Film updateFilm(Film film);
    public HashMap<Integer, Film> getFilms();
}
