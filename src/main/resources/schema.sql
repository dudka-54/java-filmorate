CREATE TABLE films (
id BIGINT PRIMARY KEY,
name VARCHAR NOT NULL,
description TEXT NOT NULL,
release_date DATE,
duration INT,
genre VARCHAR,
mpa VARCHAR
)

CREATE TABLE mpa (
film_id BIGINT,
mpa_id INTEGER NOT NULL
PRIMARY KEY (film_id, genre_id)
)

CREATE TABLE genres (
film_id BIGINT NOT NULL,
genre_id INTEGER NOT NULL,
PRIMARY KEY (film_id, genre_id)
)

CREATE TABLE film_likes(
film_id BIGINT,
user_id BIGINT,
PRIMARY KEY (user_id, film_id)

)

CREATE TABLE users(
id BIGINT PRIMARY KEY,
email VARCHAR,
login VARCHAR,
name VARCHAR,
birthday DATE,
friendship_status VARCHAR
)

