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

	public static final int FIRST_POINT = 1000;
	public static final int SECOND_POINT = 500;
	public static final int THIRD_POINT = 100;
	public static final int LOSE_POINT = 0;

	private static final Random RANDOM = new Random();

	private LotteryUtil() {
	}

	/**
	 * 抽選を実行する。
	 * @return 抽選結果
	 */
	public static LotteryResult draw() {
		int value = RANDOM.nextInt(100);

		// 1等: 5%、2等: 10%、3等: 20%、はずれ: 65%
		if (value < 5) {
			return new LotteryResult(LOTTERY_FIRST, FIRST_POINT);
		}
		if (value < 15) {
			return new LotteryResult(LOTTERY_SECOND, SECOND_POINT);
		}
		if (value < 35) {
			return new LotteryResult(LOTTERY_THIRD, THIRD_POINT);
		}
		return new LotteryResult(LOTTERY_LOSE, LOSE_POINT);
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
