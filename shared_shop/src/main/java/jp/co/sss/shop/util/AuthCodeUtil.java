package jp.co.sss.shop.util;

import java.util.Random;

/**
 * 二段階認証コード生成用ユーティリティ
 */
public class AuthCodeUtil {

    /**
     * 4桁のランダムな数字コードを生成する
     */
    public static String generate() {
        Random random = new Random();
        int code = random.nextInt(10000);
        return String.format("%04d", code);
    }
}
