CREATE SEQUENCE IF NOT EXISTS hash_sequence_number
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) PRIMARY KEY ,
    url        varchar(2048) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hash(hash)
);

CREATE TABLE IF NOT EXISTS hash
(
    hash varchar(6) PRIMARY KEY
)
