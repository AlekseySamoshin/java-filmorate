package ru.yandex.practicum.filmorate.storage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.Set;

@Component
@Primary
@Slf4j
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
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
        synchronizeFriends(user);
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
        return findFriendsByManyUserIds(foundUsers);
    }

    public User findUserById(Integer id) {
        User user;
        String sqlQuery = String.format("SELECT * FROM PUBLIC.users WHERE user_id = %d", id);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        if (userRows.next()) {
            user = (mapUser(userRows));
        } else {
            throw new NotFoundException("Пользователь с id " + id + "не найден");
        }
        user.setFriends(findFriendsByUserId(user.getId()));
        return user;
    }

    private Set<Integer> findFriendsByUserId(Integer userId) {
        Set<Integer> friends = new HashSet<>();
        String sqlQuery = String.format("SELECT friend_id FROM FRIENDS f WHERE user_id = %d;", userId);
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (friendsRows.next()) {
            friends.add(Integer.parseInt(friendsRows.getString("friend_id")));
        }
        return friends;
    }

    private HashMap<Integer, User> findFriendsByManyUserIds(HashMap<Integer, User> users) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append(
                "SELECT f.user_id, f.friend_id FROM users u " +
                "JOIN friends f ON f.user_id = u.user_id "+
                "WHERE f.user_id IN ("
        );
        String prefix = "";
        for (Integer id : users.keySet()) {
            sqlQuery.append(prefix);
            sqlQuery.append(id);
            prefix = ", ";
        }
        sqlQuery.append(");");
        SqlRowSet usersFriendsRows = jdbcTemplate.queryForRowSet(sqlQuery.toString());
        while (usersFriendsRows.next()) {
            Integer userId = Integer.parseInt(usersFriendsRows.getString("user_id"));
            Integer friendId = Integer.parseInt(usersFriendsRows.getString("friend_id"));
            users.get(userId).getFriends().add(friendId);
        }
        return users;
    }

    private void addFriendsOfUserToDb(User user, Integer friendId) {
        String sqlQuery = String.format("INSERT INTO friends (user_id, friend_id) VALUES (%d, %d); ", user.getId(), friendId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка добавления пользователя id=" + friendId + " в друзья к пользователю id=" + user.getId());
        }
    }

    private void deleteFriendsFromDb(User user, Integer friendId) {
        System.out.println(user.getFriends());
        String sqlQuery = String.format("DELETE FROM friends WHERE user_id = %d AND friend_id = %d;", user.getId(), friendId);
        try {
            jdbcTemplate.execute(sqlQuery);
        } catch (DataAccessException e) {
            log.warn("Ошибка удаления пользователя id=" + friendId + " из друзей пользователя id=" + user.getId());
        }
    }

    private void synchronizeFriends(User user) {
        Set<Integer> dbFriends = findFriendsByUserId(user.getId());
        Set<Integer> newFriends = user.getFriends();
        Set<Integer> toRemove = new HashSet<>();
        Set<Integer> toAdd = new HashSet<>();
        for (Integer dbFriendId : dbFriends) {
            if (!newFriends.contains(dbFriendId)) {
                toRemove.add(dbFriendId);
            }
        }
        for (Integer newFriendId : newFriends) {
            if (!dbFriends.contains(newFriendId)) {
                toAdd.add(newFriendId);
            }
        }
        for (Integer friendId : toRemove) {
            deleteFriendsFromDb(user, friendId);
        }
        for (Integer friendId : toAdd) {
            addFriendsOfUserToDb(user, friendId);
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