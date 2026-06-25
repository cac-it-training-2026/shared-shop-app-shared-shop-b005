package jp.co.sss.shop.bean;

/**
 * 商品情報クラス
 *
 * @author SystemShared
 */
public class ItemBean {

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
	 * 価格
	 */
	private Integer price;

	/**
	 * 商品説明
	 */
	private String description;

	/**
	 * 商品説明(英語)
	 */
	private String descriptionEn;

	/**
	 * 商品説明(スペイン語)
	 */
	private String descriptionEs;

	/**
	 * 商品説明(エスペラント語)
	 */
	private String descriptionEo;

	/**
	 * 在庫数
	 */
	private Integer stock;

	/**
	 * 商品画像ファイル名
	 */
	private String image;

	/**
	 * カテゴリID
	 */
	private Integer categoryId;

	/**
	 * カテゴリ名
	 */
	private String categoryName;

	/**
	 * カテゴリ名(英語)
	 */
	private String categoryNameEn;

	/**
	 * カテゴリ名(スペイン語)
	 */
	private String categoryNameEs;

	/**
	 * カテゴリ名(エスペラント語)
	 */
	private String categoryNameEo;

	/**
	 * お気に入り登録数
	 */
	private long favoriteCount;

	/**
	 * ログインユーザーがお気に入り登録済みかどうか
	 */
	private boolean favorite;

	/**
	 * 商品ID取得
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
	 * 商品単価の取得
	 * @return 商品単価
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * 商品単価のセット
	 * @param price 商品単価
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}

	/**
	 * 商品の説明文の取得
	 * @return 商品の説明文
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 商品の説明文のセット
	 * @param description 商品の説明文
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 商品説明(英語)の取得
	 * @return 商品説明(英語)
	 */
	public String getDescriptionEn() {
		return descriptionEn;
	}

	/**
	 * 商品説明(英語)のセット
	 * @param descriptionEn 商品説明(英語)
	 */
	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	/**
	 * 商品説明(スペイン語)の取得
	 * @return 商品説明(スペイン語)
	 */
	public String getDescriptionEs() {
		return descriptionEs;
	}

	/**
	 * 商品説明(スペイン語)のセット
	 * @param descriptionEs 商品説明(スペイン語)
	 */
	public void setDescriptionEs(String descriptionEs) {
		this.descriptionEs = descriptionEs;
	}

	/**
	 * 商品説明(エスペラント語)の取得
	 * @return 商品説明(エスペラント語)
	 */
	public String getDescriptionEo() {
		return descriptionEo;
	}

	/**
	 * 商品説明(エスペラント語)のセット
	 * @param descriptionEo 商品説明(エスペラント語)
	 */
	public void setDescriptionEo(String descriptionEo) {
		this.descriptionEo = descriptionEo;
	}

	/**
	 * 在庫数の取得
	 * @return 在庫数
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * 在庫数のセット
	 * @param stock 在庫数
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * 画像ファイル名の取得
	 * @return 画像ファイル名
	 */
	public String getImage() {
		return image;
	}

	/**
	 * 画像ファイル名のセット
	 * @param image 画像ファイル名
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * カテゴリIDの取得
	 * @return カテゴリID
	 */
	public Integer getCategoryId() {
		return categoryId;
	}

	/**
	 * カテゴリIDのセット
	 * @param categoryId カテゴリID
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * カテゴリ名の取得
	 * @return カテゴリ名
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * カテゴリ名のセット
	 * @param categoryName カテゴリ名
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * カテゴリ名(英語)の取得
	 * @return カテゴリ名(英語)
	 */
	public String getCategoryNameEn() {
		return categoryNameEn;
	}

	/**
	 * カテゴリ名(英語)のセット
	 * @param categoryNameEn カテゴリ名(英語)
	 */
	public void setCategoryNameEn(String categoryNameEn) {
		this.categoryNameEn = categoryNameEn;
	}

	/**
	 * カテゴリ名(スペイン語)の取得
	 * @return カテゴリ名(スペイン語)
	 */
	public String getCategoryNameEs() {
		return categoryNameEs;
	}

	/**
	 * カテゴリ名(スペイン語)のセット
	 * @param categoryNameEs カテゴリ名(スペイン語)
	 */
	public void setCategoryNameEs(String categoryNameEs) {
		this.categoryNameEs = categoryNameEs;
	}

	/**
	 * カテゴリ名(エスペラント語)の取得
	 * @return カテゴリ名(エスペラント語)
	 */
	public String getCategoryNameEo() {
		return categoryNameEo;
	}

	/**
	 * カテゴリ名(エスペラント語)のセット
	 * @param categoryNameEo カテゴリ名(エスペラント語)
	 */
	public void setCategoryNameEo(String categoryNameEo) {
		this.categoryNameEo = categoryNameEo;
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

	/**
	 * ロケールに応じた商品説明の取得（フォールバック付き）
	 * @param lang 言語コード
	 * @return ロケールに応じた商品説明
	 */
	public String getDescription(String lang) {
		if ("en".equals(lang) && descriptionEn != null && !descriptionEn.isEmpty()) {
			return descriptionEn;
		}
		if ("es".equals(lang) && descriptionEs != null && !descriptionEs.isEmpty()) {
			return descriptionEs;
		}
		if ("eo".equals(lang) && descriptionEo != null && !descriptionEo.isEmpty()) {
			return descriptionEo;
		}
		return description;
	}

	/**
	 * ロケールに応じたカテゴリ名の取得（フォールバック付き）
	 * @param lang 言語コード
	 * @return ロケールに応じたカテゴリ名
	 */
	public String getCategoryName(String lang) {
		if ("en".equals(lang) && categoryNameEn != null && !categoryNameEn.isEmpty()) {
			return categoryNameEn;
		}
		if ("es".equals(lang) && categoryNameEs != null && !categoryNameEs.isEmpty()) {
			return categoryNameEs;
		}
		if ("eo".equals(lang) && categoryNameEo != null && !categoryNameEo.isEmpty()) {
			return categoryNameEo;
		}
		return categoryName;
	}

	/**
	 * お気に入り登録数の取得
	 * @return お気に入り登録数
	 */
	public long getFavoriteCount() {
		return favoriteCount;
	}

	/**
	 * お気に入り登録数のセット
	 * @param favoriteCount お気に入り登録数
	 */
	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	/**
	 * ログインユーザーがお気に入り登録済みかどうかの取得
	 * @return お気に入り登録済みの場合 true
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * ログインユーザーがお気に入り登録済みかどうかのセット
	 * @param favorite お気に入り登録済みの場合 true
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

}
