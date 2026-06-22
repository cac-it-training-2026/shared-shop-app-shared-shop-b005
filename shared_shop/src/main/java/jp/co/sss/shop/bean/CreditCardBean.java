package jp.co.sss.shop.bean;

import java.io.Serializable;

/**
 * クレジットカード情報のBeanクラス
 *
 * @author SystemShared
 */
public class CreditCardBean implements Serializable {
	/**
	 * クレジットカードID
	 */
	private Integer id;

	/**
	 * カード名義人
	 */
	private String holderName;

	/**
	 * カード番号 (マスク表示用)
	 */
	private String cardNumber;

	/**
	 * 有効期限
	 */
	private String expirationDate;

	/**
	 * ブランド
	 */
	private String brand;

	/**
	 * IDの取得
	 * @return ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * IDのセット
	 * @param id ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * カード名義人の取得
	 * @return カード名義人
	 */
	public String getHolderName() {
		return holderName;
	}

	/**
	 * カード名義人のセット
	 * @param holderName カード名義人
	 */
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	/**
	 * カード番号の取得
	 * @return カード番号
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * カード番号のセット
	 * @param cardNumber カード番号
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * 有効期限の取得
	 * @return 有効期限
	 */
	public String getExpirationDate() {
		return expirationDate;
	}

	/**
	 * 有効期限のセット
	 * @param expirationDate 有効期限
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * ブランドの取得
	 * @return ブランド
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * ブランドのセット
	 * @param brand ブランド
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}
}
