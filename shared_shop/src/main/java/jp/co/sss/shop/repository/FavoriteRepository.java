package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.User;

/**
 * favoritesテーブル用リポジトリ
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

	/**
	 * ユーザーIDと商品IDに紐づくお気に入りを取得
	 */
	Favorite findByUserIdAndItemId(Integer userId, Integer itemId);

	/**
	 * ユーザーIDに紐づくお気に入り一覧を取得
	 */
	List<Favorite> findByUserIdOrderByInsertDateDesc(Integer userId);

	/**
	 * 商品IDに紐づくお気に入り数を取得
	 */
	long countByItemId(Integer itemId);

	/**
	 * 商品IDに紐づくお気に入り登録ユーザーを取得
	 */
	@Query("SELECT f.user FROM Favorite f WHERE f.item.id = :itemId")
	List<User> findUsersByItemId(@Param("itemId") Integer itemId);
}
