package jp.co.sss.shop.util;

/**
 * ポイント計算・ランク判定用ユーティリティクラス
 */
public class PointCalcUtil {

	/** ポイント付与レート (100円につき1ポイント) */
	public static final int POINT_RATE = 100;

	/** ランク閾値: シルバー */
	public static final int RANK_THRESHOLD_SILVER = 1000;

	/** ランク閾値: ゴールド */
	public static final int RANK_THRESHOLD_GOLD = 5000;

	/** ランク値 */
	public static final int RANK_BRONZE = 0;
	public static final int RANK_SILVER = 1;
	public static final int RANK_GOLD = 2;

	/**
	 * 注文金額から獲得ポイントを計算する
	 * @param amount 注文金額
	 * @return 獲得ポイント
	 */
	public static int calculateEarnedPoint(int amount) {
		return amount / POINT_RATE;
	}

	/**
	 * 累計獲得ポイントから会員ランクを判定する
	 * @param totalPoint 累計獲得ポイント
	 * @return 会員ランク (0:ブロンズ, 1:シルバー, 2:ゴールド)
	 */
	public static int judgeRank(int totalPoint) {
		if (totalPoint >= RANK_THRESHOLD_GOLD) {
			return RANK_GOLD;
		} else if (totalPoint >= RANK_THRESHOLD_SILVER) {
			return RANK_SILVER;
		} else {
			return RANK_BRONZE;
		}
	}

	/**
	 * 会員ランクの名称を取得する
	 * @param rank 会員ランク値
	 * @return ランク名
	 */
	public static String getRankName(int rank) {
		switch (rank) {
			case RANK_GOLD:
				return "ゴールド";
			case RANK_SILVER:
				return "シルバー";
			default:
				return "ブロンズ";
		}
	}
}
