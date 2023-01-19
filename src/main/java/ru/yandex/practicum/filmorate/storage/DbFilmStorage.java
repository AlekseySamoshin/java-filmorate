package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@Slf4j
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DbMpaStorage mpaStorage;
    private final DbGenreStorage genreStorage;

    public DbFilmStorage(JdbcTemplate jdbcTemplate, DbMpaStorage mpaStorage, DbGenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }
    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (" +
                "FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID" +
                ") VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setString(3, film.getReleaseDate().toString());
            statement.setString(4, film.getDuration().toString());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        film.setId((java.lang.Integer) keyHolder.getKey());
        synchronizeGenres(film);
        synchronizeLikes(film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        String sql = String.format("DELETE FROM films WHERE film_id='%s';", film.getId());
        jdbcTemplate.update(sql);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = String.format(
                "UPDATE films SET FILM_NAME = '%s', "
                        + "RELEASE_DATE = '%s', "
                        + "DESCRIPTION = '%s', "
                        + "DURATION = %d, "
                        + "RATING_ID = %d "
                        + "WHERE film_id = %d;",
                film.getName(), film.getReleaseDate().toString(), film.getDescription(), film.getDuration(), film.getMpa().getId(), film.getId()
        );
        jdbcTemplate.update(sqlQuery);
        synchronizeGenres(film);
        synchronizeLikes(film);
        film.setGenres(findGenresByFilmId(film.getId()));
        film.setLikes(findLikesByFilmId(film.getId()));
        return film;
    }

    @Override
    public HashMap<Integer, Film> getFilms() {
        HashMap<Integer, Film> foundFilms = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM films f " +
                        "JOIN rating r ON f.RATING_ID = r.RATING_ID"
        );
        while (filmRows.next()) {
            Film film = mapFilm(filmRows);
            foundFilms.put(film.getId(), film);
        }
        return foundFilms;
    }

    private Film mapFilm(SqlRowSet filmRows) {
        Mpa mpa = mpaStorage.mapMpa(filmRows);
        Film film = new Film();
        film.setId(java.lang.Integer.parseInt(filmRows.getString("film_id")));
        film.setName(filmRows.getString("film_name"));
        film.setDescription(filmRows.getString("description"));
        film.setReleaseDate(LocalDate.from(LocalDate.parse(filmRows.getString("release_date"))));
        film.setDuration(java.lang.Integer.parseInt(filmRows.getString("duration")));
        film.setMpa(mpa);
        film.setGenres(findGenresByFilmId(film.getId()));
        film.setLikes(findLikesByFilmId(film.getId()));
        return film;
    }

    private Set<Genre> findGenresByFilmId(Integer filmId) {
        Set<Genre> genres = new LinkedHashSet<>();
        String sqlQuery = String.format(
                "SELECT f.genre_id, genre_name FROM films_genres f " +
                "JOIN genres g ON g.genre_id = f.genre_id " +
                "WHERE film_id = %d;", filmId
        );
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(sqlQuery);
        genresRows.beforeFirst();
        while (genresRows.next()) {
            genres.add(genreStorage.mapGenre(genresRows));
        }
        return genres;
    }

    private void synchronizeGenres(Film film) {
        Set<Genre> oldGenres = findGenresByFilmId(film.getId());
        Set<Genre> newGenres = film.getGenres();
        Set<Genre> toRemove = new LinkedHashSet<>();
        Set<Genre> toAdd = new LinkedHashSet<>();
        for (Genre dbGenre : oldGenres) {
            if (!newGenres.contains(dbGenre)) {
                toRemove.add(dbGenre);
            }
        }
        for (Genre newGenre : newGenres) {
            if (!oldGenres.contains(newGenre)) {
                toAdd.add(newGenre);
            }
        }
        for (Genre genre : toRemove) {
            deleteGenreOfFilmFromDb(film, genre.getId());
        }
        for (Genre genre : toAdd) {
            addGenreOfFilmToDb(film, genre.getId());
        }
    }

    private void synchronizeLikes(Film film) {
        List<Integer> dbLikes = findLikesByFilmId(film.getId());
        List<Integer> newLikes = film.getLikes();
        List<Integer> toRemove = new ArrayList<>();
        List<Integer> toAdd = new ArrayList<>();
        for (Integer userId : dbLikes) {
            if (!newLikes.contains(userId)) {
                toRemove.add(userId);
            }
        }
        for (Integer userId : newLikes) {
            if (!dbLikes.contains(userId)) {
                toAdd.add(userId);
            }
        }
        for (Integer userId : toRemove) {
            deleteLikesFromDb(film, userId);
        }
        for (Integer userId : toAdd) {
            addLikesOfFilmToDb(film, userId);
        }
    }

    private void addGenreOfFilmToDb(Film film, Integer genreId) {
        String sqlQuery = String.format("INSERT INTO films_genres (film_id, genre_id) VALUES (%d, %d); ", film.getId(), genreId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка добавления фильму=" + film.getId() + " жанра id=" + genreId);
        }
    }

    private void deleteGenreOfFilmFromDb(Film film, Integer genreId) {
        String sqlQuery = String.format("DELETE FROM films_genres WHERE film_id = %d AND genre_id = %d;", film.getId(), genreId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка удаления жанра id=" + genreId + " из фильма id=" + film.getId());
        }
    }

    private void addLikesOfFilmToDb(Film film, Integer userId) {
        String sqlQuery = String.format("INSERT INTO likes (film_id, user_id) VALUES (%d, %d); ", film.getId(), userId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка добавления фильму=" + film.getId() + " лайка от пользователя id=" + userId);
        }
    }

    private void deleteLikesFromDb(Film film, Integer userId) {
        String sqlQuery = String.format("DELETE FROM likes WHERE film_id = %d AND user_id = %d;", film.getId(), userId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка удаления лайка от пользователя id=" + userId + " из фильма id=" + film.getId());
        }
    }

    private List<java.lang.Integer> findLikesByFilmId(java.lang.Integer id) {
        List<java.lang.Integer> likes = new ArrayList<>();
        String sqlQuery = String.format("SELECT user_id FROM likes WHERE film_id = %d;", id);
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (genresRows.next()) {
            likes.add(genresRows.getInt("user_id"));
        }
        return likes;
    }
}
