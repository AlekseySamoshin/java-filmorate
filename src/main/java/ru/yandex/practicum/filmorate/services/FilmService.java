package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final static int MOST_POPULAR_DEFAULT_COUNT = 10;
    private FilmStorage filmStorage;
    private UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public HashMap<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return filmStorage.getFilms().get(id);
    }

    public void addLike(int id, int userId){
        if (!userService.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        getFilmById(id).getLikes().add(userService.getUserById(userId));
    }

    public void removeLike(int id, int userId) {
        if (!userService.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        getFilmById(id).getLikes().remove(userService.getUserById(userId));
    }

    public List<User> getLikes(int id) {
        if (!filmStorage.getFilms().containsKey(id) || filmStorage.getFilms().isEmpty()) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return getFilmById(id).getLikes();
    }

    public List<Film> getMostPopular(int count) {
        if (count == 0) count = MOST_POPULAR_DEFAULT_COUNT;
        return filmStorage.getFilms().values().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size() * (-1)))
                .limit(count)
                .collect(Collectors.toList());
    }
}
