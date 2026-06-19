package jp.co.sss.shop.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Timestamp;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.AuthCodeUtil;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.PasswordHashUtil;

/**
 * ログイン機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class LoginController {

	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * ログイン処理
	 *
	 * @param form ログインフォーム
	 * @return "login" ログイン画面表示
	 */
	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login(@ModelAttribute LoginForm form) {

		// セッション情報を無効にする
		session.invalidate();

		return "login";
	}

	/**
	 * ログイン処理
	 *
	 * @param form ログインフォーム
	 * @param result 入力チェック結果
	 * @return
			一般会員の場合 "redirect:/" トップ画面表示処理
			運用管理者、システム管理者の場合 "redirect:/adminmenu"管理者メニュー表示処理
	 */
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String doLogin(@Valid @ModelAttribute LoginForm form, BindingResult result) {

		if (result.hasErrors()) {
			// バリデーションエラー（パスワード不一致やロック中も含む）
			// 失敗時のログ・ロック更新処理
			updateLoginFailure(form.getEmail());
			session.invalidate();
			return "login";
		}

		// 認証成功時
		User user = userRepository.findByEmailAndDeleteFlag(form.getEmail(), Constant.NOT_DELETED);

		// ロック情報の自動解除処理
		if (user.getAccountLocked() == 1 && user.getAccountLockedUntil() != null
				&& user.getAccountLockedUntil().before(new Timestamp(System.currentTimeMillis()))) {
			user.setAccountLocked(0);
			user.setLoginFailureCount(0);
			user.setAccountLockedUntil(null);
		}

		// ログイン成功による失敗回数リセット
		user.setLoginFailureCount(0);
		userRepository.save(user);

		UserBean userBean = new UserBean();
		userBean.setId(user.getId());
		userBean.setName(user.getName());
		userBean.setAuthority(user.getAuthority());
		userBean.setAccountLocked(user.getAccountLocked());
		userBean.setAccountLockedUntil(user.getAccountLockedUntil());

		// 二段階認証フロー開始
		session.setAttribute("tempUser", userBean);
		String authCode = AuthCodeUtil.generateAuthCode();
		session.setAttribute("authCode", authCode);
		session.setAttribute("authCodeTime", System.currentTimeMillis());

		System.out.println("Auth Code for " + user.getEmail() + ": " + authCode);

		return "redirect:/login/2fa";
	}

	/**
	 * ログイン失敗時の回数カウントおよびロック処理
	 */
	private void updateLoginFailure(String email) {
		User user = userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED);
		if (user != null) {
			int count = user.getLoginFailureCount() + 1;
			user.setLoginFailureCount(count);
			if (count >= 5) {
				user.setAccountLocked(1);
				user.setAccountLockedUntil(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000));
			}
			userRepository.save(user);
		}
	}

	/**
	 * 管理者メニュー表示処理
	 *
	 * @return "admin/menu" 管理者メニュー画面表示
	 */
	@RequestMapping(path = "/admin/menu", method = RequestMethod.GET)
	public String showAdminMenu() {

		// 管理者用メニュー画面表示
		return "admin/admin_menu";
	}

}
