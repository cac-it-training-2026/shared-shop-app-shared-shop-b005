package jp.co.sss.shop.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 暗号化ユーティリティ (AES)
 */
public class CipherUtil {
	private static final String ALGORITHM = "AES";
	// 注意: 演習用のため固定キーを使用。本来は環境変数等で管理すべき。
	private static final String KEY = "1234567890123456";

	/**
	 * 文字列を暗号化する
	 */
	public static String encrypt(String value) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 文字列を復号する
	 */
	public static String decrypt(String encrypted) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return new String(original);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
