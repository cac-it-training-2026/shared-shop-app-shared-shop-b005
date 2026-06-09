package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
			userform.setAuthority(((UserBean) session.getAttribute("user")).getAuthority());

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

}
