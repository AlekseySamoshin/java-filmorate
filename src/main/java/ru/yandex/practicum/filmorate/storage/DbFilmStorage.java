package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;

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
                "FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, GENRE_ID, RATING_ID" +
                ") VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setString(3, film.getReleaseDate().toString());
            statement.setString(4, film.getDuration().toString());
            //statement.setString(5, film.getGenre());
            statement.setString(5, film.getRating().toString());
            return statement;
        }, keyHolder);
        film.setId((Integer) keyHolder.getKey());
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
    public HashMap<Integer, Film> getFilms() {
        HashMap<Integer, Film> foundFilms = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM PUBLIC.films f JOIN PUBLIC.rating r ON f.RATING_ID = r.RATING_ID"
        );

        while (filmRows.next()) {
            Film film = rowToFilm(filmRows);
            foundFilms.put(film.getId(), film);
            filmRows.next();
        }
        return foundFilms;
    }

    private Film rowToFilm(SqlRowSet filmRows) {
        Film film = new Film();
        film.setId(Integer.parseInt(filmRows.getString("FILM_ID")));
        film.setName(filmRows.getString("FILM_NAME"));
        film.setDescription(filmRows.getString("DESCRIPTION"));
        film.setReleaseDate(LocalDate.from(LocalDate.parse(filmRows.getString("RELEASE_DATE"))));
        film.setDuration(Integer.parseInt(filmRows.getString("DURATION")));
        //film.setGenre(filmRows.getString("GENRE_ID"));
        film.setRating(RatingMpa.valueOf(filmRows.getString("RATING_NAME")));
        return film;
    }
}
