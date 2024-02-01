DROP TABLE IF EXISTS t_amount_limit CASCADE;
CREATE TABLE t_amount_limit (
    id SERIAL PRIMARY KEY NOT NULL,
    account_client VARCHAR(10) NOT NULL,
    limit_sum NUMERIC NOT NULL,
    limit_date TIMESTAMP WITH TIME ZONE NOT NULL,
    limit_currency_code VARCHAR(3) NOT NULL,
    expense_category VARCHAR(10) NOT NULL
);

DROP TABLE IF EXISTS t_expense_transaction CASCADE;
CREATE TABLE t_expense_transaction (
    id SERIAL PRIMARY KEY NOT NULL,
    account_client VARCHAR(10) NOT NULL,
    account_counterparty VARCHAR(10) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    transaction_sum NUMERIC NOT NULL,
    expense_category VARCHAR(10) NOT NULL,
    transaction_date TIMESTAMP WITH TIME ZONE NOT NULL,
    limit_exceeded BOOLEAN NOT NULL
);

DROP TABLE IF EXISTS t_exchange_info CASCADE;
CREATE TABLE t_exchange_info (
    id SERIAL PRIMARY KEY NOT NULL,
    resource TEXT NOT NULL,
    request_date DATE NOT NULL
);

DROP TABLE IF EXISTS t_exchange_rate CASCADE;
CREATE TABLE t_exchange_rate (
    id SERIAL PRIMARY KEY NOT NULL,
    currency_name TEXT NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    exchange_rate NUMERIC NOT NULL
);
