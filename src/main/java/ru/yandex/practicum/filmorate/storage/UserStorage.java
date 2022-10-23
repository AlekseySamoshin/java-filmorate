package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface UserStorage {
    public User addUser(User user);
    public User deleteUser(User user);
    public User updateUser(User user);
    public HashMap<Integer, User> getUsers();
}
