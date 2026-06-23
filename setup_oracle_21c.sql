-- ======================================================
-- チーム開発演習用 データベース初期化 SQL (Oracle 21c用)
-- 多言語対応 (#15) および セキュリティ強化 (#20) 統合版
-- ======================================================

/* 1. 初期化処理 */

-- 依存関係の順序に従ってテーブルを削除
DROP TABLE order_items CASCADE CONSTRAINTS;
DROP TABLE orders CASCADE CONSTRAINTS;
DROP TABLE users CASCADE CONSTRAINTS;
DROP TABLE items CASCADE CONSTRAINTS;
DROP TABLE categories CASCADE CONSTRAINTS;

-- シーケンスの削除
DROP SEQUENCE seq_order_items;
DROP SEQUENCE seq_orders;
DROP SEQUENCE seq_users;
DROP SEQUENCE seq_items;
DROP SEQUENCE seq_categories;

PURGE RECYCLEBIN;

/* 2. シーケンスの作成 */

-- Java側の SequenceGenerator (allocationSize=1) に合わせて、INCREMENT BY 1 で作成します。
CREATE SEQUENCE seq_categories START WITH 10 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_items START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_users START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_orders START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_order_items START WITH 5000 INCREMENT BY 1 NOCACHE;

/* 3. テーブルの作成 */

-- カテゴリテーブル
CREATE TABLE categories (
    id          NUMBER(10)      PRIMARY KEY,
    name        VARCHAR2(255)   NOT NULL,
    name_en     VARCHAR2(255),
    name_es     VARCHAR2(255),
    name_eo     VARCHAR2(255),
    description VARCHAR2(255),
    delete_flag NUMBER(1)       DEFAULT 0 NOT NULL,
    insert_date DATE            DEFAULT SYSDATE NOT NULL
);

-- 会員テーブル (セキュリティ機能用のカラムを含む)
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

-- 商品テーブル (多言語用の名称・説明カラムを含む)
CREATE TABLE items (
    id          NUMBER(10)      PRIMARY KEY,
    name        VARCHAR2(255)   NOT NULL,
    name_en     VARCHAR2(255),
    name_es     VARCHAR2(255),
    name_eo     VARCHAR2(255),
    price       NUMBER(10)      NOT NULL,
    description VARCHAR2(1000),
    description_en VARCHAR2(1000),
    description_es VARCHAR2(1000),
    description_eo VARCHAR2(1000),
    stock       NUMBER(10)      DEFAULT 0 NOT NULL,
    image       VARCHAR2(255),
    category_id NUMBER(10)      REFERENCES categories(id) NOT NULL,
    delete_flag NUMBER(1)       DEFAULT 0 NOT NULL,
    insert_date DATE            DEFAULT SYSDATE NOT NULL
);

-- 注文テーブル
CREATE TABLE orders (
    id           NUMBER(10)      PRIMARY KEY,
    postal_code  VARCHAR2(7)     NOT NULL,
    address      VARCHAR2(255)   NOT NULL,
    name         VARCHAR2(255)   NOT NULL,
    phone_number VARCHAR2(15)    NOT NULL,
    pay_method   NUMBER(1)       NOT NULL,
    user_id      NUMBER(10)      REFERENCES users(id) NOT NULL,
    insert_date  DATE            DEFAULT SYSDATE NOT NULL
);

-- 注文商品テーブル (履歴保存時の多言語名称を含む)
CREATE TABLE order_items (
    id          NUMBER(10)      PRIMARY KEY,
    quantity    NUMBER(10)      NOT NULL,
    order_id    NUMBER(10)      REFERENCES orders(id) NOT NULL,
    item_id     NUMBER(10)      REFERENCES items(id) NOT NULL,
    price       NUMBER(10)      NOT NULL,
    name_en     VARCHAR2(255),
    name_es     VARCHAR2(255),
    name_eo     VARCHAR2(255)
);

/* 4. レコード登録 (初期データ) */

-- 4.1 カテゴリ
INSERT INTO categories (id, name, name_en, name_es, name_eo, description)
VALUES (1, '食料品', 'Food', 'Alimento', 'Manĝaĵo', '野菜類、肉類、海産物、加工食品などを扱います。');
INSERT INTO categories (id, name, name_en, name_es, name_eo, description)
VALUES (2, '書籍', 'Books', 'Libros', 'Libroj', '和書、洋書、専門書、漫画、雑誌などを扱います。');
INSERT INTO categories (id, name, name_en, name_es, name_eo, description)
VALUES (3, '日用品', 'Daily Needs', 'Artículos diarios', 'Ĉiutagaj bezonoj', 'トイレットペーパーを扱います。');

-- 4.2 会員
-- パスワード 'password' のハッシュ: 5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8
-- テストの容易性のため、すべての新規ユーザーの初期パスワードを 'password' (ハッシュ値) に統一します。
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (1, 'user@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '一般 太郎', '1234567', '東京都新宿区', '09011112222', 2, '母親の旧姓は？', '田中');
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (2, 'admin@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '管理者 一郎', '9876543', '東京都千代田区', '0312345678', 0, '最初のペットの名前は？', 'ポチ');

INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (3, 'tanaka_taro@test.co.jp', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'システム管理太郎', '1111111', '東京都台東区1-2-3 ABCビル10階', '0123456789', 0, '好きな食べ物は？', 'ラーメン');
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (4, 'unyo_jiro@test.co.jp', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '運用管理二郎', '1111111', '東京都台東区1-2-3 ABCビル10階', '0123456789', 1, '出身小学校は？', 'SSS小学校');
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (5, 'mikkis01@cac.co.jp', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '一般太郎', '1111111', '東京都台東区4-5-6 ABCマンション5階', '0123456789', 2, '母親の旧姓は？', '佐藤');
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (6, 'mikkis02@cac.co.jp', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '一般次郎', '1111111', '東京都台東区4-5-6 ABCマンション5階', '0123456789', 2, '好きな映画は？', 'スターウォーズ');
INSERT INTO users (id, email, password, name, postal_code, address, phone_number, authority, secret_question, secret_answer)
VALUES (7, 'mikkis03@cac.co.jp', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '一般龍之介', '1111111', '東京都台東区4-5-6 ABCマンション5階', '0123456789', 2, '趣味は？', '読書');

-- 4.3 商品
INSERT INTO items (id, name, name_en, name_es, name_eo, price, description, description_en, description_es, description_eo, stock, image, category_id)
VALUES (1, 'りんご', 'Apple', 'Manzana', 'Pomo', 100, '青森県産のりんごです。とってもみずみずしい！', 'A fresh apple from Aomori. Very juicy!', 'Una manzana fresca de Aomori. ¡Muy jugosa!', 'Freŝa pomo el Aomori. Tre sukplena!', 9999, 'apple.jpg', 1);
INSERT INTO items (id, name, name_en, name_es, name_eo, price, description, description_en, description_es, description_eo, stock, image, category_id)
VALUES (2, 'オレンジ', 'Orange', 'Naranja', 'Oranĝo', 150, 'オーストラリア産のオレンジです。', 'An orange from Australia.', 'Una naranja de Australia.', 'Oranĝo el Aŭstralio.', 1, NULL, 1);
INSERT INTO items (id, name, name_en, name_es, name_eo, price, description, description_en, description_es, description_eo, stock, image, category_id)
VALUES (3, 'ばなな', 'Banana', 'Banana', 'Banano', 70, '獲れたてです。', 'Freshly picked.', 'Recién cosechado.', 'Freŝe rikoltita.', 0, 'banana.jpg', 1);
INSERT INTO items (id, name, name_en, name_es, name_eo, price, description, description_en, description_es, description_eo, stock, image, category_id)
VALUES (4, '辞書', 'Dictionary', 'Diccionario', 'Vortaro', 2000, 'これ一冊があれば大丈夫！', 'Everything you need in one book!', '¡Todo lo que necesitas en un solo libro!', 'Ĉio, kion vi bezonas, en unu libro!', 5, 'dictionary.jpg', 2);
INSERT INTO items (id, name, name_en, name_es, name_eo, price, description, description_en, description_es, description_eo, stock, image, category_id)
VALUES (5, 'トイレットペーパー(6ロール)', 'Toilet Paper (6 rolls)', 'Papel higiénico (6 rollos)', 'Neceseja papero (6 ruloj)', 700, 'おしりにやさしいです。', 'Gentle on the skin.', 'Suave para la piel.', 'Milda por la haŭto.', 6, NULL, 3);

-- 4.4 注文
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (1, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般太郎', '0123456789', 2, 5, TO_DATE('2025-10-10', 'YYYY-MM-DD'));
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (2, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, TO_DATE('2026-01-01', 'YYYY-MM-DD'));
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (3, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, TO_DATE('2026-01-10', 'YYYY-MM-DD'));
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (4, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 2, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (5, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 1, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (6, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 2, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (7, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (8, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (9, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (10, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (11, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, SYSDATE);
INSERT INTO orders (id, postal_code, address, name, phone_number, pay_method, user_id, insert_date)
VALUES (12, '1111111', '東京都台東区4-5-6 ABCマンション5階', '一般次郎', '0123456789', 3, 6, SYSDATE);

-- 4.5 注文商品
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (1, 4, 1, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (2, 1, 1, 2, 150, 'Orange', 'Naranja', 'Oranĝo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (3, 1, 2, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (4, 1, 3, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (5, 1, 4, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (6, 1, 5, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (7, 1, 6, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (8, 1, 7, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (9, 1, 8, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (10, 1, 9, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (11, 1, 10, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (12, 1, 11, 1, 100, 'Apple', 'Manzana', 'Pomo');
INSERT INTO order_items (id, quantity, order_id, item_id, price, name_en, name_es, name_eo)
VALUES (13, 1, 12, 1, 100, 'Apple', 'Manzana', 'Pomo');

-- コミット
COMMIT;
