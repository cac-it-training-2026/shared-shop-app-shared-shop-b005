-- Shared Shop Database Setup for Oracle 21c
-- Issue #16: ポイント・会員ランク・くじ機能

-- テーブル削除 (既存のデータをクリアする場合)
-- DROP TABLE order_items CASCADE CONSTRAINTS;
-- DROP TABLE orders CASCADE CONSTRAINTS;
-- DROP TABLE items CASCADE CONSTRAINTS;
-- DROP TABLE categories CASCADE CONSTRAINTS;
-- DROP TABLE users CASCADE CONSTRAINTS;

-- シーケンス削除
-- DROP SEQUENCE seq_users;
-- DROP SEQUENCE seq_categories;
-- DROP SEQUENCE seq_items;
-- DROP SEQUENCE seq_orders;
-- DROP SEQUENCE seq_order_items;

-- シーケンス作成
CREATE SEQUENCE seq_users START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_categories START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_items START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_orders START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_order_items START WITH 1 INCREMENT BY 1;

-- 1. 会員テーブル (users)
CREATE TABLE users (
    id NUMBER(10) PRIMARY KEY,
    email VARCHAR2(255) UNIQUE NOT NULL,
    password VARCHAR2(255) NOT NULL,
    name VARCHAR2(255) NOT NULL,
    postal_code CHAR(7) NOT NULL,
    address VARCHAR2(255) NOT NULL,
    phone_number VARCHAR2(11) NOT NULL,
    authority NUMBER(1) DEFAULT 2 NOT NULL, -- 0:管理者, 1:運用管理者, 2:一般
    delete_flag NUMBER(1) DEFAULT 0 NOT NULL,
    insert_date DATE DEFAULT SYSDATE NOT NULL,
    -- Issue #16 追加カラム
    current_point NUMBER(10) DEFAULT 0 NOT NULL,
    total_point NUMBER(10) DEFAULT 0 NOT NULL,
    rank NUMBER(1) DEFAULT 0 NOT NULL -- 0:ブロンズ, 1:シルバー, 2:ゴールド
);

-- 2. カテゴリテーブル (categories)
CREATE TABLE categories (
    id NUMBER(10) PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    description VARCHAR2(255),
    delete_flag NUMBER(1) DEFAULT 0 NOT NULL,
    insert_date DATE DEFAULT SYSDATE NOT NULL
);

-- 3. 商品テーブル (items)
CREATE TABLE items (
    id NUMBER(10) PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    price NUMBER(10) NOT NULL,
    description VARCHAR2(255),
    stock NUMBER(10) DEFAULT 0 NOT NULL,
    image VARCHAR2(255),
    category_id NUMBER(10),
    delete_flag NUMBER(1) DEFAULT 0 NOT NULL,
    insert_date DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 4. 注文テーブル (orders)
CREATE TABLE orders (
    id NUMBER(10) PRIMARY KEY,
    postal_code CHAR(7) NOT NULL,
    address VARCHAR2(255) NOT NULL,
    name VARCHAR2(255) NOT NULL,
    phone_number VARCHAR2(11) NOT NULL,
    pay_method NUMBER(1) NOT NULL,
    user_id NUMBER(10),
    insert_date DATE DEFAULT SYSDATE NOT NULL,
    -- Issue #16 追加カラム
    used_point NUMBER(10) DEFAULT 0 NOT NULL,
    earned_point NUMBER(10) DEFAULT 0 NOT NULL,
    lottery_executed NUMBER(1) DEFAULT 0 NOT NULL, -- 0:未実施, 1:実施済み
    lottery_rank NUMBER(1), -- 1:1等, 2:2等, 3:3等, 4:はずれ
    lottery_point NUMBER(10) DEFAULT 0 NOT NULL,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 5. 注文商品テーブル (order_items)
CREATE TABLE order_items (
    id NUMBER(10) PRIMARY KEY,
    quantity NUMBER(10) NOT NULL,
    order_id NUMBER(10),
    item_id NUMBER(10),
    price NUMBER(10) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_item FOREIGN KEY (item_id) REFERENCES items(id)
);

-- 初期データ挿入

-- 会員 (パスワードはすべて 'password')
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, current_point, total_point, rank)
VALUES (seq_users.NEXTVAL, 'admin@example.com', 'password', '管理者', '1234567', '東京都千代田区', '09011112222', 0, 0, 0, 0);

INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, current_point, total_point, rank)
VALUES (seq_users.NEXTVAL, 'user@example.com', 'password', '一般会員', '2345678', '神奈川県横浜市', '08033334444', 2, 500, 1500, 1);

-- カテゴリ
INSERT INTO categories (id, name, description) VALUES (seq_categories.NEXTVAL, '食品', '美味しい食べ物');
INSERT INTO categories (id, name, description) VALUES (seq_categories.NEXTVAL, '家電', '便利な電化製品');

-- 商品
INSERT INTO items (id, name, price, description, stock, image, category_id)
VALUES (seq_items.NEXTVAL, '美味しいリンゴ', 200, '青森県産のリンゴです。', 100, 'apple.jpg', 1);

INSERT INTO items (id, name, price, description, stock, image, category_id)
VALUES (seq_items.NEXTVAL, '最新型冷蔵庫', 50000, '省エネモデルの冷蔵庫です。', 10, 'fridge.jpg', 2);

COMMIT;
