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
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

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
	 * @param id
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {

		// セッションスコープより情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {

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
	 * @return
	 */
	@RequestMapping(path = "client/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから入力フォーム取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {

			return "redirect:/syserror";
		}
		model.addAttribute("userForm", userForm);

		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {

			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("result");
		}
		// 変更入力画面表示
		return "client/user/update_input";
	}

	/**
	 * 変更確認処理
	 * 
	 * @param form
	 * @param result
	 * @return
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

			//変更入力画面　表示処理
			return "redirect:/client/user/update/input";
		}
		return "redirect:/client/user/update/check";
	}

	/**
	 * 確認画面表示
	 * 
	 * @param model
	 * @return
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
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {
		return "redirect:/client/user/update/complete";
	}

	/**
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
		return "client/user/update_complete";
	}

}
