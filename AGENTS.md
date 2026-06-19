このSpring Boot製ECサイトを解析した結果、概要は以下の通りです。

# システム概要

**会員制ECサイト（ネットショップ）** を実装したWebアプリケーションです。

* 商品閲覧
* 会員登録・ログイン
* カート機能
* 注文機能
* 管理者による商品・会員・カテゴリ管理

を備えています。

---

# 使用技術

* Java 17
* Spring Boot 4
* Spring MVC
* Spring Data JPA
* Thymeleaf
* Oracle Database（ojdbc11）
* Bean Validation

アーキテクチャは典型的な

```
Controller
 ↓
Service相当の処理
 ↓
Repository
 ↓
Entity
 ↓
Database
```

のMVC構成です。

---

# 主な機能

## 1. ログイン機能

* ログイン
* ログアウト
* セッション管理

関連Controller

* LoginController
* LogoutController

---

## 2. 会員機能

一般ユーザー向け

* 新規会員登録
* 会員情報参照
* 会員情報変更
* 退会

関連Controller

* ClientUserRegistController
* ClientUserShowController
* ClientUserUpdateController
* ClientUserDeleteController

---

## 3. 商品機能

* 商品一覧表示
* 商品詳細表示
* 商品検索

関連Controller

* ClientItemShowController

---

## 4. カート機能

* 商品をカートへ追加
* 数量変更
* 商品削除
* カート内容確認

関連Controller

* ClientBasketController

---

## 5. 注文機能

* 配送先入力
* 支払方法選択
* 注文確定
* 注文履歴確認

関連Controller

* ClientOrderRegistController
* ClientOrderShowController

---

# 管理者機能

## 商品管理

* 商品一覧
* 商品登録
* 商品編集
* 商品削除

関連Controller

* AdminItemShowController
* AdminItemRegistController
* AdminItemUpdateController
* AdminItemDeleteController

---

## カテゴリ管理

* カテゴリ一覧
* カテゴリ登録
* カテゴリ編集
* カテゴリ削除

関連Controller

* AdminCategoryShowController
* AdminCategoryRegistController
* AdminCategoryUpdateController
* AdminCategoryDeleteController

---

## 会員管理

* 会員一覧
* 会員登録
* 会員編集
* 会員削除

関連Controller

* AdminUserShowController
* AdminUserRegistController
* AdminUserUpdateController
* AdminUserDeleteController

---

## 注文管理

* 注文一覧
* 注文詳細確認

関連Controller

* AdminOrderShowController

---

# データベース設計

主要エンティティは5つです。

## User（会員）

保持情報

* メールアドレス
* パスワード
* 氏名
* 郵便番号
* 住所
* 電話番号
* 権限（管理者/一般会員）

---

## Category（カテゴリ）

商品分類情報

例

* 食品
* 家電
* 書籍

---

## Item（商品）

保持情報

* 商品名
* 価格
* 商品説明
* 在庫数
* 商品画像
* カテゴリ

関係

```
Category
   ↓
 Item
```

（多対1）

---

## Order（注文）

保持情報

* 配送先
* 支払方法
* 注文日時
* 注文者

関係

```
User
 ↓
Order
```

（多対1）

---

## OrderItem（注文商品）

注文と商品の中間テーブル

関係

```
Order
  ↓
OrderItem
  ↑
 Item
```

注文ごとの購入商品と数量を管理します。

---

# 特徴

このシステムは学習用ECサイトとして非常に典型的な構成で、

* Spring MVC
* JPAのEntity関連付け（@ManyToOne、@OneToMany）
* セッション管理
* 入力チェック（Validation）
* 管理者画面と一般ユーザー画面の分離

など、Spring Bootの実務的な基本機能を一通り学べる内容になっています。

一言でまとめると、

**「会員管理・商品管理・カート・注文機能を備えた、管理者画面付きの標準的なSpring Boot製ECサイト」**

です。

---
# 開発時の禁止事項

* pom.xmlの変更を生じる新たなライブラリの導入は禁止（フロントエンドのCDNはOK）
* サービスレイヤは含まず、基礎的なＭＶＣで構成する（Controllerから直接JPAリポジトリのメソッドを呼んでいる）
* コーディング規約はクラスはUpperキャメル、メソッドはlowerキャメルなど原則、Google Java Styleに準拠する
---
# Julesのルール

* セッション内のやり取り、プルリクエストの内容は日本語で記述して下さい
* 画面に変更がある場合はマルチモーダルで視覚的に示してください
---
