package jp.co.sss.shop.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * レビュー入力フォーム
 */
public class ReviewForm {

	/**
	 * 商品ID
	 */
	@NotNull
	private Integer itemId;

	/**
	 * 評価 (1-5)
	 */
	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;

	/**
	 * コメント
	 */
	@NotBlank
	@Size(max = 400)
	private String comment;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
