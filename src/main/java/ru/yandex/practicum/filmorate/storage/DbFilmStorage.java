package ru.yandex.practicum.filmorate.storage;

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
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class DbFilmStorage implements FilmStorage {
    Logger log = LoggerFactory.getLogger(DbFilmStorage.class);
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        return film;
    }

//    public User updateUser(User user) {
//        String sqlQuery = String.format(
//                "UPDATE users SET USER_EMAIL = '%s', "
//                        + "USER_LOGIN = '%s', "
//                        + "USER_NAME = '%s', "
//                        + "USER_BIRTHDAY = '%s' "
//                        + "WHERE user_id = '%s';",
//                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday().toString(), user.getId()
//        );
//        jdbcTemplate.update(sqlQuery);
//        synchronizeFriends(user);
//        return user;
//    }






    @Override
    public HashMap<Integer, Film> getFilms() {
        HashMap<Integer, Film> foundFilms = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM PUBLIC.films f JOIN PUBLIC.rating r ON f.RATING_ID = r.RATING_ID"
        );
        while (filmRows.next()) {
            Film film = mapFilm(filmRows);
            foundFilms.put(film.getId(), film);
        }
        return foundFilms;
    }

    private Film mapFilm(SqlRowSet filmRows) {
        Mpa mpa = mapMpa(filmRows);
        Film film = new Film();
        film.setId(java.lang.Integer.parseInt(filmRows.getString("FILM_ID")));
        film.setName(filmRows.getString("FILM_NAME"));
        film.setDescription(filmRows.getString("DESCRIPTION"));
        film.setReleaseDate(LocalDate.from(LocalDate.parse(filmRows.getString("RELEASE_DATE"))));
        film.setDuration(java.lang.Integer.parseInt(filmRows.getString("DURATION")));
        film.setMpa(mpa);
        film.setGenres(findGenresByFilmId(film.getId()));
        film.setLikes(findLikesByFilmId(film.getId()));
        return film;
    }

    private Mpa mapMpa(SqlRowSet mpaRows) {
        Mpa mpa = new Mpa();
        mpa.setId(mpaRows.getInt("rating_id"));
        mpa.setName(mpaRows.getString("rating_name"));
        return mpa;
    }

    private Set<Integer> findGenresByFilmId(Integer filmId) {
        Set<Integer> genres = new HashSet<>();
        String sqlQuery = String.format(
                "SELECT f.genre_id FROM films_genres f " +
                "JOIN public.genres g ON g.genre_id = f.genre_id " +
                "WHERE film_id = %d;", filmId
        );
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (genresRows.next()) {
            genres.add(genresRows.getInt("genre_id"));
        }
        return genres;
    }

    private void synchronizeGenres(Film film) {
//        if (film.getGenre() == null) {
//            return;
//        }
        Set<Integer> dbGenres = findGenresByFilmId(film.getId());
        Set<Integer> newGenres = film.getGenres();
        Set<Integer> toRemove = new HashSet<>();
        Set<Integer> toAdd = new HashSet<>();
        for (Integer dbGenre : dbGenres) {
            if (!newGenres.contains(dbGenre)) {
                toRemove.add(dbGenre);
            }
        }
        for (Integer newGenre : newGenres) {
            if (!dbGenres.contains(newGenre)) {
                toAdd.add(newGenre);
            }
        }
        for (Integer friendId : toRemove) {
            deleteGenresFromDb(film, friendId);
        }
        for (Integer friendId : toAdd) {
            addGenresOfFilmToDb(film, friendId);
        }
    }

    private void addGenresOfFilmToDb(Film film, Integer genreId) {
        String sqlQuery = String.format("INSERT INTO films_genres (user_id, genre_id) VALUES (%d, %d); ", film.getId(), genreId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка добавления фильму=" + film.getId() + " жанра id=" + genreId);
        }
    }

    private void deleteGenresFromDb(Film film, Integer genreId) {
        System.out.println(film.getGenres());
        String sqlQuery = String.format("DELETE FROM films_genres WHERE film_id = %d AND genre_id = %d;", film.getId(), genreId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка удаления жанра id=" + genreId + " из фильма id=" + film.getId());
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
