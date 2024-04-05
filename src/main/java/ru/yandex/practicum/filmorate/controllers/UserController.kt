package ru.yandex.practicum.filmorate.controllers

import lombok.extern.slf4j.Slf4j
import org.apache.logging.slf4j.SLF4JLogger
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.yandex.practicum.filmorate.model.User
import ru.yandex.practicum.filmorate.services.UserService

@RestController
@Slf4j
class UserController @Autowired constructor(
    private val userService: UserService,
    private val logger: Logger
) {
    @PostMapping("/users")
    fun addUser(@RequestBody user: User): User {
        var user = user
        logger.info("Получен запрос на добавление пользователя.")
        userService.validate(user)
        user = userService.addUser(user)
        logger.info("Добавлен пользователь $user")
        return user
    }

    @PutMapping("/users")
    fun updateUser(@RequestBody user: User): User {
        var user = user
        logger.info("Получен запрос на обновление данных пользователя.")
        userService.validate(user)
        user = userService.updateUser(user)
        logger.info("Обновлены данные пользователя $user")
        return user
    }

    @get:GetMapping("/users")
    val users: Collection<User>
        get() {
            logger.info("Получен запрос на получение списка пользователей")
            return userService.users.values
        }

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Int): User {
        logger.info("Получен запрос на получение пользователя")
        return userService.getUserById(id)
    }

    @GetMapping("/users/{id}/friends")
    fun getFriends(@PathVariable id: Int): Collection<User> {
        return userService.getFriends(id)
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    fun getCommonFriends(@PathVariable id: Int, @PathVariable otherId: Int): Collection<User> {
        return userService.getCommonFriends(id, otherId)
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    fun addFriend(@PathVariable id: Int, @PathVariable friendId: Int) {
        userService.addFriend(id, friendId)
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    fun deleteFriend(@PathVariable id: Int, @PathVariable friendId: Int) {
        userService.deleteFriend(id, friendId)
    }
}

