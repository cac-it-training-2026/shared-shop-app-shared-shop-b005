/*
 * Issue2 ポイント・会員ランク・くじ機能用 追加DDL
 * 既存DBに対して1回だけ実行してください。
 */

ALTER TABLE users ADD (
    current_point NUMBER(8) DEFAULT 0 NOT NULL,
    total_point NUMBER(8) DEFAULT 0 NOT NULL,
    rank VARCHAR2(20) DEFAULT 'ブロンズ' NOT NULL
);

ALTER TABLE orders ADD (
    used_point NUMBER(8) DEFAULT 0 NOT NULL,
    earned_point NUMBER(8) DEFAULT 0 NOT NULL,
    lottery_executed NUMBER(1) DEFAULT 0 NOT NULL,
    lottery_rank VARCHAR2(20),
    lottery_point NUMBER(8) DEFAULT 0 NOT NULL
);

UPDATE users
SET current_point = 0,
    total_point = 0,
    rank = 'ブロンズ'
WHERE current_point IS NULL
   OR total_point IS NULL
   OR rank IS NULL;

UPDATE orders
SET used_point = 0,
    earned_point = 0,
    lottery_executed = 0,
    lottery_point = 0
WHERE used_point IS NULL
   OR earned_point IS NULL
   OR lottery_executed IS NULL
   OR lottery_point IS NULL;

COMMIT;
