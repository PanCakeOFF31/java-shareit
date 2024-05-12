drop table IF EXISTS person CASCADE;
drop table IF EXISTS item CASCADE;
drop table IF EXISTS booking CASCADE;
drop table IF EXISTS comment CASCADE;
drop table IF EXISTS request CASCADE;

create TABLE IF NOT EXISTS person ( id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name varchar(128) NOT NULL, email varchar(256) NOT NULL UNIQUE CHECK(email ~ ('^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')));

create TABLE IF NOT EXISTS request ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, description varchar(1024) NOT NULL CHECK(length(description) > 10), requester_id bigint REFERENCES person(id) ON delete CASCADE NOT NULL, created timestamp WITHOUT TIME ZONE NOT NULL);

create TABLE IF NOT EXISTS item ( id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name varchar(128) NOT NULL, description varchar(1024) NOT NULL, is_available boolean NOT NULL, owner_id bigint REFERENCES person(id) ON delete CASCADE NOT NULL, request_id bigint REFERENCES request(id) ON delete CASCADE NULL);

create TABLE IF NOT EXISTS booking ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, booking_start timestamp WITHOUT TIME ZONE NOT NULL, booking_end timestamp WITHOUT TIME ZONE NOT NULL, item_id bigint REFERENCES item(id) ON delete CASCADE NOT NULL, booker_id bigint REFERENCES person(id) ON delete CASCADE NOT NULL, status varchar(10) NOT NULL, CHECK(booking_start != booking_end AND booking_end > booking_start));

create TABLE IF NOT EXISTS comment ( id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, text varchar(1024) NOT NULL CHECK(length(text) > 10), item_id bigint REFERENCES item(id) ON delete CASCADE NOT NULL, author_id bigint REFERENCES person(id) ON delete CASCADE NOT NULL, created timestamp WITHOUT TIME ZONE NOT NULL, UNIQUE(author_id, item_id));
