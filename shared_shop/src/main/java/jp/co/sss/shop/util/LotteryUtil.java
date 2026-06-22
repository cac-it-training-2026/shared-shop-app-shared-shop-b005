package jp.co.sss.shop.util;

import java.util.Random;

/**
 * くじ抽選用ユーティリティクラス
 */
public class LotteryUtil {

	/** 抽選結果: 1等 */
	public static final int LOTTERY_RANK_1 = 1;
	/** 抽選結果: 2等 */
	public static final int LOTTERY_RANK_2 = 2;
	/** 抽選結果: 3等 */
	public static final int LOTTERY_RANK_3 = 3;
	/** 抽選結果: はずれ */
	public static final int LOTTERY_RANK_MISS = 4;

	/** 1等のポイント */
	public static final int POINT_1ST = 1000;
	/** 2等のポイント */
	public static final int POINT_2ND = 500;
	/** 3等のポイント */
	public static final int POINT_3RD = 100;
	/** はずれのポイント */
	public static final int POINT_MISS = 0;

	private static final Random random = new Random();

	/**
	 * くじを引く
	 * @return 抽選結果 (1〜4)
	 */
	public static int doLottery() {
		int val = random.nextInt(100); // 0-99
		if (val < 5) { // 5%
			return LOTTERY_RANK_1;
		} else if (val < 15) { // 10%
			return LOTTERY_RANK_2;
		} else if (val < 40) { // 25%
			return LOTTERY_RANK_3;
		} else { // 60%
			return LOTTERY_RANK_MISS;
		}
	}

	/**
	 * 抽選結果に応じたポイントを取得する
	 * @param rank 抽選結果
	 * @return 付与ポイント
	 */
	public static int getLotteryPoint(int rank) {
		switch (rank) {
			case LOTTERY_RANK_1:
				return POINT_1ST;
			case LOTTERY_RANK_2:
				return POINT_2ND;
			case LOTTERY_RANK_3:
				return POINT_3RD;
			default:
				return POINT_MISS;
		}
	}

	/**
	 * 抽選結果の名称を取得する
	 * @param rank 抽選結果
	 * @return 結果名称
	 */
	public static String getLotteryRankName(int rank) {
		switch (rank) {
			case LOTTERY_RANK_1:
				return "1等";
			case LOTTERY_RANK_2:
				return "2等";
			case LOTTERY_RANK_3:
				return "3等";
			default:
				return "はずれ";
		}
	}
}
