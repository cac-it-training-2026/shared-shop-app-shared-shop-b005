package jp.co.sss.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jp.co.sss.shop.repository.InquiryRepository;

/**
 * 問い合わせ管理用コントローラ
 */
@Controller
public class AdminInquiryController {

	/**
	 * 問い合わせ情報レポジトリ
	 */
	@Autowired
	InquiryRepository inquiryRepository;

	/**
	 * 問い合わせ一覧画面を表示
	 *
	 * @param model モデル
	 * @return 遷移先
	 */
	@GetMapping("/admin/inquiry/list")
	public String inquiryList(Model model) {
		model.addAttribute("inquiries", inquiryRepository.findAllByOrderByInsertDateDesc());
		return "admin/inquiry/list";
	}

	/**
	 * 問い合わせ詳細画面を表示
	 *
	 * @param id    問い合わせID
	 * @param model モデル
	 * @return 遷移先
	 */
	@GetMapping("/admin/inquiry/detail/{id}")
	public String inquiryDetail(@PathVariable Integer id, Model model) {
		model.addAttribute("inquiry", inquiryRepository.findById(id).orElse(null));
		return "admin/inquiry/detail";
	}
}
