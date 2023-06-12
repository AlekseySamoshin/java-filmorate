# java-filmorate
### Социальная сеть для любителей фильмов

объединяет любителей фильмов и позволяет находить интересные для себя фильмы и получать рекомендации к просмотру на основании положительных отзывов других пользователей со схожими интересами.   

------------

[![Схема базы данных](https://github.com/AlekseySamoshin/java-filmorate/blob/main/Filmorate_database%20-%20Frame%201.jpg "Схема базы данных")](https://github.com/AlekseySamoshin/java-filmorate/blob/main/Filmorate_database%20-%20Frame%201.jpg "Схема базы данных")

------------

#### Примеры HTTP-запросов к серверу:
- POST http://localhost:8080/users - создание пользователя с передачей объекта в теле запроса
- GET http://localhost:8080/users - получение списка всех пользователей
- GET http://localhost:8080/users/{userId} - получение пользователя по id
- PUT http://localhost:8080/users/{userId}/friends/{friendId} - добавление пользователю в друзья другого пользователя
- GET http://localhost:8080/users/{userId}/friends - получение списка друзей пользователя
- GET http://localhost:8080/films - получение списка фильмов
- POST http://localhost:8080/films - создание фильма с передачей объекта в теле запроса
- PUT http://localhost:8080/films/{filmId}/like/{userId} - добавление лайка фильму от указанного пользователя
- GET http://localhost:8080/films/popular - получение топ-10 фильмов с максимальным количеством лайков
