-- -- H2 schema

CREATE TABLE IF NOT EXISTS Users(
    id INTEGER AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    primary key (id)
);