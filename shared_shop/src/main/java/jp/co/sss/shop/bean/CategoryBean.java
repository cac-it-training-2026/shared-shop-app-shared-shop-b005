package jp.co.sss.shop.bean;

/**
 * カテゴリ情報クラス
 *
 * @author SystemShared
 */
public class CategoryBean {

	/**
	 * カテゴリID
	 */
	private Integer id;
	/**
	 * カテゴリ名
	 */
	private String name;
	/**
	 * カテゴリ名(英語)
	 */
	private String nameEn;
	/**
	 * カテゴリ名(スペイン語)
	 */
	private String nameEs;
	/**
	 * カテゴリ名(エスペラント語)
	 */
	private String nameEo;
	/**
	 * カテゴリ説明
	 */
	private String description;

	/**
	 * カテゴリIDの取得
	 * @return カテゴリID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * カテゴリIDのセット
	 * @param id カテゴリID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * カテゴリ名の取得
	 * @return カテゴリ名
	 */
	public String getName() {
		return name;
	}

	/**
	 * カテゴリ名のセット
	 * @param name カテゴリ名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * カテゴリ名(英語)の取得
	 * @return カテゴリ名(英語)
	 */
	public String getNameEn() {
		return nameEn;
	}

	/**
	 * カテゴリ名(英語)のセット
	 * @param nameEn カテゴリ名(英語)
	 */
	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	/**
	 * カテゴリ名(スペイン語)の取得
	 * @return カテゴリ名(スペイン語)
	 */
	public String getNameEs() {
		return nameEs;
	}

	/**
	 * カテゴリ名(スペイン語)のセット
	 * @param nameEs カテゴリ名(スペイン語)
	 */
	public void setNameEs(String nameEs) {
		this.nameEs = nameEs;
	}

	/**
	 * カテゴリ名(エスペラント語)の取得
	 * @return カテゴリ名(エスペラント語)
	 */
	public String getNameEo() {
		return nameEo;
	}

	/**
	 * カテゴリ名(エスペラント語)のセット
	 * @param nameEo カテゴリ名(エスペラント語)
	 */
	public void setNameEo(String nameEo) {
		this.nameEo = nameEo;
	}

	/**
	 * カテゴリ説明文の取得
	 * @return カテゴリ説明文
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * カテゴリ説明文のセット
	 * @param description カテゴリ説明文
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * ロケールに応じたカテゴリ名の取得（フォールバック付き）
	 * @param lang 言語コード
	 * @return ロケールに応じたカテゴリ名
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
