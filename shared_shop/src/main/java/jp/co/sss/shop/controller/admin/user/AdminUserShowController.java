package jp.co.sss.shop.controller.admin.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 表示機能(運用管理者、システム管理者)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class AdminUserShowController {

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
	 * 会員一覧表示処理
	 *
	 * 会員情報をページング形式で取得し、会員一覧画面へ表示する。
	 * 会員登録数が上限に達している場合は、新規登録不可として画面へ渡す。
	 *
	 * @param model Viewとの値受渡し
	 * @param pageable ページング制御
	 * @return 会員一覧画面
	 */
	@RequestMapping(path = "/admin/user/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUserList(Model model, Pageable pageable) {

		Long usersCount = userRepository.count();
		Boolean registrable = true;

		if (usersCount == Constant.USERS_MAX_COUNT) {
			registrable = false;
		}

		Integer authority = ((UserBean) session.getAttribute("user")).getAuthority();
		Page<User> userList = userRepository.findUsersListOrderByInsertDate(
				Constant.NOT_DELETED, authority, pageable);

		model.addAttribute("registrable", registrable);
		model.addAttribute("pages", userList);
		model.addAttribute("users", userList.getContent());

		session.removeAttribute("userForm");

		return "admin/user/list";
	}

	/**
	 * 会員詳細表示処理
	 *
	 * 指定された会員IDの会員情報を取得し、会員詳細画面へ表示する。
	 * 対象会員が存在しない場合はシステムエラー画面へ遷移する。
	 *
	 * @param id 表示対象会員ID
	 * @param model Viewとの値受渡し
	 * @return 会員詳細画面
	 */
	@RequestMapping(path = "/admin/user/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUser(@PathVariable int id, Model model) {

		User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

		if (user == null) {
			return "redirect:/syserror";
		}

		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		model.addAttribute("userBean", userBean);
		model.addAttribute("user", user);

		session.removeAttribute("userForm");

		return "admin/user/detail";
	}

	/**
	 * アカウントロック解除処理
	 *
	 * 指定された会員のアカウントロック状態を解除する。
	 * ログイン失敗回数、ロック状態、ロック解除予定日時を初期化する。
	 *
	 * @param id ロック解除対象の会員ID
	 * @return 会員詳細画面へリダイレクト
	 */
	@RequestMapping(path = "/admin/user/unlock/{id}", method = RequestMethod.POST)
	public String unlock(@PathVariable int id) {

		User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

		if (user != null) {
			user.setAccountLocked(0);
			user.setLoginFailureCount(0);
			user.setAccountLockedUntil(null);
			userRepository.save(user);
		}

		return "redirect:/admin/user/detail/" + id;
	}
}