CREATE TABLE IF NOT EXISTS PARSE
(
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        reqtime TIMESTAMP,
        reqlink VARCHAR,
        resptime TIMESTAMP,
        resplink VARCHAR
);