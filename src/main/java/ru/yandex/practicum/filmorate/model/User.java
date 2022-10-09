package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Getter
@Setter
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @Override
    public String toString() {
        return "User{id=" + id
                + ", login=" + login
                + ", name=" + name
                + ", email=" + email
                + ", birthday=" + birthday
                + "}";
    }
}
