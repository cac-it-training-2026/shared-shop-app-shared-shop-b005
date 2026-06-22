package jp.co.sss.shop.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * クレジットカード情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "credit_cards")
public class CreditCard {
	/**
	 * クレジットカードID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_credit_cards_gen")
	@SequenceGenerator(name = "seq_credit_cards_gen", sequenceName = "seq_credit_cards", allocationSize = 1)
	private Integer id;

	/**
	 * カード名義人
	 */
	@Column
	private String holderName;

	/**
	 * カード番号
	 */
	@Column
	private String cardNumber;

	/**
	 * 有効期限 (MM/YY形式を想定)
	 */
	@Column
	private String expirationDate;

	/**
	 * ブランド (Visa, Mastercardなど)
	 */
	@Column
	private String brand;

	/**
	 * 登録日付
	 */
	@Column(insertable = false)
	private Date insertDate;

	/**
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

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

	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public Date getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日付のセット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * 会員エンティティの取得
	 * @return 会員エンティティ
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 会員エンティティのセット
	 * @param user 会員エンティティ
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
