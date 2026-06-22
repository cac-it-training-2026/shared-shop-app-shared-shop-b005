package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 詳細表示機能(一般会員用)のコントローラクラス
 *
 * @author 佐藤匠
 */
@Controller
public class ClientUserShowController {

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
	 * 会員詳細表示処理(GET)
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/detail" 会員詳細画面
	 */
	@RequestMapping(path = "/client/user/detail", method = RequestMethod.GET)
	public String showUser(Model model) {

		return showUserDetail(model);
	}

	/**
	 * 会員詳細表示処理(POST)
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/detail" 会員詳細画面
	 */
	@RequestMapping(path = "/client/user/detail", method = RequestMethod.POST)
	public String showUserByPost(Model model) {

		return showUserDetail(model);
	}

	/**
	 * 会員詳細画面表示共通処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/detail" 会員詳細画面
	 */
	private String showUserDetail(Model model) {

		// セッションからログイン中の会員情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {
			return "redirect:/login";
		}

		// 表示対象の会員情報を取得
		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);

		if (user == null) {
			return "redirect:/syserror";
		}

		// Userエンティティの値をUserBeanにコピー
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		// 会員情報をViewに渡す
		model.addAttribute("userBean", userBean);

		// 会員登録・変更・削除用のセッションスコープを初期化
		session.removeAttribute("userForm");

		return "client/user/detail";
	}
}