-- ======================================================
-- #20 認証・ログイン・セキュリティ機能テスト用 SQL (Oracle 21c用)
-- ======================================================

-- 1. シーケンス
DROP SEQUENCE seq_users;
CREATE SEQUENCE seq_users START WITH 100 INCREMENT BY 1;
DROP SEQUENCE seq_categories;
CREATE SEQUENCE seq_categories START WITH 10 INCREMENT BY 1;
DROP SEQUENCE seq_items;
CREATE SEQUENCE seq_items START WITH 100 INCREMENT BY 1;
DROP SEQUENCE seq_orders;
CREATE SEQUENCE seq_orders START WITH 1000 INCREMENT BY 1;
DROP SEQUENCE seq_order_items;
CREATE SEQUENCE seq_order_items START WITH 5000 INCREMENT BY 1;
DROP SEQUENCE seq_inquiries;
CREATE SEQUENCE seq_inquiries START WITH 1 INCREMENT BY 1;

-- 2. テーブル
CREATE TABLE users (
    id                  NUMBER(10)      PRIMARY KEY,
    email               VARCHAR2(255)   NOT NULL UNIQUE,
    password            VARCHAR2(255)   NOT NULL,
    name                VARCHAR2(255)   NOT NULL,
    postal_code         VARCHAR2(7)     NOT NULL,
    address             VARCHAR2(255)   NOT NULL,
    phone_number        VARCHAR2(15)    NOT NULL,
    authority           NUMBER(1)       NOT NULL,
    delete_flag         NUMBER(1)       DEFAULT 0 NOT NULL,
    insert_date         DATE            DEFAULT SYSDATE NOT NULL,
    secret_question     VARCHAR2(255),
    secret_answer       VARCHAR2(255),
    login_failure_count NUMBER(5)       DEFAULT 0,
    account_locked      NUMBER(1)       DEFAULT 0,
    account_locked_until TIMESTAMP,
    reset_token         VARCHAR2(255),
    reset_token_expire  TIMESTAMP
);

CREATE TABLE categories (
    id          NUMBER(10)      PRIMARY KEY,
    name        VARCHAR2(255)   NOT NULL,
    description VARCHAR2(255),
    delete_flag NUMBER(1)       DEFAULT 0 NOT NULL,
    insert_date DATE            DEFAULT SYSDATE NOT NULL
);

CREATE TABLE items (
    id          NUMBER(10)      PRIMARY KEY,
    name        VARCHAR2(255)   NOT NULL,
    price       NUMBER(10)      NOT NULL,
    description VARCHAR2(255),
    stock       NUMBER(10)      NOT NULL,
    image       VARCHAR2(255),
    category_id NUMBER(10)      REFERENCES categories(id),
    delete_flag NUMBER(1)       DEFAULT 0 NOT NULL,
    insert_date DATE            DEFAULT SYSDATE NOT NULL
);

CREATE TABLE orders (
    id           NUMBER(10)      PRIMARY KEY,
    postal_code  VARCHAR2(7)     NOT NULL,
    address      VARCHAR2(255)   NOT NULL,
    name         VARCHAR2(255)   NOT NULL,
    phone_number VARCHAR2(15)    NOT NULL,
    pay_method   NUMBER(1)       NOT NULL,
    user_id      NUMBER(10)      REFERENCES users(id),
    insert_date  DATE            DEFAULT SYSDATE NOT NULL
);

CREATE TABLE order_items (
    id          NUMBER(10)      PRIMARY KEY,
    quantity    NUMBER(10)      NOT NULL,
    order_id    NUMBER(10)      REFERENCES orders(id),
    item_id     NUMBER(10)      REFERENCES items(id),
    price       NUMBER(10)      NOT NULL
);

CREATE TABLE inquiries (
    id          NUMBER(10)      PRIMARY KEY,
    name        VARCHAR2(255)   NOT NULL,
    email       VARCHAR2(255)   NOT NULL,
    content     VARCHAR2(4000)  NOT NULL,
    insert_date TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 3. データ (パスワード 'password' のハッシュ: 5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8)
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (1, 'user@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '一般 太郎', '1234567', '東京都新宿区', '09011112222', 2, '母親の旧姓は？', '田中');

INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (2, 'admin@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '管理者 一郎', '9876543', '東京都千代田区', '0312345678', 0, '最初のペットの名前は？', 'ポチ');

INSERT INTO categories (id, name) VALUES (1, '食品');
INSERT INTO items (id, name, price, stock, category_id) VALUES (1, 'りんご', 100, 50, 1);

COMMIT;
