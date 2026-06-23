package jp.co.sss.shop.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CipherUtilTest {
	@BeforeAll
	public static void setup() {
		CipherUtil.setKey("S3cr3tK3yF0rSh0p");
	}

	@Test
	public void testEncryptDecrypt() {
		String original = "1234567890123456";
		String encrypted = CipherUtil.encrypt(original);
		assertNotEquals(original, encrypted);
		String decrypted = CipherUtil.decrypt(encrypted);
		assertEquals(original, decrypted);
	}
}
