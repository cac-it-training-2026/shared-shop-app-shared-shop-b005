package jp.co.sss.shop.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import jp.co.sss.shop.entity.CreditCard;

public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {
	List<CreditCard> findByUser_IdOrderByInsertDateDescIdDesc(Integer userId);
}
