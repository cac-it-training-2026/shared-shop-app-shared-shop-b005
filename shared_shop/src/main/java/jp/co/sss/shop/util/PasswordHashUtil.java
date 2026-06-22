package jp.co.sss.shop.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * パスワードハッシュ化用ユーティリティ
 */
public class PasswordHashUtil {

    /**
     * SHA-256でハッシュ化する
     */
    public static String hash(String password) {
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
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
