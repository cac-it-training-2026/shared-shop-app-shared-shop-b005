package jp.co.sss.shop.util;

import java.util.Random;

/**
 * 二段階認証コード生成用ユーティリティクラス
 */
public class AuthCodeUtil {

    /**
     * 4桁のランダムな認証コードを生成する
     * @return 4桁の認証コード（文字列）
     */
    public static String generateAuthCode() {
        Random random = new Random();
        int code = random.nextInt(10000);
        return String.format("%04d", code);
    }
}
