package ru.yandex.practicum.filmorate.exceptions;

public class WrongFilmDataException extends RuntimeException {
    public WrongFilmDataException(String message) {
        super (message);
    }
}
