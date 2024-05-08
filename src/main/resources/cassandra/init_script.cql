CREATE KEYSPACE IF NOT EXISTS my_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1} AND durable_writes = true;

USE my_keyspace;

CREATE TABLE IF NOT EXISTS exchange_info_rate (
    request_date DATE,
    is_closed BOOLEAN,
    resource TEXT,
    exchange_rates MAP<Text, Double>,
    PRIMARY KEY (request_date, is_closed)
);

INSERT INTO exchange_info_rate (request_date, is_closed, resource, exchange_rates)
    VALUES ('2024-04-30', False, 'www.nationalbank.kz', {});

INSERT INTO exchange_info_rate (request_date, is_closed, resource, exchange_rates)
    VALUES ('2024-04-30', True, 'www.nationalbank.kz', {'EUR': 475.55, 'RUB': 4.88, 'USD': 443.33});

INSERT INTO exchange_info_rate (request_date, is_closed, resource, exchange_rates)
    VALUES ('2024-04-29', True, 'www.nationalbank.kz', {'EUR': 475.54, 'RUB': 4.82, 'USD': 443.35});

INSERT INTO exchange_info_rate (request_date, is_closed, resource, exchange_rates)
    VALUES ('2024-04-28', False, 'www.nationalbank.kz', {'EUR': 475.54, 'RUB': 4.82, 'USD': 443.35});

INSERT INTO exchange_info_rate (request_date, is_closed, resource, exchange_rates)
    VALUES ('2024-04-28', True, 'www.nationalbank.kz', {'EUR': 475.54, 'RUB': 4.82, 'USD': 443.35});