package jp.co.sss.shop.entity;

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
 * 注文商品情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

	/**
	 * 注文商品ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_order_items_gen")
	@SequenceGenerator(name = "seq_order_items_gen", sequenceName = "seq_order_items", allocationSize = 1)
	private Integer id;

	/**
	 * 注文個数
	 */
	@Column
	private Integer quantity;

	/**
	 * 注文情報
	 */
	@ManyToOne
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private Order order;

	/**
	 * 商品情報
	 */
	@ManyToOne
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	/**
	 * 注文時点商品単価
	 */
	@Column
	private int price;

	/**
	 * 注文時点商品名(英語)
	 */
	@Column
	private String nameEn;

	/**
	 * 注文時点商品名(スペイン語)
	 */
	@Column
	private String nameEs;

	/**
	 * 注文時点商品名(エスペラント語)
	 */
	@Column
	private String nameEo;

	/**
	 * 注文商品IDの取得
	 * @return 注文商品ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 注文商品IDのセット
	 * @param id 注文商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文個数の取得
	 * @return 注文個数
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * 注文個数のセット
	 * @param quantity 注文個数
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * 注文エンティティの取得
	 * @return 注文エンティティ
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * 注文エンティティのセット
	 * @param order 注文エンティティ
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 商品エンティティの取得
	 * @return 商品エンティティ
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * 商品エンティティのセット
	 * @param item 商品エンティティ
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * 注文時商品単価の取得
	 * @return 注文時商品単価
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * 注文時商品単価のセット
	 * @param price 注文時商品単価
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * 注文時点商品名(英語)の取得
	 * @return 注文時点商品名(英語)
	 */
	public String getNameEn() {
		return nameEn;
	}

	/**
	 * 注文時点商品名(英語)のセット
	 * @param nameEn 注文時点商品名(英語)
	 */
	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	/**
	 * 注文時点商品名(スペイン語)の取得
	 * @return 注文時点商品名(スペイン語)
	 */
	public String getNameEs() {
		return nameEs;
	}

	/**
	 * 注文時点商品名(スペイン語)のセット
	 * @param nameEs 注文時点商品名(スペイン語)
	 */
	public void setNameEs(String nameEs) {
		this.nameEs = nameEs;
	}

	/**
	 * 注文時点商品名(エスペラント語)の取得
	 * @return 注文時点商品名(エスペラント語)
	 */
	public String getNameEo() {
		return nameEo;
	}

	/**
	 * 注文時点商品名(エスペラント語)のセット
	 * @param nameEo 注文時点商品名(エスペラント語)
	 */
	public void setNameEo(String nameEo) {
		this.nameEo = nameEo;
	}

}
