package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
			@Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者,商品詳細機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);

	/**
	 * 商品一覧を新着順（同日の場合はIDの降順）に並び替え
	 * @author 児島涼音
	 * @param deleteFlag
	 * @return 商品一覧
	 */
	List<Item> findByDeleteFlagOrderByInsertDateDescIdDesc(int deleteFlag);

	/**
	 * 商品一覧をカテゴリ別に新着順（同日の場合はIDの降順）に並び替
	 * @author 児島涼音
	 * @param categoryId
	 * @param deleteFlag
	 * @return 商品一覧
	 */
	List<Item> findByCategoryIdAndDeleteFlagOrderByInsertDateDescIdDesc(Integer categoryId, int deleteFlag);

	/**
	 * 商品一覧を売れ筋順（同数の場合はIDの昇順）に並び替え
	 * @author 児島涼音
	 * @param deleteFlag
	 * @return 商品一覧
	 */
	@Query("SELECT i FROM Item i INNER JOIN OrderItem oi ON i.id = oi.item.id WHERE i.deleteFlag = :deleteFlag GROUP BY i ORDER BY COUNT(oi.item.id) DESC, i.id ASC")
	List<Item> findHotItems(@Param("deleteFlag") int deleteFlag);

	/** 商品一覧をカテゴリ別に売れ筋順（同数の場合はIDの昇順）で並び替え
	 * @author 児島涼音
	 * @param categoryId
	 * @param deleteFlag
	 * @return 商品一覧
	 */
	@Query("SELECT i FROM Item i INNER JOIN OrderItem oi ON i.id = oi.item.id WHERE i.deleteFlag = :deleteFlag AND i.category.id = :categoryId GROUP BY i ORDER BY COUNT(oi.item.id) DESC, i.id ASC")
	List<Item> findHotItemsByCategory(@Param("categoryId") Integer categoryId, @Param("deleteFlag") int deleteFlag);

	@Override
	Item getReferenceById(Integer id);

	/**
	 * お気に入り数順の商品一覧取得
	 */
	@Query("SELECT i FROM Item i LEFT JOIN Favorite f ON i.id = f.item.id WHERE i.deleteFlag = :deleteFlag GROUP BY i ORDER BY COUNT(f.id) DESC, i.id ASC")
	List<Item> findItemsByFavorite(@Param("deleteFlag") int deleteFlag);

	/**
	 * カテゴリ指定ありのお気に入り数順の商品一覧取得
	 */
	@Query("SELECT i FROM Item i LEFT JOIN Favorite f ON i.id = f.item.id WHERE i.deleteFlag = :deleteFlag AND i.category.id = :categoryId GROUP BY i ORDER BY COUNT(f.id) DESC, i.id ASC")
	List<Item> findItemsByCategoryByFavorite(@Param("categoryId") Integer categoryId, @Param("deleteFlag") int deleteFlag);

	/**
	 * 同一カテゴリのレコメンド商品を取得（購入商品自身を除外、最大5件）
	 */
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.id != :itemId AND i.deleteFlag = 0 ORDER BY i.insertDate DESC")
	List<Item> findRecommendItems(@Param("categoryId") Integer categoryId, @Param("itemId") Integer itemId, Pageable pageable);

}
