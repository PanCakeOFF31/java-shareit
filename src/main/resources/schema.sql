drop table IF EXISTS person CASCADE;
drop table IF EXISTS item CASCADE;
drop table IF EXISTS booking CASCADE;
drop table IF EXISTS comment CASCADE;

create TABLE IF NOT EXISTS person ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name VARCHAR(128) NOT NULL, email VARCHAR(256) NOT NULL UNIQUE CHECK(email ~ ('^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')));

create TABLE IF NOT EXISTS item ( id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name varchar(128) NOT NULL, description varchar(1024) NOT NULL, is_available boolean NOT NULL, owner_id bigint REFERENCES person(id) ON delete CASCADE NOT NULL);

create TABLE IF NOT EXISTS booking ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, booking_start timestamp WITHOUT TIME ZONE NOT NULL, booking_end timestamp WITHOUT TIME ZONE NOT NULL, item_id bigint REFERENCES item(id) ON delete CASCADE NOT NULL, booker_id bigint REFERENCES person(id) ON delete CASCADE NOT NULL, status varchar(10) NOT NULL, CHECK(booking_start != booking_end AND booking_end > booking_start));

create TABLE IF NOT EXISTS comment ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, text varchar(1024) NOT NULL, item_id bigint REFERENCES item(id) ON delete CASCADE NOT NULL, author_id bigint REFERENCES person(id) ON delete CASCADE NOT NULL, created timestamp WITHOUT TIME ZONE NOT NULL, UNIQUE(author_id, item_id));

DELETE FROM person;
DELETE FROM item;
DELETE FROM booking;
DELETE FROM comment;

ALTER TABLE person ALTER id RESTART;
ALTER TABLE item ALTER id RESTART;
ALTER TABLE booking ALTER id RESTART;
ALTER TABLE comment ALTER id RESTART;