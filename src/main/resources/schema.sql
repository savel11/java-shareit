CREATE TABLE IF NOT EXISTS users (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 name VARCHAR NOT NULL,
 email VARCHAR NOT NULL,
 CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 description VARCHAR NOT NULL,
 user_id BIGINT,
 created_date TIMESTAMP WITHOUT TIME ZONE,
 CONSTRAINT fk_requests_to_users FOREIGN KEY(user_id) REFERENCES users(id),
 CONSTRAINT length_request_description CHECK (length(description) <= 200)
);

CREATE TABLE IF NOT EXISTS items (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 name VARCHAR NOT NULL,
 description VARCHAR NOT NULL,
 is_available BOOL NOT NUll,
 owner_id BIGINT,
 request_id BIGINT,
 CONSTRAINT length_item_name CHECK (length(name) <= 30),
 CONSTRAINT length_item_description CHECK (length(description) <= 200),
 CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
 CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id)
);
CREATE TABLE IF NOT EXISTS bookings (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 item_id BIGINT,
 booker_id BIGINT,
 start_date TIMESTAMP WITHOUT TIME ZONE,
 end_date TIMESTAMP WITHOUT TIME ZONE,
 status VARCHAR,
 CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id),
 CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
 CONSTRAINT status_enum CHECK (status in ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS comments (
 id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 text VARCHAR NOT NULL,
 item_id BIGINT,
 author_id BIGINT,
 created_date TIMESTAMP WITHOUT TIME ZONE,
 CONSTRAINT length_comment_text CHECK (length(text) <= 500),
 CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id),
 CONSTRAINT fk_comments_to_users  FOREIGN KEY (author_id) REFERENCES users (id)
);
