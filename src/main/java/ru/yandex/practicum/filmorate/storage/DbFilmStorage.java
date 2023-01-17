package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class DbFilmStorage implements FilmStorage {
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
            statement.setString(5, film.getMpa().toString());
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
        String sql = String.format(
                "UPDATE films SET FILM_NAME = '%s', "
                        + "DESCRIPTION = '%s', "
                        + "RELEASE_DATE = '%s', "
                        + "DURATION = '%s' "
                        + "GENRE_ID = '%s', "
                        + "RATING_ID = '%s' "
                        + "WHERE film_id = '%s';",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getGenre().toString(), film.getId()
        );
        jdbcTemplate.update(sql);
        return film;
    }

    @Override
    public HashMap<java.lang.Integer, Film> getFilms() {
        HashMap<java.lang.Integer, Film> foundFilms = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM PUBLIC.films f JOIN PUBLIC.rating r ON f.RATING_ID = r.RATING_ID"
        );

        while (filmRows.next()) {
            Film film = mapFilm(filmRows);
            foundFilms.put(film.getId(), film);
            filmRows.next();
        }
        return foundFilms;
    }

    private Film mapFilm(SqlRowSet filmRows) {
        Film film = new Film();
        film.setId(java.lang.Integer.parseInt(filmRows.getString("FILM_ID")));
        film.setName(filmRows.getString("FILM_NAME"));
        film.setDescription(filmRows.getString("DESCRIPTION"));
        film.setReleaseDate(LocalDate.from(LocalDate.parse(filmRows.getString("RELEASE_DATE"))));
        film.setDuration(java.lang.Integer.parseInt(filmRows.getString("DURATION")));
        film.setMpa(Integer.valueOf(filmRows.getString("RATING_NAME")));
        film.setGenre(findGenresByFilmId(film.getId()));
        film.setLikes(findLikesByFilmId(film.getId()));
        return film;
    }

    private Set<Genre> findGenresByFilmId(java.lang.Integer filmId) {
        Set<Genre> genres = new HashSet<>();
        String sqlQuery = String.format(
                "SELECT genre_id FROM films_genres f " +
                "JOIN public.genres g ON g.genre_id = f.genre_id " +
                "WHERE film_id = %d;", filmId
        );
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (genresRows.next()) {
            genres.add(Genre.valueOf(genresRows.getString("genre_name")));
        }
        return genres;
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
