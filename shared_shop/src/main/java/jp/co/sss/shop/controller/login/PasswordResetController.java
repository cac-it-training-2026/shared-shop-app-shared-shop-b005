package jp.co.sss.shop.controller.login;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.PasswordHashUtil;
import jp.co.sss.shop.util.TokenUtil;

/**
 * パスワード再設定機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class PasswordResetController {

	/**
	 * 会員情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * パスワード再設定申請画面表示処理
	 *
	 * パスワードを忘れた利用者が、メールアドレスと秘密の質問の回答を入力する画面を表示する。
	 *
	 * @return パスワード再設定申請画面
	 */
	@RequestMapping(path = "/password_reset/request", method = RequestMethod.GET)
	public String showRequest() {

		return "login/password_reset_request";
	}

	/**
	 * パスワード再設定申請処理
	 *
	 * 入力されたメールアドレスと秘密の質問の回答が一致した場合、
	 * パスワード再設定用トークンを生成し、有効期限を設定してDBへ保存する。
	 *
	 * 現在はメール送信の代替として、再設定URLをコンソールへ出力する。
	 *
	 * @param email メールアドレス
	 * @param secretAnswer 秘密の質問の回答
	 * @param model Viewとの値受渡し
	 * @return パスワード再設定申請画面
	 */
	@RequestMapping(path = "/password_reset/request", method = RequestMethod.POST)
	public String handleRequest(@RequestParam String email,
			@RequestParam(required = false) String secretAnswer, Model model) {

		User user = userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED);
		model.addAttribute("email", email);

		if (user == null || user.getSecretQuestion() == null || user.getSecretQuestion().isEmpty()) {
			model.addAttribute("errorMessage", "入力されたメールアドレスの会員情報が見つかりません。");
			return "login/password_reset_request";
		}

		// 1回目の送信では、メールアドレスに紐づく秘密の質問を画面に表示する
		if (secretAnswer == null || secretAnswer.isEmpty()) {
			model.addAttribute("secretQuestion", user.getSecretQuestion());
			return "login/password_reset_request";
		}

		if (user.getSecretAnswer() != null && secretAnswer.equals(user.getSecretAnswer())) {

			String token = TokenUtil.generate();

			user.setResetToken(token);
			user.setResetTokenExpire(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000));
			userRepository.save(user);

			System.out.println("Password Reset Link: /password_reset/form?token=" + token);

			model.addAttribute("resetToken", token);
			model.addAttribute("message", "秘密の質問の確認が完了しました。下のリンクからパスワードを再設定してください。");

		} else {
			model.addAttribute("secretQuestion", user.getSecretQuestion());
			model.addAttribute("errorMessage", "秘密の質問の回答が正しくありません。");
		}

		return "login/password_reset_request";
	}

	/**
	 * パスワード再設定画面表示処理
	 *
	 * URLパラメータのトークンを確認し、有効期限内であれば
	 * 新しいパスワード入力画面を表示する。
	 *
	 * トークンが存在しない、または期限切れの場合はログイン画面へ戻す。
	 *
	 * @param token パスワード再設定トークン
	 * @param model Viewとの値受渡し
	 * @return パスワード再設定画面またはログイン画面
	 */
	@RequestMapping(path = "/password_reset/form", method = RequestMethod.GET)
	public String showForm(@RequestParam String token, Model model) {

		User user = userRepository.findByResetToken(token);

		if (user == null || user.getResetTokenExpire() == null
				|| user.getResetTokenExpire().before(new Timestamp(System.currentTimeMillis()))) {

			model.addAttribute("errorMessage", "トークンが無効か期限切れです。");
			model.addAttribute("loginForm", new LoginForm());
			return "login";
		}

		model.addAttribute("token", token);

		return "login/password_reset_form";
	}

	/**
	 * パスワード再設定処理
	 *
	 * トークンが有効期限内であることを確認し、
	 * 新しいパスワードをハッシュ化してDBへ保存する。
	 *
	 * パスワード変更後は再利用できないように、トークンと有効期限を削除する。
	 *
	 * @param token パスワード再設定トークン
	 * @param newPassword 新しいパスワード
	 * @param model Viewとの値受渡し
	 * @return ログイン画面
	 */
	@RequestMapping(path = "/password_reset/form", method = RequestMethod.POST)
	public String handleReset(@RequestParam String token, @RequestParam String newPassword, Model model) {

		User user = userRepository.findByResetToken(token);

		if (user == null || user.getResetTokenExpire() == null
				|| user.getResetTokenExpire().before(new Timestamp(System.currentTimeMillis()))) {

			model.addAttribute("errorMessage", "トークンが無効か期限切れです。");
			model.addAttribute("loginForm", new LoginForm());
			return "login";
		}

		user.setPassword(PasswordHashUtil.hash(newPassword));
		user.setResetToken(null);
		user.setResetTokenExpire(null);
		userRepository.save(user);

		model.addAttribute("message", "パスワードを更新しました。新しいパスワードでログインしてください。");
		model.addAttribute("loginForm", new LoginForm());

		return "login";
	}
}