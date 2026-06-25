package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 問い合わせフォーム
 */
public class InquiryForm implements Serializable {

	/**
	 * 氏名
	 */
	@NotBlank(message = "{inquiryForm.name.required}")
	@Size(max = 30, message = "{inquiryForm.name.size}")
	private String name;

	/**
	 * メールアドレス
	 */
	@NotBlank(message = "{inquiryForm.email.required}")
	@Email(message = "{inquiryForm.email.format}")
	@Size(max = 255, message = "{inquiryForm.email.size}")
	private String email;

	/**
	 * 問い合わせ内容
	 */
	@NotBlank(message = "{inquiryForm.content.required}")
	@Size(max = 1000, message = "{inquiryForm.content.size}")
	private String content;

	/**
	 * 氏名の取得
	 * @return 氏名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 氏名のセット
	 * @param name 氏名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * メールアドレスの取得
	 * @return メールアドレス
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * メールアドレスのセット
	 * @param email メールアドレス
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 問い合わせ内容の取得
	 * @return 問い合わせ内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 問い合わせ内容のセット
	 * @param content 問い合わせ内容
	 */
	public void setContent(String content) {
		this.content = content;
	}
}
