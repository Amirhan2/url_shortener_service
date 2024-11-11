CREATE TABLE url
(
    hash       CHAR(6) PRIMARY KEY,
    url        VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash
(
    hash         CHAR(6) PRIMARY KEY,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
