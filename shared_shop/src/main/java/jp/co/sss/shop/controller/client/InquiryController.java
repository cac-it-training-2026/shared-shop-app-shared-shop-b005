package jp.co.sss.shop.controller.client;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import jp.co.sss.shop.entity.Inquiry;
import jp.co.sss.shop.form.InquiryForm;
import jp.co.sss.shop.repository.InquiryRepository;

/**
 * 問い合わせ用コントローラ
 */
@Controller
public class InquiryController {

	/**
	 * 問い合わせ情報レポジトリ
	 */
	private final InquiryRepository inquiryRepository;

	/**
	 * コンストラクタ注入
	 * @param inquiryRepository 問い合わせ情報レポジトリ
	 */
	public InquiryController(InquiryRepository inquiryRepository) {
		this.inquiryRepository = inquiryRepository;
	}

	/**
	 * 問い合わせ入力画面を表示
	 *
	 * @param form 問い合わせフォーム
	 * @return 遷移先
	 */
	@GetMapping("/inquiry/input")
	public String inquiryInput(@ModelAttribute InquiryForm form) {
		return "client/inquiry/input";
	}

	/**
	 * 問い合わせ入力画面へ戻る（確認画面から）
	 *
	 * @param form 問い合わせフォーム
	 * @return 遷移先
	 */
	@PostMapping("/inquiry/input")
	public String inquiryInputBack(@ModelAttribute InquiryForm form) {
		return "client/inquiry/input";
	}

	/**
	 * 問い合わせ確認画面を表示
	 *
	 * @param form   問い合わせフォーム
	 * @param result バリデーション結果
	 * @return 遷移先
	 */
	@PostMapping("/inquiry/check")
	public String inquiryCheck(@Valid @ModelAttribute InquiryForm form, BindingResult result) {
		if (result.hasErrors()) {
			return "client/inquiry/input";
		}
		return "client/inquiry/check";
	}

	/**
	 * 問い合わせ登録処理
	 *
	 * @param form 問い合わせフォーム
	 * @return 遷移先
	 */
	@PostMapping("/inquiry/complete")
	public String inquiryRegister(@ModelAttribute InquiryForm form) {
		Inquiry inquiry = new Inquiry();
		BeanUtils.copyProperties(form, inquiry);
		inquiryRepository.save(inquiry);
		return "redirect:/inquiry/complete";
	}

	/**
	 * 問い合わせ完了画面を表示
	 *
	 * @return 遷移先
	 */
	@GetMapping("/inquiry/complete")
	public String inquiryComplete() {
		return "client/inquiry/complete";
	}
}
