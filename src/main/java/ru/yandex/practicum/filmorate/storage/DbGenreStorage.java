package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
public class DbGenreStorage {
    Logger log = LoggerFactory.getLogger(DbFilmStorage.class);
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres(){
        List<Genre> genreList = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM PUBLIC.genres");
        while (genreRows.next()) {
            Genre genre = mapGenre(genreRows);
            genreList.add(genre);
        }
        return genreList;
    }

    public Genre mapGenre(SqlRowSet mpaRows) {
        Genre genre = new Genre();
        genre.setId(mpaRows.getInt("genre_id"));
        genre.setName(mpaRows.getString("genre_name"));
        return genre;
    }
}