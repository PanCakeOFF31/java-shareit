delete from person;
delete from item;
delete from booking;
delete from comment;
delete from request;

alter table person alter id RESTART;
alter table item alter id RESTART;
alter table booking alter id RESTART;
alter table comment alter id RESTART;
alter table request alter id RESTART;
