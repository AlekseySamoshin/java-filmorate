package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        userStorage.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        if (!getUsers().containsKey(user.getId())) {
            String message = "Пользователь с id " + user.getId() + " не найден";
            log.warn(message + "id=" + user.getId());
            throw new NotFoundException(message);
        }
        userStorage.updateUser(user);
        return user;
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        Map<Integer, User> users = userStorage.getUsers();
        if (!users.containsKey(id)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return users.get(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        Map<Integer, User> users = getUsers();
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            String message = "Пользователь не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        User user = users.get(id);
        user.getFriends().add(friendId);
        log.info("Пользователь id=" + friendId + " добавлен в друзья пользователю id=" + id);
        updateUser(user);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        Map<Integer, User> users = getUsers();
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        User user = users.get(id);
        if(!user.getFriends().contains(friendId)) {
            String message = "Пользователь id=" + friendId + " не найден среди друзей пользователя id=" + id;
            log.warn(message);
            throw new NotFoundException(message);
        }
        user.getFriends().remove(friendId);
        updateUser(user);
    }

    public List<User> getFriends(Integer id) {
        Map<Integer, User> users = getUsers();
        if (!users.containsKey(id) || users.isEmpty()) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return users.get(id).getFriends().stream()
                .map((userId) -> users.get(userId))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        Map<Integer, User> users = getUsers();
        if (!users.containsKey(id) || !users.containsKey(otherId)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return users.get(id).getFriends().stream()
                .filter(users.get(otherId).getFriends() :: contains)
                .map((userId) -> users.get(userId))
                .collect(Collectors.toList());
    }

    public void validate(User user) throws WrongDataException {
        StringBuilder message = new StringBuilder();
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@"))
            message.append("Не указан email! ");
        if (user.getLogin().isBlank()) message.append("Не указан логин! ");
        if (user.getBirthday().isAfter(LocalDate.now())) message.append("Некорректная дата рождения! ");
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации данных пользователя: " + message.toString());
            throw new WrongDataException(message.toString());
        }
        if (user.getName().isBlank()) user.setName(user.getLogin());
    }
}
