-- Add account_id column to reader table
ALTER TABLE reader ADD COLUMN account_id BIGINT REFERENCES account(account_id);
