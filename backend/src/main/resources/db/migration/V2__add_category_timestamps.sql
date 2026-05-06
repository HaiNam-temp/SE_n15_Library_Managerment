ALTER TABLE book_category
  ADD COLUMN created_at TIMESTAMP,
  ADD COLUMN updated_at TIMESTAMP;

UPDATE book_category SET created_at = NOW(), updated_at = NOW();
