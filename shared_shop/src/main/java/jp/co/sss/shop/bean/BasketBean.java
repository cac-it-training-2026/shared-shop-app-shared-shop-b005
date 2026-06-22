package jp.co.sss.shop.bean;

/**
 * 買い物かご内の商品情報クラス
 *
 * @author SystemShared
 */

public class BasketBean {

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 商品名
	 */
	private String name;

	/**
	 * 商品名(英語)
	 */
	private String nameEn;

	/**
	 * 商品名(スペイン語)
	 */
	private String nameEs;

	/**
	 * 商品名(エスペラント語)
	 */
	private String nameEo;

	/**
	 * 商品在庫数
	 */
	private Integer stock;

	/**
	 * 商品注文個数 初期値 1
	 */
	private Integer orderNum = 1;

	/**
	 * コンストラクタ
	 */
	public BasketBean() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param id  商品ID
	 * @param name  商品名
	 * @param stock 商品在庫数
	 */
	public BasketBean(Integer id, String name, Integer stock) {
		this.id = id;
		this.name = name;
		this.stock = stock;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param id  商品ID
	 * @param name  商品名
	 * @param stock  商品在庫数
	 * @param orderNum  注文個数
	 */
	public BasketBean(Integer id, String name, Integer stock, Integer orderNum) {
		this.id = id;
		this.name = name;
		this.stock = stock;
		this.orderNum = orderNum;
	}

	/**
	 * 商品IDの取得
	 * @return 商品ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 商品IDのセット
	 * @param id 商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 商品名の取得
	 * @return 商品名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 商品名のセット
	 * @param name 商品名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 商品の在庫数の取得
	 * @return 在庫数
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * 商品の在庫数のセット
	 * @param stock 在庫数
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * 買い物かごに入れている商品個数の取得
	 * @return 注文個数
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 買い物かごに入れる商品個数のセット
	 * @param orderNum 注文個数
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * 商品名(英語)の取得
	 * @return 商品名(英語)
	 */
	public String getNameEn() {
		return nameEn;
	}

	/**
	 * 商品名(英語)のセット
	 * @param nameEn 商品名(英語)
	 */
	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	/**
	 * 商品名(スペイン語)の取得
	 * @return 商品名(スペイン語)
	 */
	public String getNameEs() {
		return nameEs;
	}

	/**
	 * 商品名(スペイン語)のセット
	 * @param nameEs 商品名(スペイン語)
	 */
	public void setNameEs(String nameEs) {
		this.nameEs = nameEs;
	}

	/**
	 * 商品名(エスペラント語)の取得
	 * @return 商品名(エスペラント語)
	 */
	public String getNameEo() {
		return nameEo;
	}

	/**
	 * 商品名(エスペラント語)のセット
	 * @param nameEo 商品名(エスペラント語)
	 */
	public void setNameEo(String nameEo) {
		this.nameEo = nameEo;
	}

	/**
	 * ロケールに応じた商品名の取得（フォールバック付き）
	 * @param lang 言語コード
	 * @return ロケールに応じた商品名
	 */
	public String getName(String lang) {
		if ("en".equals(lang) && nameEn != null && !nameEn.isEmpty()) {
			return nameEn;
		}
		if ("es".equals(lang) && nameEs != null && !nameEs.isEmpty()) {
			return nameEs;
		}
		if ("eo".equals(lang) && nameEo != null && !nameEo.isEmpty()) {
			return nameEo;
		}
		return name;
	}
}

//ii
