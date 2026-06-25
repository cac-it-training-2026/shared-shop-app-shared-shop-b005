package jp.co.sss.shop.controller.login;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.AuthCodeUtil;
import jp.co.sss.shop.util.Constant;

/**
 * ログイン機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class LoginController {

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
	 * ログイン画面表示処理
	 *
	 * セッション情報を破棄し、ログイン画面を表示する。
	 *
	 * @param form ログインフォーム
	 * @return ログイン画面
	 */
	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login(@ModelAttribute LoginForm form, Model model) {

		session.invalidate();
		model.addAttribute("loginLockNotice", "パスワードを5回間違えるとアカウントが30分間ロックされます。");

		return "login";
	}

	/**
	 * ログイン処理
	 *
	 * メールアドレスおよびパスワードの認証後、
	 * 二段階認証コードを生成し認証画面へ遷移する。
	 *
	 * ログイン失敗時は失敗回数を加算し、
	 * 5回以上失敗した場合はアカウントをロックする。
	 *
	 * ロック中の場合はログイン画面に解除予定時刻を表示する。
	 *
	 * @param form ログインフォーム
	 * @param result 入力チェック結果
	 * @param model 画面表示用モデル
	 * @return ログイン画面または二段階認証画面へのリダイレクト
	 */
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String doLogin(@Valid @ModelAttribute LoginForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {

			User user = userRepository.findByEmailAndDeleteFlag(form.getEmail(), Constant.NOT_DELETED);

			if (user != null && user.getAccountLocked() != null && user.getAccountLocked() == 1) {
				model.addAttribute("errorMessage",
						"アカウントがロックされています。解除予定時刻：" + user.getAccountLockedUntil());
				model.addAttribute("loginLockNotice", "アカウントロック中です。しばらく待つか、パスワード再設定を行ってください。");
				return "login";
			}

			int remainingCount = updateLoginFailure(form.getEmail());
			if (remainingCount == 0) {
				User lockedUser = userRepository.findByEmailAndDeleteFlag(form.getEmail(), Constant.NOT_DELETED);
				model.addAttribute("errorMessage",
						"ログインに5回失敗したため、アカウントをロックしました。解除予定時刻："
								+ (lockedUser != null ? lockedUser.getAccountLockedUntil() : "30分後"));
				model.addAttribute("loginLockNotice", "アカウントロック中です。しばらく待つか、パスワード再設定を行ってください。");
			} else if (remainingCount > 0) {
				model.addAttribute("errorMessage", "メールアドレスまたはパスワードが正しくありません。");
				model.addAttribute("loginLockNotice", "あと" + remainingCount + "回間違えるとアカウントが30分間ロックされます。");
			} else {
				model.addAttribute("errorMessage", "メールアドレスまたはパスワードが正しくありません。");
				model.addAttribute("loginLockNotice", "パスワードを5回間違えるとアカウントが30分間ロックされます。");
			}
			session.invalidate();
			return "login";
		}

		User user = userRepository.findByEmailAndDeleteFlag(form.getEmail(), Constant.NOT_DELETED);

		if (user == null) {
			session.invalidate();
			return "login";
		}

		// ロック期限が過ぎている場合は自動解除
		if (user.getAccountLocked() != null && user.getAccountLocked() == 1
				&& user.getAccountLockedUntil() != null
				&& user.getAccountLockedUntil().before(new Timestamp(System.currentTimeMillis()))) {

			user.setAccountLocked(0);
			user.setLoginFailureCount(0);
			user.setAccountLockedUntil(null);
			userRepository.save(user);
		}

		// ロック中の場合はログイン不可
		if (user.getAccountLocked() != null && user.getAccountLocked() == 1) {
			model.addAttribute("errorMessage",
					"アカウントがロックされています。解除予定時刻：" + user.getAccountLockedUntil());
			model.addAttribute("loginLockNotice", "アカウントロック中です。しばらく待つか、パスワード再設定を行ってください。");
			return "login";
		}

		// パスワード認証成功時は失敗回数・ロック状態をリセット
		user.setLoginFailureCount(0);
		user.setAccountLocked(0);
		user.setAccountLockedUntil(null);
		userRepository.save(user);

		UserBean userBean = new UserBean();
		userBean.setId(user.getId());
		userBean.setName(user.getName());
		userBean.setAuthority(user.getAuthority());
		userBean.setCurrentPoint(user.getCurrentPoint());
		userBean.setTotalPoint(user.getTotalPoint());
		userBean.setRank(user.getRank());

		// 二段階認証用の一時ログイン情報をセッションへ保存
		session.setAttribute("tempUser", userBean);

		String authCode = AuthCodeUtil.generate();
		session.setAttribute("authCode", authCode);
		session.setAttribute("authCodeTime", System.currentTimeMillis());

		System.out.println("Auth Code for " + user.getEmail() + ": " + authCode);

		return "redirect:/login/2fa";
	}

	/**
	 * ログイン失敗回数更新処理
	 *
	 * ログイン失敗時に失敗回数を加算する。
	 * 失敗回数が5回以上になった場合は、アカウントを30分間ロックする。
	 *
	 * ロック期間が経過している場合はロック状態を解除し、
	 * 今回のログイン失敗を1回目として扱う。
	 *
	 * @param email ログイン対象メールアドレス
	 */
	private int updateLoginFailure(String email) {

		User user = userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED);

		if (user == null) {
			return -1;
		}

		// すでにロックされている場合
		if (user.getAccountLocked() != null && user.getAccountLocked() == 1) {

			// ロック期限が過ぎている場合は解除し、今回の失敗を1回目として保存
			if (user.getAccountLockedUntil() != null
					&& user.getAccountLockedUntil().before(new Timestamp(System.currentTimeMillis()))) {

				user.setAccountLocked(0);
				user.setLoginFailureCount(1);
				user.setAccountLockedUntil(null);
				userRepository.save(user);
				return 4;
			}

			return 0;
		}

		int count = user.getLoginFailureCount() != null ? user.getLoginFailureCount() : 0;
		count++;

		user.setLoginFailureCount(count);

		if (count >= 5) {
			user.setAccountLocked(1);
			user.setAccountLockedUntil(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000));
			userRepository.save(user);
			return 0;
		}

		userRepository.save(user);
		return 5 - count;
	}

	/**
	 * 管理者メニュー表示処理
	 *
	 * 管理者メニュー画面を表示する。
	 *
	 * @return 管理者メニュー画面
	 */
	@RequestMapping(path = "/admin/menu", method = RequestMethod.GET)
	public String showAdminMenu() {

		return "admin/admin_menu";
	}
}