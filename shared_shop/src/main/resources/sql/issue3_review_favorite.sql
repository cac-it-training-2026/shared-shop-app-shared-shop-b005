-- reviews テーブル
CREATE TABLE reviews (
    id          NUMBER(10)      PRIMARY KEY,
    user_id     NUMBER(10)      NOT NULL REFERENCES users(id),
    item_id     NUMBER(10)      NOT NULL REFERENCES items(id),
    rating      NUMBER(1)       NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     VARCHAR2(1000)   NOT NULL,
    insert_date DATE            DEFAULT SYSDATE NOT NULL,
    CONSTRAINT unique_user_item_review UNIQUE (user_id, item_id)
);

-- favorites テーブル
CREATE TABLE favorites (
    id          NUMBER(10)      PRIMARY KEY,
    user_id     NUMBER(10)      NOT NULL REFERENCES users(id),
    item_id     NUMBER(10)      NOT NULL REFERENCES items(id),
    insert_date DATE            DEFAULT SYSDATE NOT NULL,
    CONSTRAINT unique_user_item_favorite UNIQUE (user_id, item_id)
);

-- シーケンス
CREATE SEQUENCE seq_reviews START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_favorites START WITH 1 INCREMENT BY 1;
