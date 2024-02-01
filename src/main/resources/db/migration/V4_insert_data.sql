-- Вставка данных в таблицу t_exchange_info
INSERT INTO t_exchange_info (request_date, resource)
VALUES ('2024-01-29'::DATE, 'www.nationalbank.kz');

-- Получение последнего идентификатора вставленной записи в t_exchange_info
WITH last_exchange_info_id AS (
    SELECT currval(pg_get_serial_sequence('t_exchange_info', 'id')) AS last_id
)

-- Вставка данных в таблицу t_exchange_rate
INSERT INTO t_exchange_rate (currency_name, currency_code, exchange_rate, exchange_info_id)
VALUES
    ('ДОЛЛАР США', 'USD', 449.74, (SELECT last_id FROM last_exchange_info_id)),
    ('РОССИЙСКИЙ РУБЛЬ', 'RUB', 5.03, (SELECT last_id FROM last_exchange_info_id)),
    ('ЕВРО', 'EUR', 487.7, (SELECT last_id FROM last_exchange_info_id));
