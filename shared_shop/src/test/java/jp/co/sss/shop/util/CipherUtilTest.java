package jp.co.sss.shop.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CipherUtilTest {

    @Test
    public void testEncryptDecrypt() {
        String original = "1234567890123456";
        String encrypted = CipherUtil.encrypt(original);
        assertNotEquals(original, encrypted);
        String decrypted = CipherUtil.decrypt(encrypted);
        assertEquals(original, decrypted);
    }
}
