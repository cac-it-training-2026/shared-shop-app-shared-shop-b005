package jp.co.sss.shop.util;

/**
 * 割引計算ユーティリティクラス
 */
public class DiscountCalcUtil {
	/**
	 * 購入個数に応じた割引額を計算する
	 * 5個以上: 5%, 10個以上: 10%
	 */
	public static int calculateDiscount(int unitPrice, int quantity) {
		double discountRate = 0;
		if (quantity >= 10) {
			discountRate = 0.10;
		} else if (quantity >= 5) {
			discountRate = 0.05;
		}

		if (discountRate > 0) {
			return (int) Math.floor(unitPrice * quantity * discountRate);
		}
		return 0;
	}
}
