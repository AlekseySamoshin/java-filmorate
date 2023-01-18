INSERT INTO rating (rating_id, rating_name) VALUES (1, 'G');
INSERT INTO rating (rating_id, rating_name) VALUES (2, 'PG');
INSERT INTO rating (rating_id, rating_name) VALUES (3, 'PG-13');
INSERT INTO rating (rating_id, rating_name) VALUES (4, 'R');
INSERT INTO rating (rating_id, rating_name) VALUES (5, 'NC-17');

INSERT INTO genres (genre_id, genre_name) VALUES (1, 'Комедия');
INSERT INTO genres (genre_id, genre_name) VALUES (2, 'Драма');
INSERT INTO genres (genre_id, genre_name) VALUES (3, 'Мультфильм');
INSERT INTO genres (genre_id, genre_name) VALUES (4, 'Триллер');
INSERT INTO genres (genre_id, genre_name) VALUES (5, 'Документальный');
INSERT INTO genres (genre_id, genre_name) VALUES (6, 'Боевик');


INSERT INTO users (user_email, user_login, user_name, user_birthday)
VALUES ('email@mail.ru', 'superman', 'Ivan', '2016-11-10');

INSERT INTO users (user_email, user_login, user_name, user_birthday)
VALUES ('oldstar@rambler.ru', 'superstar', 'Efim', '1987-08-06');

INSERT INTO users (user_email, user_login, user_name, user_birthday)
VALUES ('cheburashka@zoo.ru', 'cheburator', 'Cheburashka', '1969-08-20');


INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Безудержное веселье', 'мимолетная комедия', '2020-02-22', 436, 1);

INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Котастрофа', 'рыжий триллер', '2022-06-18', 120, 3);

INSERT INTO films (film_name, description, release_date, duration, rating_id)
VALUES ('Красная синева', 'Закат синел бордовым в гранатовой лазури аквамарина', '2016-08-16', 122, 5);