package jp.co.sss.shop.util;

import java.util.UUID;

/**
 * トークン生成用ユーティリティクラス
 */
public class TokenUtil {

    /**
     * ランダムなトークンを生成する
     * @return 生成されたトークン（文字列）
     */
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
