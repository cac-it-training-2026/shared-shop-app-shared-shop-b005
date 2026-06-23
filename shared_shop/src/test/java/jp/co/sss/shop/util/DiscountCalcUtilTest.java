package jp.co.sss.shop.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DiscountCalcUtilTest {
	@Test
	public void testCalculateDiscount() {
		assertEquals(0, DiscountCalcUtil.calculateDiscount(100, 4));
		assertEquals(25, DiscountCalcUtil.calculateDiscount(100, 5));
		assertEquals(45, DiscountCalcUtil.calculateDiscount(100, 9));
		assertEquals(100, DiscountCalcUtil.calculateDiscount(100, 10));
	}
}
