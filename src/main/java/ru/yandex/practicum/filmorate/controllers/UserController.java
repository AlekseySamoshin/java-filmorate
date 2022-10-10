package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int generatedId;

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        log.info("Получен запрос на добавление пользователя.");
        validate(user);
        if (user.getId() == null) user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь " + user.toString());
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос на обновление данных пользователя.");
        validate(user);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Ошибка обновления пользователя: неверный id" + user.toString());
            throw new WrongDataException("Неверный id");
        }
        users.put(user.getId(), user);
        log.info("Обновлены данные пользователя " + user.toString());
        return user;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return users.values();
    }

    private int generateId() {
        return ++generatedId;
    }

    private void validate(User user) throws WrongDataException {
        StringBuilder message = new StringBuilder();
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) message.append("Не указан email! ");
        if (user.getLogin().isBlank()) message.append("Не указан логин! ");
        if (user.getBirthday().isAfter(LocalDate.now())) message.append("Некорректная дата рождения! ");
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации данных пользователя: " + message.toString());
            throw new WrongDataException(message.toString());
        }
        if (user.getName() == null) user.setName(user.getLogin());
    }
}

