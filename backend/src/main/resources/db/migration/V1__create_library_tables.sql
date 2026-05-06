-- Flyway initial migration: create library tables

CREATE TABLE IF NOT EXISTS account (
  account_id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50),
  status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS admin (
  admin_id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(255),
  contact_info VARCHAR(255),
  account_id BIGINT REFERENCES account(account_id)
);

CREATE TABLE IF NOT EXISTS librarian (
  librarian_id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(255),
  contact_info VARCHAR(255),
  account_id BIGINT REFERENCES account(account_id)
);

CREATE TABLE IF NOT EXISTS activity_log (
  log_id BIGSERIAL PRIMARY KEY,
  action VARCHAR(255),
  action_time TIMESTAMP,
  description TEXT,
  account_id BIGINT REFERENCES account(account_id)
);

CREATE TABLE IF NOT EXISTS borrowing_rule (
  rule_id BIGSERIAL PRIMARY KEY,
  max_books INTEGER,
  max_borrow_days INTEGER,
  fine_per_day NUMERIC(10,2)
);

CREATE TABLE IF NOT EXISTS book_category (
  id BIGSERIAL PRIMARY KEY,
  category_name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(500),
  status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS book (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255),
  author VARCHAR(255),
  published_year INTEGER,
  publisher VARCHAR(255),
  status VARCHAR(100),
  imported_date DATE,
  isbn VARCHAR(100),
  description TEXT,
  book_category_id BIGINT REFERENCES book_category(id)
);

CREATE TABLE IF NOT EXISTS book_item (
  item_id BIGSERIAL PRIMARY KEY,
  barcode VARCHAR(255),
  status VARCHAR(50),
  location VARCHAR(255),
  book_id BIGINT REFERENCES book(id)
);

CREATE TABLE IF NOT EXISTS reader (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(255),
  date_of_birth DATE,
  gender VARCHAR(50),
  address VARCHAR(500),
  email VARCHAR(255),
  card_created_date DATE,
  card_expired_date DATE,
  card_status VARCHAR(50),
  current_borrowed_count INTEGER,
  phone VARCHAR(50),
  student_code_or_citizen_id VARCHAR(100),
  borrowing_rule_id BIGINT REFERENCES borrowing_rule(rule_id)
);

CREATE TABLE IF NOT EXISTS borrow_receipt (
  id BIGSERIAL PRIMARY KEY,
  volume_title VARCHAR(255),
  borrowed_date DATE,
  reader_id BIGINT REFERENCES reader(id)
);

CREATE TABLE IF NOT EXISTS borrow_detail (
  id BIGSERIAL PRIMARY KEY,
  returned_date DATE,
  borrow_receipt_id BIGINT REFERENCES borrow_receipt(id),
  book_id BIGINT REFERENCES book(id)
);

CREATE TABLE IF NOT EXISTS reservation (
  reservation_id BIGSERIAL PRIMARY KEY,
  reservation_date DATE,
  status VARCHAR(50),
  reader_id BIGINT REFERENCES reader(id),
  book_item_id BIGINT REFERENCES book_item(item_id)
);

CREATE TABLE IF NOT EXISTS loan_ticket (
  loan_id BIGSERIAL PRIMARY KEY,
  borrow_date DATE,
  due_date DATE,
  status VARCHAR(50),
  reader_id BIGINT REFERENCES reader(id)
);

CREATE TABLE IF NOT EXISTS loan_detail (
  id BIGSERIAL PRIMARY KEY,
  actual_return_date DATE,
  return_condition VARCHAR(255),
  loan_ticket_id BIGINT REFERENCES loan_ticket(loan_id),
  book_item_id BIGINT REFERENCES book_item(item_id)
);

CREATE TABLE IF NOT EXISTS fine (
  fine_id BIGSERIAL PRIMARY KEY,
  amount NUMERIC(12,2),
  reason VARCHAR(500),
  status VARCHAR(50),
  loan_detail_id BIGINT REFERENCES loan_detail(id)
);
