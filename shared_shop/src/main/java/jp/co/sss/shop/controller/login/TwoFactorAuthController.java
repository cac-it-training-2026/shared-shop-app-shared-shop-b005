package jp.co.sss.shop.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.util.Constant;

/**
 * 二段階認証機能のコントローラクラス
 *
 * ログイン時に生成された4桁の認証コードを確認し、
 * 認証成功後に正式なログインセッションを生成する。
 *
 * @author SystemShared
 */
@Controller
public class TwoFactorAuthController {

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 二段階認証画面表示処理
	 *
	 * 仮ログイン情報がセッションに存在する場合、
	 * 二段階認証コード入力画面を表示する。
	 *
	 * 仮ログイン情報が存在しない場合はログイン画面へリダイレクトする。
	 *
	 * @param model Viewとの値受渡し
	 * @return 二段階認証画面またはログイン画面へのリダイレクト
	 */
	@RequestMapping(path = "/login/2fa", method = RequestMethod.GET)
	public String show2fa(Model model) {

		if (session.getAttribute("tempUser") == null) {
			return "redirect:/login";
		}

		String authCode = (String) session.getAttribute("authCode");
		model.addAttribute("popupAuthCode", authCode);

		return "login/two_factor_auth";
	}

	/**
	 * 二段階認証処理
	 *
	 * 入力された認証コードとセッションに保存されている認証コードを照合する。
	 * 認証コードの有効期限は5分とする。
	 *
	 * 認証成功時は仮ログイン情報を正式なログイン情報としてセッションへ保存し、
	 * 一時的な認証情報を削除する。
	 *
	 * @param authCode 入力された認証コード
	 * @param model Viewとの値受渡し
	 * @return 一般会員トップ画面、管理者メニュー、または二段階認証画面
	 */
	@RequestMapping(path = "/login/2fa", method = RequestMethod.POST)
	public String verify2fa(@RequestParam String authCode, Model model) {

		UserBean tempUser = (UserBean) session.getAttribute("tempUser");
		String correctCode = (String) session.getAttribute("authCode");
		Long codeTime = (Long) session.getAttribute("authCodeTime");

		if (tempUser == null || correctCode == null || codeTime == null) {
			return "redirect:/login";
		}

		if (System.currentTimeMillis() - codeTime > 5 * 60 * 1000) {
			model.addAttribute("errorMessage", "認証コードの有効期限が切れています。再度ログインしてください。");
			session.invalidate();
			return "login";
		}

		if (correctCode.equals(authCode)) {
			session.setAttribute("user", tempUser);
			session.removeAttribute("tempUser");
			session.removeAttribute("authCode");
			session.removeAttribute("authCodeTime");

			if (tempUser.getAuthority() == Constant.AUTH_CLIENT) {
				return "redirect:/";
			}

			return "redirect:/admin/menu";
		}

		model.addAttribute("errorMessage", "認証コードが正しくありません。");
		model.addAttribute("popupAuthCode", correctCode);

		return "login/two_factor_auth";
	}
}
