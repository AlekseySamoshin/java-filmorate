package ru.yandex.practicum.filmorate.controllers

import lombok.extern.slf4j.Slf4j
import org.apache.logging.slf4j.SLF4JLogger
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.yandex.practicum.filmorate.exceptions.NotFoundException
import ru.yandex.practicum.filmorate.exceptions.WrongDataException

@RestControllerAdvice
@Slf4j
class ErrorHandler(
    private val logger: Logger
) {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleWrongData(e: WrongDataException): Map<String, String?> {
        logError(e.message)
        return java.util.Map.of("Ошибка: ", e.message)
    }

    private fun logError(message: String?) {
        logger.error("Ошибка: $message")
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(e: NotFoundException): Map<String, String?> {
        logError(e.message)
        return java.util.Map.of("Ошибка: ", e.message)
    }
}
