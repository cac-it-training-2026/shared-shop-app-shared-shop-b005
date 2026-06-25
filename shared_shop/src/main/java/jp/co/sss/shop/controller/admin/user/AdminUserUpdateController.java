package jp.co.sss.shop.controller.admin.user;

import java.sql.Date;
import java.sql.Timestamp;

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
import jp.co.sss.shop.util.PasswordHashUtil;

/**
 * 会員管理 変更機能(運用管理者、システム管理者)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminUserUpdateController {

	/**
	 * 会員情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 変更入力画面初期表示処理
	 *
	 * 指定された会員IDの会員情報を取得し、変更入力画面に表示するための
	 * UserFormをセッションに保存する。
	 *
	 * @param id 変更対象会員ID
	 * @return 変更入力画面表示処理へのリダイレクト
	 */
	@RequestMapping(path = "/admin/user/update/input/{id}", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {

		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {
			User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

			if (user == null) {
				return "redirect:/syserror";
			}

			userForm = new UserForm();
			BeanUtils.copyProperties(user, userForm);

			session.setAttribute("userForm", userForm);
		}

		return "redirect:/admin/user/update/input";
	}

	/**
	 * 変更入力画面表示処理
	 *
	 * セッションに保存されたUserFormを取得し、変更入力画面に表示する。
	 * 入力エラーが存在する場合は、エラー情報も画面へ渡す。
	 *
	 * @param model Viewとの値受渡し
	 * @return 変更入力画面
	 */
	@RequestMapping(path = "/admin/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {

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

		return "admin/user/update_input";
	}

	/**
	 * 変更入力確認処理
	 *
	 * 入力内容のチェックを行い、エラーがある場合は変更入力画面へ戻す。
	 * エラーがない場合は、変更確認画面へ遷移する。
	 *
	 * @param form 入力フォーム
	 * @param result 入力チェック結果
	 * @return 変更入力画面または変更確認画面へのリダイレクト
	 */
	@RequestMapping(path = "/admin/user/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		UserForm lastUserForm = (UserForm) session.getAttribute("userForm");

		if (lastUserForm == null) {
			return "redirect:/syserror";
		}

		if (form.getAuthority() == null) {
			form.setAuthority(lastUserForm.getAuthority());
		}

		session.setAttribute("userForm", form);

		if (result.hasErrors()) {
			session.setAttribute("result", result);
			return "redirect:/admin/user/update/input";
		}

		return "redirect:/admin/user/update/check";
	}

	/**
	 * 変更確認画面表示処理
	 *
	 * セッションに保存された入力フォーム情報を取得し、
	 * 変更確認画面へ表示する。
	 *
	 * @param model Viewとの値受渡し
	 * @return 変更確認画面
	 */
	@RequestMapping(path = "/admin/user/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {

		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {
			return "redirect:/syserror";
		}

		model.addAttribute("userForm", userForm);

		return "admin/user/update_check";
	}

	/**
	 * 変更登録処理
	 *
	 * 入力された会員情報をDBに保存する。
	 * パスワードはハッシュ化して保存する。
	 *
	 * BeanUtils.copyPropertiesによって更新対象外の値が上書きされないように、
	 * 削除フラグ、登録日付、ログイン失敗回数、アカウントロック情報、
	 * パスワード再設定トークン情報は退避してから復元する。
	 *
	 * @return 変更完了画面表示処理へのリダイレクト
	 */
	@RequestMapping(path = "/admin/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {

		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {
			return "redirect:/syserror";
		}

		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);

		if (user == null) {
			return "redirect:/syserror";
		}

		Integer deleteFlag = user.getDeleteFlag();
		Date insertDate = user.getInsertDate();

		Integer loginFailureCount = user.getLoginFailureCount();
		Integer accountLocked = user.getAccountLocked();
		Timestamp accountLockedUntil = user.getAccountLockedUntil();
		String resetToken = user.getResetToken();
		Timestamp resetTokenExpire = user.getResetTokenExpire();

		BeanUtils.copyProperties(userForm, user);

		user.setPassword(PasswordHashUtil.hash(userForm.getPassword()));

		user.setDeleteFlag(deleteFlag);
		user.setInsertDate(insertDate);

		user.setLoginFailureCount(loginFailureCount);
		user.setAccountLocked(accountLocked);
		user.setAccountLockedUntil(accountLockedUntil);
		user.setResetToken(resetToken);
		user.setResetTokenExpire(resetTokenExpire);

		userRepository.save(user);

		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser != null && loginUser.getId().equals(userForm.getId())) {
			loginUser.setName(userForm.getName());
			session.setAttribute("user", loginUser);
		}

		session.removeAttribute("userForm");

		return "redirect:/admin/user/update/complete";
	}

	/**
	 * 変更完了画面表示処理
	 *
	 * @return 変更完了画面
	 */
	@RequestMapping(path = "/admin/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {

		return "admin/user/update_complete";
	}
}