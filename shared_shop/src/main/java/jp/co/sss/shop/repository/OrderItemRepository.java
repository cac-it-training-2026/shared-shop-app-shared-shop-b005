package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.OrderItem;

/**
 * order_itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	/**
	 * 指定ユーザーが指定商品を購入済みか判定
	 */
	@Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.order.user.id = :userId AND oi.item.id = :itemId")
	boolean existsByUserIdAndItemId(@Param("userId") Integer userId, @Param("itemId") Integer itemId);
}
