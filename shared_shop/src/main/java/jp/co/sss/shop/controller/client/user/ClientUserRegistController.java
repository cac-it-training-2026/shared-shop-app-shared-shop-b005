package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String ClientInput() {

		//セッションに保存されているuserFormを取得
		UserForm userform = (UserForm) session.getAttribute("userForm");

		//userformが空の場合
		if (userform == null) {
			//UserFormを新規作成
			userform = new UserForm();
			//権限をコピー
			//userform.setAuthority(((UserBean) session.getAttribute("user")).getAuthority());

			//セッションに保存
			session.setAttribute("userForm", userform);
		}
		return "redirect:/client/user/regist/input";
	}

	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String ClientnewInput() {

		//セッションに保存されているuserFormを取得
		UserForm userform = (UserForm) session.getAttribute("userForm");

		//userformが空の場合
		if (userform == null) {
			//UserFormを新規作成
			userform = new UserForm();
			//権限をコピー
			userform.setAuthority(((UserBean) session.getAttribute("user")).getAuthority());

			//セッションに保存
			session.setAttribute("userForm", userform);
		}
		return "redirect:/client/user/regist/input";
	}

	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String ClientInput(Model model) {

		//セッションに保存されているuserFormを取得
		UserForm userform = (UserForm) session.getAttribute("userForm");
		if (userform == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		//前の画面で発生した入力エラー情報をセッションから取得する
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			//セッションのエラー情報を削除
			session.removeAttribute("result");
		}

		//入力情報をスコープに保存
		model.addAttribute("userForm", userform);

		return "client/user/regist_input";
	}

}
