-- ======================================================
-- Issue #18 クレジットカード決済・割引機能 テスト用 SQL
-- 実行環境: Oracle SQL Developer (Version 21)
-- ======================================================

-- 1. シーケンスの削除と作成
DROP SEQUENCE seq_credit_cards;
CREATE SEQUENCE seq_credit_cards START WITH 1 INCREMENT BY 1;

-- 2. テーブルの作成（既存のテーブルがある場合は、一度削除してから実行するか、ALTER文を使用してください）
-- ここでは、新規追加・変更が必要な部分のみを記述します。

-- クレジットカードテーブルの作成
CREATE TABLE credit_cards (
    id              NUMBER(10)      PRIMARY KEY,
    holder_name     VARCHAR2(255)   NOT NULL,
    card_number     VARCHAR2(255)   NOT NULL, -- アプリケーション側で暗号化されるため、手動登録時は注意
    expiration_date VARCHAR2(5)     NOT NULL, -- 形式: MM/YY
    brand           VARCHAR2(50)    NOT NULL,
    user_id         NUMBER(10)      REFERENCES users(id),
    insert_date     DATE            DEFAULT SYSDATE NOT NULL
);

-- 注文テーブルへのカラム追加（まだ存在しない場合のみ実行）
-- ALTER TABLE orders ADD (credit_card_id NUMBER(10) REFERENCES credit_cards(id));

-- 3. テストデータの投入
-- 注意: カード番号は CipherUtil (AES) で暗号化された値を入れる必要があります。
-- 以下の値は、キー "S3cr3tK3yF0rSh0p" で "1234567812345678" を暗号化したサンプルです。

INSERT INTO credit_cards (id, holder_name, card_number, expiration_date, brand, user_id)
VALUES (seq_credit_cards.NEXTVAL, 'TARO YAMADA', '87/iLshB4I68m8C+NOf1Lw==', '12/25', 'Visa', 1);

INSERT INTO credit_cards (id, holder_name, card_number, expiration_date, brand, user_id)
VALUES (seq_credit_cards.NEXTVAL, 'TARO YAMADA', '87/iLshB4I68m8C+NOf1Lw==', '06/26', 'Mastercard', 1);

-- まとめ買い割引のテスト用に在庫の多い商品を追加
INSERT INTO items (id, name, price, stock, category_id)
VALUES (seq_items.NEXTVAL, 'テスト用大量在庫商品', 1000, 999, 1);

COMMIT;

-- 確認用クエリ
-- SELECT * FROM credit_cards;
-- DESC orders;
