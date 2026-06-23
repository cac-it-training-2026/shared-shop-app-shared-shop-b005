package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * reviewsテーブル用リポジトリ
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	/**
	 * 商品IDに紐づくレビューを新着順で最大5件取得
	 */
	List<Review> findTop5ByItemIdOrderByInsertDateDesc(Integer itemId);

	/**
	 * 商品IDに紐づくレビューを評価が高い順で最大5件取得
	 */
	List<Review> findTop5ByItemIdOrderByRatingDescInsertDateDesc(Integer itemId);

	/**
	 * 商品IDに紐づく平均評価を取得
	 */
	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId")
	Double getAverageRating(@Param("itemId") Integer itemId);

	/**
	 * ユーザーIDと商品IDに紐づくレビューを取得
	 */
	Review findByUserIdAndItemId(Integer userId, Integer itemId);
}
