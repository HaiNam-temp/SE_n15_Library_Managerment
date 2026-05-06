ALTER TABLE book_category
  ADD COLUMN is_default BOOLEAN NOT NULL DEFAULT FALSE;

-- Set one default category if none exists
INSERT INTO book_category (category_name, description, status, is_default, created_at, updated_at)
SELECT 'Danh mục mặc định', 'Danh mục sách mặc định của hệ thống', 'ACTIVE', TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM book_category WHERE is_default = TRUE);