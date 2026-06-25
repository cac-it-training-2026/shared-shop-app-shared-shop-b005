package jp.co.sss.shop.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * 暗号化ユーティリティクラス (AESを使用)
 */
@Component
public class CipherUtil {
	private static final String ALGORITHM = "AES";

	@Value("${cipher.secret-key:S3cr3tK3yF0rSh0p}")
	private String secretKey;

	private static String KEY;

	@PostConstruct
	public void init() {
		KEY = secretKey;
	}

	/**
	 * テスト用などのためにキーを直接設定する
	 */
	public static void setKey(String key) {
		KEY = key;
	}

	/**
	 * 文字列を暗号化する
	 */
	public static String encrypt(String str) {
		if (str == null) return null;
		try {
			SecretKeySpec sks = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, sks);
			byte[] encrypted = cipher.doFinal(str.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("Encryption failed", e);
		}
	}

	/**
	 * 暗号化された文字列を復号する
	 */
	public static String decrypt(String str) {
		if (str == null) return null;
		try {
			SecretKeySpec sks = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, sks);
			byte[] decoded = Base64.getDecoder().decode(str);
			byte[] decrypted = cipher.doFinal(decoded);
			return new String(decrypted);
		} catch (Exception e) {
			throw new RuntimeException("Decryption failed", e);
		}
	}
}
