package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class DbMpaStorage {
    Logger log = LoggerFactory.getLogger(DbFilmStorage.class);
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getMpa(){
        List<Mpa> mpaList = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM PUBLIC.rating");
        while (mpaRows.next()) {
            Mpa mpa = mapMpa(mpaRows);
            mpaList.add(mpa);
        }
        return mpaList;
    }

    public Mpa mapMpa(SqlRowSet mpaRows) {
        Mpa mpa = new Mpa();
        mpa.setId(mpaRows.getInt("rating_id"));
        mpa.setName(mpaRows.getString("rating_name"));
        return mpa;
    }
}
