package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
public class Film {
    Integer id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;

    @Override
    public String toString() {
        return "Film{id=" + id
                + ", name=" + name
                + ", release=" +  releaseDate
                + ", duration=" + duration + " мин."
                + ", descr.=" + description
                + "}";
    }
}
