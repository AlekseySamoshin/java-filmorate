package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final static int MOST_POPULAR_DEFAULT_COUNT = 10;
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        validate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!getFilms().containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public HashMap<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            String message = "Фильм с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return filmStorage.getFilms().get(id);
    }

    public void addLike(int filmId, int userId){
        Map<Integer, Film> films = getFilms();
        Map<Integer, User> users = userService.getUsers();
        if (!users.containsKey(userId)) {
            String message = "Пользователь с id " + userId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        if (!films.containsKey(filmId)) {
            String message = "Фильм с id " + filmId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        Film film = films.get(filmId);
        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
        }
        updateFilm(film);
    }

    public void removeLike(int filmId, int userId) {
        Map<Integer, Film> films = getFilms();
        Map<Integer, User> users = userService.getUsers();
        if (!users.containsKey(userId)) {
            String message = "Пользователь с id " + userId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        if (!films.containsKey(filmId)) {
            String message = "Фильм с id " + filmId + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }

        Film film = films.get(userId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove((Integer)userId);
        }
        updateFilm(films.get(userId));
    }

    public List<Integer> getLikes(int id) {
        if (!filmStorage.getFilms().containsKey(id) || filmStorage.getFilms().isEmpty()) {
            String message = "Фильм с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
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

    public void validate(Film film) throws WrongDataException {
        StringBuilder message = new StringBuilder();
        if (film.getName().isBlank()) message.append("Не указано название! ");
        if (film.getDescription().length() > 200) message.append("Слишком длинное описание! ");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) message.append("Некорректрая дата выхода! ");
        if (film.getDuration() < 0) message.append("Длительность фильма не может быть меньше 0! ");
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации данных фильма: " + message.toString());
            throw new WrongDataException(message.toString());
        }
    }
}
