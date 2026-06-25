package jp.co.sss.shop.form;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreditCardForm implements Serializable {
	private Integer id;

	@NotBlank
	private String holderName;

	@NotBlank
	@Pattern(regexp = "^[0-9]{14,16}$", message = "カード番号は14〜16桁の数字で入力してください")
	private String cardNumber;

	@NotBlank
	@Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "有効期限は MM/YY 形式で入力してください")
	private String expirationDate;

	@NotBlank
	private String brand;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
}
