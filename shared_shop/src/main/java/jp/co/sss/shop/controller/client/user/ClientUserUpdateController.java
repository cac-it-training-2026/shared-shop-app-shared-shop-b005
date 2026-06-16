package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * @author 犬丸 晴斗
 */
@Controller
public class ClientUserUpdateController {

	/**
	 * 会員情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 入力画面初期表示処理(POST)
	 * @param id 変更対象ID
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/input/{id}", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {

		// セッションスコープより情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {

			// 取得したidからユーザ情報を取得
			User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

			if (user == null) {
				return "redirect:/syserror";
			}

			// 初期表示用情報の生成
			userForm = new UserForm();

			// 情報をコピー
			BeanUtils.copyProperties(user, userForm);

			// 変更入力フォームをセッションに保持
			session.setAttribute("userForm", userForm);

		}
		return "redirect:/client/user/update/input";
	}

	/**
	 * 入力画面　表示処理
	 * @param model
	 * @return "client/user/update_input" 変更入力画面 表示
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		// セッションから入力フォーム取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {

			return "redirect:/syserror";
		}

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// セッションスコープから入力エラー情報を取得
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {

			// 入力エラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);

			// セッションスコープから入力エラー情報を削除
			session.removeAttribute("result");
		}

		// 変更入力画面表示
		return "client/user/update_input";
	}

	/**
	 * 変更確認処理
	 * 
	 * @param form 入力フォーム
	 * @param result 入力チェック結果
	 * @return
	 *入力エラーあり: "redirect:/client/user/update/input"
	 *入力エラーなし: "redirect:/client/user/update/check"確認画面へ
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (lastUserForm == null) {
			return "redirect:/syserror";
		}

		// 入力フォーム情報をセッションに保持
		session.setAttribute("userForm", form);

		// 入力値にエラーがあった場合、入力画面に戻る
		if (result.hasErrors()) {
			session.setAttribute("result", result);

			// 変更入力画面　表示処理
			return "redirect:/client/user/update/input";
		}
		return "redirect:/client/user/update/check";
	}

	/**
	 * 確認画面表示
	 * 
	 * @param model
	 * @return
	 * 入力エラーあり: "redirect:/syserror"
	 * 入力エラーなし: "client/user/update_check" 確認画面 表示
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {

		// セッションから入力フォーム情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {

			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		model.addAttribute("userForm", userForm);

		return "client/user/update_check";
	}

	/**
	 * @return "redirect:/client/user/update/input" postに変換
	 * 
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInputBack() {
		return "redirect:/client/user/update/input";
	}

	/**
	 * @return
	 * 入力エラーあり: "redirect:/syserror" エラー処理
	 * 入力エラーなし: "redirect:/client/user/update/complete" updateCompleteFinishのメソッドへ
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {

		// セッションスコープから入力フォーム情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// セッション情報がない場合エラー
		if (userForm == null) {
			return "redirect:/syserror";
		}

		// 入力フォームのidをもとに、DBから変更対象の会員情報を取得
		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);

		// 変更対象の会員情報が存在しない場合、エラー画面へ
		if (user == null) {
			return "redirect:/syserror";
		}

		// 入力フォームの内容を会員情報エンティティにコピー
		BeanUtils.copyProperties(userForm, user);
		user.setDeleteFlag(Constant.NOT_DELETED);

		// 変更後の会員情報をDBに保存
		userRepository.save(user);

		// セッションの情報を更新
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);
		session.setAttribute("user", userBean);

		// 変更用の入力フォーム情報をセッションから削除
		session.removeAttribute("userForm");

		return "redirect:/client/user/update/complete";
	}

	/**
	 * @return
	 * "client/user/update_complete"変更完了画面 表示
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
		return "client/user/update_complete";
	}

}
