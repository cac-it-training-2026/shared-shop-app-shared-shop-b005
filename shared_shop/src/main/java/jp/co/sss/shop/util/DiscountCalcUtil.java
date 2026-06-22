package jp.co.sss.shop.util;

/**
 * 割引計算ユーティリティ
 */
public class DiscountCalcUtil {

	/** 割引閾値1 (5個) */
	public static final int THRESHOLD_5 = 5;
	/** 割引閾値2 (10個) */
	public static final int THRESHOLD_10 = 10;
	/** 割引率1 (5%) */
	public static final double RATE_5 = 0.05;
	/** 割引率2 (10%) */
	public static final double RATE_10 = 0.10;

	/**
	 * 商品ごとの割引率を計算する
	 * @param quantity 注文個数
	 * @return 割引率
	 */
	public static double getDiscountRate(int quantity) {
		if (quantity >= THRESHOLD_10) {
			return RATE_10;
		} else if (quantity >= THRESHOLD_5) {
			return RATE_5;
		}
		return 0.0;
	}

	/**
	 * 割引額を計算する
	 * @param price 単価
	 * @param quantity 注文個数
	 * @return 割引額
	 */
	public static int calculateDiscount(int price, int quantity) {
		double rate = getDiscountRate(quantity);
		if (rate == 0.0) {
			return 0;
		}
		return (int) (price * quantity * rate);
	}
}
