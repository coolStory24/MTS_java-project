CREATE TABLE reservation(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    user_id BIGINT REFERENCES "user" (id) ON DELETE SET NULL,
    start TIME NOT NULL,
    "end" TIME NOT NULL,
    start_day DATE NOT NULL,
    room_id BIGINT REFERENCES room (id) ON DELETE CASCADE NOT NULL
)