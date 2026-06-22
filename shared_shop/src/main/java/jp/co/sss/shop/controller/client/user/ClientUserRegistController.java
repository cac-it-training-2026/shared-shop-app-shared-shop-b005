package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
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
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.PasswordHashUtil;

/**
 * 会員管理 新規会員登録機能(非会員向け)のコントローラクラス
 *
 * @author 松浦崚汰
 * 
 */
@Controller
public class ClientUserRegistController {

	/**
	 * 会員情報　リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 
	 * 入力画面　表示処理(GET) 新規登録リンク クリック時処理
	 * 
	 * @return "redirect:/client/user/regist/input" 入力画面　表示処理
	 */
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String initRegistInput() {

		// 入力フォーム情報を新規生成
		UserForm userForm = new UserForm();

		// セッションに保存
		session.setAttribute("userForm", userForm);

		// 登録入力画面表示処理へリダイレクト
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 入力画面　表示処理(POST) 一覧画面での新規ボタン押下後の処理、確認画面～戻るボタン 押下時処理
	 * 
	 * @return "redirect:/client/user/regist/input" 入力画面　表示処理
	 */
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String prepareRegistInput() {

		// セッションに保存されているuserFormを取得
		UserForm userform = (UserForm) session.getAttribute("userForm");

		// userformが空の場合
		if (userform == null) {

			// UserFormを新規作成
			userform = new UserForm();

			// 権限を設定（未ログインの場合は一般会員:2）
			UserBean loginUser = (UserBean) session.getAttribute("user");
			if (loginUser != null) {
				userform.setAuthority(loginUser.getAuthority());
			} else {
				userform.setAuthority(Constant.AUTH_CLIENT);
			}

			// セッションに保存
			session.setAttribute("userForm", userform);
		}
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 入力画面　表示処理(GET)
	 * 
	 * @param model Viewとの値受渡し
	 * @return "client/user/regist_input" 入力画面　表示
	 * 
	 * 入力値エラーあり："redirect:/syserror" エラー画面　表示処理
	 * 入力値エラーなし："redirect:/client/user/regist_input" 入力画面　表示処理
	 */
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String showRegistInput(Model model) {

		// セッションに保存されているuserFormを取得
		UserForm userform = (UserForm) session.getAttribute("userForm");
		if (userform == null) {

			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 前の画面で発生した入力エラー情報をセッションから取得する
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {

			// セッションにエラー情報がある場合、エラー情報をスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);

			// セッションのエラー情報を削除
			session.removeAttribute("result");
		}

		// 入力情報をスコープに保存
		model.addAttribute("userForm", userform);

		// 入力画面 表示
		return "client/user/regist_input";
	}

	/**
	 * 登録入力確認　処理
	 *
	 * @param form 入力フォーム
	 * @param result 入力値チェックの結果
	 * @return 
	 * 	入力値エラーあり："redirect:/client/user/regist/input" 会員登録入力画面　表示処理
	 * 	入力値エラーなし："redirect:/client/user/regist/check" 会員登録確認画面　表示処理
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String clientInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		// 直前のセッション情報を取得
		UserForm inputUserForm = (UserForm) session.getAttribute("userForm");
		if (inputUserForm == null) {

			// セッション情報がない場合エラー
			return "redirect:/syserror";
		}

		// 権限情報がない場合、セッション情報から値をセット
		if (form.getAuthority() == null) {
			form.setAuthority(inputUserForm.getAuthority());
		}

		// 入力フォーム情報をセッションにセット
		session.setAttribute("userForm", form);

		if (result.hasErrors()) {

			// 入力値にエラーがあった場合、エラー情報をセッションに保持
			session.setAttribute("result", result);

			// 登録入力画面 表示処理
			return "redirect:/client/user/regist/input";
		}

		// 登録確認画面 表示処理 
		return "redirect:/client/user/regist/check";
	}

	/**
	 * 確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/regist_check" 確認画面　表示
	 * 
	 * 入力値エラーあり："redirect:/syserror" エラー画面　表示処理
	 * 入力値エラーなし："redirect:/client/user/regist_check" 会員情報登録確認画面　表示処理
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.GET)
	public String clientCheck(Model model) {

		// セッションから入力フォーム情報取得
		UserForm userform = (UserForm) session.getAttribute("userForm");
		if (userform == null) {

			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 入力フォーム情報をスコープへ保存
		model.addAttribute("userForm", userform);

		// 登録確認画面 表示処理
		return "client/user/regist_check";
	}

	/**
	 * 会員情報登録処理
	 *
	 * @return "redirect:/client/user/regist/complete" 登録完了画面　表示処理
	 * 
	 * 入力値エラーあり："redirect:/syserror" エラー画面　表示処理
	 * 入力値エラーなし："redirect:/client/user/regist/check" 会員登録完了画面　表示処理
	 * 
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String clientComplete() {

		// セッションから入力フォーム情報取得
		UserForm userform = (UserForm) session.getAttribute("userForm");
		if (userform == null) {

			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 会員情報を生成
		User user = new User();

		// 入力フォーム情報をエンティティに設定
		BeanUtils.copyProperties(userform, user);

		// パスワードをハッシュ化
		user.setPassword(PasswordHashUtil.hash(userform.getPassword()));

		// 初期設定
		user.setLoginFailureCount(0);
		user.setAccountLocked(0);

		// DB登録実施
		userRepository.save(user);

		// 入力情報削除
		session.removeAttribute("userForm");

		// 登録完了画面 表示処理
		return "redirect:/client/user/regist/complete";
	}

	/**
	 * 新規会員登録完了画面　表示処理
	 *
	 * @return "client/user/regist_complete" 登録完了画面　表示
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.GET)
	public String clientCompleteFinish() {
		return "client/user/regist_complete";
	}

}
