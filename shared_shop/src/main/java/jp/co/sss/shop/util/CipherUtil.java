package jp.co.sss.shop.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 暗号化ユーティリティ (AES)
 */
public class CipherUtil {
	private static final String ALGORITHM = "AES";

	/**
	 * 暗号化キー。
	 * 本来は環境変数や設定ファイル、秘密情報管理サービスから取得すべきですが、
	 * 演習用としてソースコード内に定義しています。
	 */
	private static final String KEY = "shared-shop-key-"; // 16 bytes

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
