package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
            log.warn(message);
            throw new NotFoundException(message);
        }
        userStorage.updateUser(user);
        return user;
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return userStorage.getUsers().get(id);
    }

    public void addFriend(int id, int friendId) {
        if (!getUsers().containsKey(id) || !getUsers().containsKey(friendId)) {
            String message = "Пользователь не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        getUserById(id).getFriends().add(getUserById(friendId).getId());
        getUserById(friendId).getFriends().add(getUserById(id).getId());
        log.info("Пользователь id=" + friendId + " добавлен в друзья пользователю " + id);
    }

    public void deleteFriend(int id, int friendId) {
        if (!getUsers().containsKey(id) || !getUsers().containsKey(friendId)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        if(!getFriends(id).contains(getUserById(friendId))) {
            String message = "Пользователь id=" + friendId + " не найден среди друзей пользователя id=" + id;
            log.warn(message);
            throw new NotFoundException(message);
        }
        getUserById(id).getFriends().remove(getUserById(friendId));
        getUserById(friendId).getFriends().remove(getUserById(id));
    }

    public List<User> getFriends(int id) {
        if (!userStorage.getUsers().containsKey(id) || userStorage.getUsers().isEmpty()) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return getUserById(id).getFriends().stream()
                .map((userId) -> getUserById(userId))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        if (!getUsers().containsKey(id) || !getUsers().containsKey(otherId)) {
            String message = "Пользователь с id " + id + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return getFriends(id).stream()
                .filter(getFriends(otherId) :: contains)
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
