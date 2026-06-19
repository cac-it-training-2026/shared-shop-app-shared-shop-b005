package jp.co.sss.shop.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * パスワードハッシュ化用ユーティリティクラス
 */
public class PasswordHashUtil {

    /**
     * パスワードをSHA-256でハッシュ化する
     * @param password 平文パスワード
     * @return ハッシュ化されたパスワード（16進数文字列）
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found", e);
        }
    }
}
