package jp.co.sss.shop.util;

import java.util.Random;

/**
 * 注文完了後のくじ抽選用ユーティリティクラス
 */
public class LotteryUtil {

	public static final String LOTTERY_FIRST = "1等";
	public static final String LOTTERY_SECOND = "2等";
	public static final String LOTTERY_THIRD = "3等";
	public static final String LOTTERY_LOSE = "はずれ";

	private static final Random RANDOM = new Random();

	private LotteryUtil() {
	}

	/**
	 * 抽選を実行する。
	 * 購入金額に応じてポイントを付与する。
	 * 1等: 購入金額の20%
	 * 2等: 購入金額の10%
	 * 3等: 購入金額の5%
	 * はずれ: 0ポイント
	 *
	 * @param paymentTotal ポイント利用後の支払金額
	 * @return 抽選結果
	 */
	public static LotteryResult draw(int paymentTotal) {
		int value = RANDOM.nextInt(100);

		if (paymentTotal <= 0) {
			return new LotteryResult(LOTTERY_LOSE, 0);
		}

		if (value < 5) {
			return new LotteryResult(LOTTERY_FIRST, paymentTotal * 20 / 100);
		}
		if (value < 15) {
			return new LotteryResult(LOTTERY_SECOND, paymentTotal * 10 / 100);
		}
		if (value < 35) {
			return new LotteryResult(LOTTERY_THIRD, paymentTotal * 5 / 100);
		}
		return new LotteryResult(LOTTERY_LOSE, 0);
	}

	/**
	 * 抽選結果を保持するクラス
	 */
	public static class LotteryResult {
		private String rank;
		private int point;

		public LotteryResult(String rank, int point) {
			this.rank = rank;
			this.point = point;
		}

		public String getRank() {
			return rank;
		}

		public int getPoint() {
			return point;
		}
	}
}