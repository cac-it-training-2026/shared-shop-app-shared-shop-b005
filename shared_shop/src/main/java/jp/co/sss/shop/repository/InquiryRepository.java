package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Inquiry;

/**
 * 問い合わせ情報のレポジトリインタフェース
 */
@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {

	/**
	 * 問い合わせ情報を登録日時の降順で取得
	 * @return 問い合わせ情報のリスト
	 */
	List<Inquiry> findAllByOrderByInsertDateDesc();
}
