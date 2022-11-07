# java-filmorate
### Социальная сеть для любителей фильмов

------------
#### Примеры запросов к базе данных:
###### Список фильмов с возрастным рейтингом:
    SELECT film.film_name,
                 rating.rating_id
    FROM film
    LEFT JOIN rating ON film.rating_id = rating.rating_id

###### Список друзей пользователя с id=1:
    SELECT friends.friend_id AS friends_list
    FROM friends
    JOIN user
    WHERE user.user_id = 1

[![Схема базы данных](https://github.com/AlekseySamoshin/java-filmorate/blob/main/Filmorate_database%20-%20Frame%201.jpg "Схема базы данных")](https://github.com/AlekseySamoshin/java-filmorate/blob/main/Filmorate_database%20-%20Frame%201.jpg "Схема базы данных")

#### Примеры HTTP-запросов к серверу:
- POST http://localhost:8080/users - создание пользователя с передачей объекта в теле запроса
- GET http://localhost:8080/users - получение списка всех пользователей
- GET http://localhost:8080/users/1 - получение пользователя с id=1
- PUT http://localhost:8080/users/1/friends/2 - добавление пользователя с id=2 в друзья пользователю id=1
- GET http://localhost:8080/users/1/friends - получение списка друзей пользователя с id=1
- GET http://localhost:8080/films - получение списка фильмов
- POST http://localhost:8080/films - создание фильма с передачей объекта в теле запроса
- PUT http://localhost:8080/films/2/like/1 - добавление лайка фильму с id=2 от пользователя с id=1
- GET http://localhost:8080/films/popular - получение топ-10 фильмов с максимальным количеством лайков
