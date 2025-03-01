drop table if exists users;

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    CITEXT      NOT NULL UNIQUE,
    username VARCHAR(30) NOT NULL UNIQUE,
    password TEXT        NOT NULL,
    role     varchar(30)
);

insert into users (email, username, password, role)
values ('peterskh13@gmail.com', 'Ken', crypt('admin', gen_salt('bf')), 'ADMIN') returning id;

insert into users (email, username, password, role)
values ('peterskp7@gmail.com', 'Ken2', crypt('user', gen_salt('bf')), 'USER') returning id;

insert into users (email, username, password, role)
values ('fake@gmail.com', 'Ken3', crypt('fake', gen_salt('bf')), 'USER') returning id;
