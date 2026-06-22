package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.CreditCard;

/**
 * credit_cardsテーブル用リポジトリ
 *
 * @author SystemShared
 */
@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {
	/**
	 * 会員IDを条件にクレジットカード情報を取得
	 * @param userId 会員ID
	 * @return クレジットカード情報のリスト
	 */
	List<CreditCard> findByUser_IdOrderByInsertDateDescIdDesc(Integer userId);
}
