package ru.yandex.practicum.filmorate.exceptions;

public class WrongDataException extends RuntimeException {
    public WrongDataException(String message) {
        super (message);
    }
}
