package ru.yandex.practicum.filmorate.exceptions;

public class WrongUserDataException extends RuntimeException {

    public WrongUserDataException(String message) {
        super (message);
    }
}
