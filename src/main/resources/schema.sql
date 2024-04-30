DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS "item" CASCADE;
DROP TABLE IF EXISTS "booking" CASCADE;
DROP TABLE IF EXISTS "comment" CASCADE;

CREATE TABLE IF NOT EXISTS "user" ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name VARCHAR(128) NOT NULL, email VARCHAR(256) NOT NULL UNIQUE CHECK(email ~ ('^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')));

CREATE TABLE IF NOT EXISTS "item" ( id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name varchar(128) NOT NULL, description varchar(1024) NOT NULL, is_available boolean NOT NULL, owner_id bigint REFERENCES "user"(id) ON DELETE CASCADE NOT NULL);

CREATE TABLE IF NOT EXISTS "booking" ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, booking_start timestamp WITHOUT TIME ZONE NOT NULL, booking_end timestamp WITHOUT TIME ZONE NOT NULL, item_id bigint REFERENCES "item"(id) ON DELETE CASCADE NOT NULL, booker_id bigint REFERENCES "user"(id) ON DELETE CASCADE NOT NULL, status varchar(10) NOT NULL, CHECK(booking_start != booking_end AND booking_end > booking_start));

CREATE TABLE IF NOT EXISTS "comment" ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, text varchar(1024) NOT NULL, item_id bigint REFERENCES "item"(id) ON DELETE CASCADE NOT NULL, author_id bigint REFERENCES "user"(id) ON DELETE CASCADE NOT NULL, created timestamp WITHOUT TIME ZONE NOT NULL, UNIQUE(author_id, item_id));

--DELETE FROM "user";
--DELETE FROM "item";
--DELETE FROM "booking";
--DELETE FROM "comment";
--
--ALTER TABLE "user" ALTER id RESTART;
--ALTER TABLE "item" ALTER id RESTART;
--ALTER TABLE "booking" ALTER id RESTART;
--ALTER TABLE "comment" ALTER id RESTART;