package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@Slf4j
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        log.info("Получен запрос на добавление пользователя.");
        validate(user);
        user = userService.addUser(user);
        log.info("Добавлен пользователь " + user.toString());
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос на обновление данных пользователя.");
        validate(user);
        user = userService.updateUser(user);
        log.info("Обновлены данные пользователя " + user.toString());
        return user;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return userService.getUsers().values();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Получен запрос на получение пользователя");
        return userService.getUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    private void validate(User user) throws WrongDataException {
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

