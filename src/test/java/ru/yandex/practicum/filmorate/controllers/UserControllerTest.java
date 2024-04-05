package ru.yandex.practicum.filmorate.controllers;

import org.apache.logging.slf4j.SLF4JLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserControllerTest {

    private HashMap<Integer, User> users = new HashMap<>();
    private int generatedId;
    UserController userController;
    User user1;
    User user2;
    User wrongUser;
    Logger logger = mock(Logger.class);

    @BeforeEach
    void prepare() {
        userController = new UserController(new UserService(new InMemoryUserStorage(logger)), logger);
        users = new HashMap<>();
        generatedId = 0;
        user1 = new User();
        user1.setName("Иван");
        user1.setEmail("ivan@ya.ru");
        user1.setLogin("vanyator-3000");
        user1.setBirthday(LocalDate.of(2000, 1, 2));
        user2 = new User();
        user2.setName("Маша");
        user2.setEmail("mylo@shilo.ru");
        user2.setLogin("mashinistka");
        user2.setBirthday(LocalDate.of(1991, 2, 3));
        wrongUser = new User();
        wrongUser.setName("");
        wrongUser.setLogin("");
        wrongUser.setBirthday(LocalDate.of(2500, 3, 4));
    }

    @Test
    void addUser() {
        userController.addUser(user1);
        userController.addUser(user2);
        assertEquals(1, user1.getId(), "Пользователям присваивается неверный id");
        assertEquals(2, user2.getId(), "Пользователям присваивается неверный id");
        try {
            userController.addUser(wrongUser);
        } catch (WrongDataException exception) {
            assertEquals("Не указан email! Не указан логин! Некорректная дата рождения! ", exception.getMessage());
        }
    }

    @Test
    void updateUser() {
        userController.addUser(user1);
        user2.setId(1);
        userController.updateUser(user2);
        assertEquals(userController.getUsers().toArray()[0], user2, "Данные пользователей обновляются неправильно");
    }

    @Test
    void getUsers() {
        userController.addUser(user1);
        userController.addUser(user2);
        HashMap<Integer, User> collection = new HashMap<>();
        collection.put(1, user1);
        collection.put(2, user2);
        assertEquals(userController.getUsers().toArray()[0], collection.values().toArray()[0], "Список пользователей получен неправильно");
    }

    @Test
    void validate() {
        try {
            userController.addUser(wrongUser);
        } catch (WrongDataException exception) {
            assertEquals("Не указан email! Не указан логин! Некорректная дата рождения! ", exception.getMessage());
        }
    }
}