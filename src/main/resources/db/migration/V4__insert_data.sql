-- Вставка данных в таблицу t_amount_limit
INSERT INTO t_amount_limit (account_client, limit_sum, limit_date, limit_currency_code, expense_category)
VALUES
    ('0000000123', 1000, '2024-01-01 00:00:00+06'::TIMESTAMP, 'RUB', 'Service'),
    ('0000000123', 1000, '2024-01-30 15:35:34+06'::TIMESTAMP, 'EUR', 'Service'),
    ('0000001234', 1000, '2024-01-01 00:00:00+06'::TIMESTAMP, 'RUB', 'Product'),
    ('0000001234', 10000, '2024-01-30 16:20:26+06'::TIMESTAMP, 'KZT', 'Product'),
    ('0000012345', 1000, '2024-01-01 00:00:00+06'::TIMESTAMP, 'KZT', 'Product'),
    ('0000012345', 100000.01, '2024-01-30 16:39:11+06'::TIMESTAMP, 'KZT', 'Product'),
    ('0000012345', 100, '2024-01-01 00:00:00+06'::TIMESTAMP, 'EUR', 'Service'),
    ('0000123456', 500, '2024-02-01 15:45:03+06'::TIMESTAMP, 'EUR', 'Product');

-- Вставка данных в таблицу t_expense_transaction
INSERT INTO t_expense_transaction (account_client, account_counterparty, currency_code,
    transaction_sum, expense_category, transaction_date, limit_exceeded, amount_limit_id)
VALUES
    ('0000000123', '9870000000', 'RUB', 100, 'Service', '2024-01-30 12:16:15+06', 'false', 1),
    ('0000000123', '9870000000', 'RUB', 10, 'Service', '2024-01-30 12:16:15+06', 'false', 1),
    ('0000000123', '9870000000', 'EUR', 101, 'Service', '2024-01-30 15:15:15+06', 'false', 2),
    ('0000001234', '9876500000', 'EUR', 100, 'Product', '2024-01-30 15:15:15+06', 'true', 3),
    ('0000001234', '9876500000', 'RUB', 100, 'Product', '2024-01-30 15:15:15+06', 'true', 4),
    ('0000012345', '9876500000', 'USD', 10, 'Product', '2024-01-30 15:15:15+06', 'true', 5),
    ('0000012345', '9876500000', 'USD', 100.1, 'Product', '2024-01-30 15:15:20+06', 'false', 6),
    ('0000012345', '9870000000', 'USD', 99.99, 'Service', '2024-01-30 15:15:20+06', 'false', 7),
    ('0000123456', '9876500000', 'USD', 100.99, 'Product', '2024-01-31 15:15:20+06', 'false', 8);

-- Вставка данных в таблицу t_exchange_info
INSERT INTO t_exchange_info (request_date, resource)
VALUES
    ('2024-01-30'::DATE, 'www.nationalbank.kz'),
    ('2024-01-31'::DATE, 'www.nationalbank.kz');

-- Вставка данных в таблицу t_exchange_rate
INSERT INTO t_exchange_rate (currency_name, currency_code, exchange_rate, exchange_info_id)
VALUES
    ('ДОЛЛАР США', 'USD', 449.74, 1),
    ('РОССИЙСКИЙ РУБЛЬ', 'RUB', 5.03, 1),
    ('ЕВРО', 'EUR', 487.7, 1),
    ('ДОЛЛАР США', 'USD', 449.98, 2),
    ('РОССИЙСКИЙ РУБЛЬ', 'RUB', 5.02, 2),
    ('ЕВРО', 'EUR', 487.01, 2);
