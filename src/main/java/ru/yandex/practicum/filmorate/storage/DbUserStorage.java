package ru.yandex.practicum.filmorate.storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

@Component
@Primary
public class DbUserStorage implements UserStorage {
    Logger log = LoggerFactory.getLogger(DbUserStorage.class);
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO users (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setString(4, user.getBirthday().toString());
            return statement;
        }, keyHolder);
        user.setId((Integer) keyHolder.getKey());
        System.out.println(user.toString());
        return user;
    }


    @Override
    public User deleteUser(User user) {
        String sql = String.format("DELETE FROM users WHERE user_id='%s';", user.getId());
        jdbcTemplate.update(sql);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = String.format(
                "UPDATE users SET USER_EMAIL = '%s', "
                + "USER_LOGIN = '%s', "
                + "USER_NAME = '%s', "
                + "USER_BIRTHDAY = '%s' "
                + "WHERE user_id = '%s';",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday().toString(), user.getId()
        );
        jdbcTemplate.update(sql);
        return user;
    }

    @Override
    public HashMap<Integer, User> getUsers() {
        HashMap<Integer, User> foundUsers = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM PUBLIC.users");
        System.out.println("Запрос отправлен, нужен ответ");

        while (userRows.next()) {
            User user = new User();
            user.setId(Integer.parseInt(userRows.getString("USER_ID")));
            user.setEmail(userRows.getString("USER_EMAIL"));
            user.setLogin(userRows.getString("USER_LOGIN"));
            user.setName(userRows.getString("USER_NAME"));
            user.setBirthday(LocalDate.from(LocalDate.parse(userRows.getString("USER_BIRTHDAY"))));
            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());
            foundUsers.put(user.getId(), user);
            userRows.next();
        }

        return foundUsers;
    }
}
