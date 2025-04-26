CREATE TABLE "Transaction"  (
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    account_number VARCHAR(100),
    trx_amount NUMERIC,
    description VARCHAR(500),
    trx_date DATE,
    trx_time TIME,
    customer_id VARCHAR(100),
    version INT
);