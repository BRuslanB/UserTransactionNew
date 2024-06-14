ALTER TABLE t_expense_transaction DROP CONSTRAINT IF EXISTS t_expense_transaction_amount_limit_id_fk;
ALTER TABLE t_expense_transaction
    ADD CONSTRAINT t_expense_transaction_amount_limit_id_fk
        FOREIGN KEY (amount_limit_id) REFERENCES t_amount_limit(id)
        ON DELETE SET NULL;