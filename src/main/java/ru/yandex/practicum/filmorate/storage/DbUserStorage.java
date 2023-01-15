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
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        return user;
    }


    @Override
    public User deleteUser(User user) {
        String sqlQuery = String.format("DELETE FROM users WHERE user_id='%s';", user.getId());
        jdbcTemplate.update(sqlQuery);
        log.info("Удален пользователь " + user.toString());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = String.format(
                "UPDATE users SET USER_EMAIL = '%s', "
                + "USER_LOGIN = '%s', "
                + "USER_NAME = '%s', "
                + "USER_BIRTHDAY = '%s' "
                + "WHERE user_id = '%s';",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday().toString(), user.getId()
        );
        jdbcTemplate.update(sqlQuery);
        return user;
    }

    @Override
    public HashMap<Integer, User> getUsers() {
        HashMap<Integer, User> foundUsers = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM PUBLIC.users");
        while (userRows.next()) {
            User user = mapUser(userRows);
            foundUsers.put(user.getId(), user);
        }
        return foundUsers;
    }

    public User findUserById(Integer id) {
        User user = null;
        String sqlQuery = String.format("SELECT * FROM PUBLIC.users WHERE user_id = %d", id);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        if (userRows.next()) {
            user = (mapUser(userRows));
        } else {
            throw new NotFoundException("Пользователь с id " + id + "не найден");
        }
        return user;
    }

    private Set<Integer> findFriendsByUserId(Integer userId) {
        Set<Integer> friends = new HashSet<>();
        String sqlQuery = String.format("SELECT friend_id FROM FRIENDS f WHERE user_id = %d;", userId);
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (friendsRows.next()) {
            friends.add(Integer.parseInt(friendsRows.getString("FRIEND_ID")));
        }
        return friends;
    }

    private void addFriendsToUser(User user) {
        if (!user.getFriends().isEmpty()) {
            StringBuilder sqlQuery = new StringBuilder();
            for (Integer friendId : user.getFriends()) {
                sqlQuery = sqlQuery.append(
                        String.format("INSERT INTO friends (user_id, friend_id) VALUES ( %d, %d); ", user.getId(), friendId)
                );
            }
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery.toString());
        }
    }

    private User mapUser(SqlRowSet userRows) {
        User user = new User();
        user.setId(Integer.parseInt(userRows.getString("user_id")));
        user.setEmail(userRows.getString("USER_EMAIL"));
        user.setLogin(userRows.getString("USER_LOGIN"));
        user.setName(userRows.getString("USER_NAME"));
        user.setBirthday(LocalDate.from(LocalDate.parse(userRows.getString("USER_BIRTHDAY"))));
        user.setFriends(findFriendsByUserId(user.getId()));
        return user;
    }


}