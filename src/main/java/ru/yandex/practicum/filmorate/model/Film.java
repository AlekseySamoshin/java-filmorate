package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

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
