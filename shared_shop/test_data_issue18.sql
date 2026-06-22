-- ======================================================
-- Issue #18 購入・決済機能拡張 テストデータ用 SQL (Oracle 21c用)
-- ======================================================

-- 既存データのクリーンアップ（必要に応じて実行）
-- DELETE FROM order_items;
-- DELETE FROM orders;
-- DELETE FROM credit_cards;
-- DELETE FROM items WHERE id IN (201, 202, 203);

-- 1. テスト用商品の追加 (まとめ買い割引テスト用)
-- IDはシーケンスと競合しないよう大きな値を指定するか、シーケンスを使用してください。
INSERT INTO items (id, name, price, description, stock, category_id, delete_flag, insert_date)
VALUES (201, 'テスト用お菓子', 100, '5個以上で5円引き、10個以上で10円引きのテスト用', 100, 1, 0, SYSDATE);

INSERT INTO items (id, name, price, description, stock, category_id, delete_flag, insert_date)
VALUES (202, 'テスト用高級飲料', 1000, 'まとめ買い割引の計算確認用', 50, 1, 0, SYSDATE);

INSERT INTO items (id, name, price, description, stock, category_id, delete_flag, insert_date)
VALUES (203, 'テスト用日用品', 500, '在庫数確認用', 20, 1, 0, SYSDATE);

-- 2. クレジットカード情報のサンプル (手動確認用)
-- 注意: アプリケーション側で暗号化(AES)を行うため、以下のデータはアプリ上では正しく復号できません。
-- アプリケーションの画面から登録することをお勧めしますが、テーブル定義の確認用として用意しています。
-- INSERT INTO credit_cards (id, holder_name, card_number, expiration_date, brand, user_id, insert_date)
-- VALUES (seq_credit_cards.NEXTVAL, 'TEST USER', 'EncryptedStringHere', '12/28', 'Visa', 1, SYSDATE);

-- 3. 確認用SELECT文
-- 商品一覧
SELECT id, name, price, stock FROM items WHERE id >= 201;

-- クレジットカード一覧
SELECT * FROM credit_cards;

COMMIT;
