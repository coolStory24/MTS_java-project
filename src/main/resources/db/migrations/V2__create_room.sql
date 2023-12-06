CREATE TABLE room(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    title VARCHAR(30) NOT NULL,
    start_interval TIME NOT NULL,
    end_interval TIME NOT NULL
);