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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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


//    public User updateUser(User user) {
//        if (!getUsers().containsKey(user.getId())) {
//            String message = "Пользователь с id " + user.getId() + " не найден";
//            log.warn(message + "id=" + user.getId());
//            throw new NotFoundException(message);
//        }
//        userStorage.updateUser(user);
//        return user;
//    }




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

    public void addLike(int id, int userId){
        if (!userService.getUsers().containsKey(userId)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        if (!filmStorage.getFilms().containsKey(id)) {
            String message = "Фильм с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        getFilmById(id).getLikes().add(userId);
    }

    public void removeLike(int id, int userId) {
        if (!userService.getUsers().containsKey(userId)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        if (!filmStorage.getFilms().containsKey(id)) {
            String message = "Фильм с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        getFilmById(id).getLikes().remove(userService.getUserById(userId));
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
//        if (film.getGenre() == null) message.append("Не указан жанр! ");
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации данных фильма: " + message.toString());
            throw new WrongDataException(message.toString());
        }
    }
}
