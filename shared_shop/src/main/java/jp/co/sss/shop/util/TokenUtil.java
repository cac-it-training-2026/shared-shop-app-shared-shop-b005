package jp.co.sss.shop.util;

import java.util.UUID;

/**
 * トークン生成用ユーティリティ
 */
public class TokenUtil {

    /**
     * UUIDベースのトークンを生成する
     */
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
