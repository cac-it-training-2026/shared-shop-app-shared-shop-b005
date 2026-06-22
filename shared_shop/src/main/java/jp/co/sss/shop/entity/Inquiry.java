package jp.co.sss.shop.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 問い合わせ情報のエンティティクラス
 */
@Entity
@Table(name = "inquiries")
public class Inquiry {

	/**
	 * 問い合わせID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inquiries_gen")
	@SequenceGenerator(name = "seq_inquiries_gen", sequenceName = "seq_inquiries", allocationSize = 1)
	private Integer id;

	/**
	 * 氏名
	 */
	@Column
	private String name;

	/**
	 * メールアドレス
	 */
	@Column
	private String email;

	/**
	 * 問い合わせ内容
	 */
	@Column
	private String content;

	/**
	 * 登録日時
	 */
	@Column(insertable = false, updatable = false)
	private Timestamp insertDate;

	/**
	 * 問い合わせIDの取得
	 * @return 問い合わせID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 問い合わせIDのセット
	 * @param id 問い合わせID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

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

	/**
	 * 登録日時の取得
	 * @return 登録日時
	 */
	public Timestamp getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日時のセット
	 * @param insertDate 登録日時
	 */
	public void setInsertDate(Timestamp insertDate) {
		this.insertDate = insertDate;
	}
}
