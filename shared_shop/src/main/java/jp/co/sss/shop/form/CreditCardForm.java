package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * クレジットカード情報のフォームクラス
 *
 * @author SystemShared
 */
public class CreditCardForm implements Serializable {
	/**
	 * クレジットカードID
	 */
	private Integer id;

	/**
	 * カード名義人
	 */
	@NotBlank
	@Size(max = 100)
	private String holderName;

	/**
	 * カード番号
	 */
	@NotBlank
	@Size(min = 14, max = 16)
	@Pattern(regexp = "^[0-9]+$")
	private String cardNumber;

	/**
	 * 有効期限 (MM/YY形式)
	 */
	@NotBlank
	@Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$")
	private String expirationDate;

	/**
	 * ブランド
	 */
	@NotBlank
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
