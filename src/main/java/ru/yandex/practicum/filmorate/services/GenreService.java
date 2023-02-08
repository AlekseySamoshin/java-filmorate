package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DbGenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final DbGenreStorage genreStorage;

    @Autowired
    public GenreService(DbGenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        List<Genre> genres = getGenres();
        for (Genre genre : genres) {
            if (genre.getId() == id) {
                return genre;
            }
        }
            String message = "Жанр с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
    }
}
