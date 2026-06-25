package jp.co.sss.shop.util;

/**
 * ポイント計算・会員ランク判定用ユーティリティクラス
 */
public class PointCalcUtil {

	/** 何円ごとに1ポイント付与するか */
	public static final int POINT_RATE_PRICE = 100;

	/** 初期ランク */
	public static final String RANK_BRONZE = "ブロンズ";
	/** シルバーランク */
	public static final String RANK_SILVER = "シルバー";
	/** ゴールドランク */
	public static final String RANK_GOLD = "ゴールド";

	/** シルバーランクに必要な累計獲得ポイント */
	public static final int SILVER_BORDER = 1000;
	/** ゴールドランクに必要な累計獲得ポイント */
	public static final int GOLD_BORDER = 5000;

	private PointCalcUtil() {
	}

	/**
	 * 注文金額から付与ポイントを計算する。
	 * 100円ごとに1ポイント付与する。
	 * @param paymentTotal ポイント利用後の支払金額
	 * @return 付与ポイント
	 */
	public static int calcEarnedPoint(int paymentTotal) {
		if (paymentTotal <= 0) {
			return 0;
		}
		return paymentTotal / POINT_RATE_PRICE;
	}

	/**
	 * 商品合計金額から利用ポイントを差し引いた支払金額を計算する。
	 * @param total 商品合計金額
	 * @param usedPoint 利用ポイント
	 * @return 支払金額
	 */
	public static int calcPaymentTotal(int total, Integer usedPoint) {
		int point = nvl(usedPoint);
		int paymentTotal = total - point;
		return paymentTotal < 0 ? 0 : paymentTotal;
	}

	/**
	 * 累計獲得ポイントから会員ランクを判定する。
	 * @param totalPoint 累計獲得ポイント
	 * @return 会員ランク
	 */
	public static String judgeRank(Integer totalPoint) {
		int point = nvl(totalPoint);
		if (point >= GOLD_BORDER) {
			return RANK_GOLD;
		}
		if (point >= SILVER_BORDER) {
			return RANK_SILVER;
		}
		return RANK_BRONZE;
	}

	/**
	 * nullの場合は0として扱う。
	 * @param value 数値
	 * @return nullなら0、それ以外は元の値
	 */
	public static int nvl(Integer value) {
		return value == null ? 0 : value;
	}
}
