//package jp.co.sss.shop.controller.client.user;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import jakarta.servlet.http.HttpSession;
//import jp.co.sss.shop.bean.UserBean;
//import jp.co.sss.shop.entity.User;
//import jp.co.sss.shop.form.UserForm;
//import jp.co.sss.shop.repository.UserRepository;
//import jp.co.sss.shop.util.Constant;
//
///**
// * 会員管理 削除機能(一般会員用)のコントローラクラス
// *
// * @author 佐藤匠
// */
//@Controller
//public class ClientUserDeleteController {
//
//	/**
//	 * 会員情報
//	 */
//	@Autowired
//	UserRepository userRepository;
//
//	/**
//	 * セッション情報
//	 */
//	@Autowired
//	HttpSession session;
//
//	/**
//	 * 会員削除確認処理
//	 *
//	 * @return 削除確認画面へリダイレクト
//	 */
//	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
//	public String deleteCheck() {
//
//		// セッションからログイン中の会員情報を取得
//		UserBean loginUser = (UserBean) session.getAttribute("user");
//
//		if (loginUser == null) {
//			return "redirect:/login";
//		}
//
//		// 表示対象の会員情報を取得
//		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);
//
//		if (user == null) {
//			return "redirect:/syserror";
//		}
//
//		// UserFormへコピー
//		UserForm userForm = new UserForm();
//		BeanUtils.copyProperties(user, userForm);
//
//		// セッションへ保持
//		session.setAttribute("userForm", userForm);
//
//		return "redirect:/client/user/delete/check";
//	}
//
//	/**
//	 * 削除確認画面表示処理
//	 *
//	 * @param model Viewとの値受渡し
//	 * @return "client/user/delete_check" 削除確認画面
//	 */
//	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
//	public String deleteCheckShow(Model model) {
//
//		// セッションから削除対象情報を取得
//		UserForm userForm = (UserForm) session.getAttribute("userForm");
//
//		if (userForm == null) {
//			return "redirect:/syserror";
//		}
//
//		// 会員情報をViewへ渡す
//		model.addAttribute("userForm", userForm);
//
//		return "client/user/delete_check";
//	}
//
//	/**
//	 * 会員削除完了処理
//	 *
//	 * @return 削除完了画面へリダイレクト
//	 */
//	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
//	public String deleteComplete() {
//
//		// セッションから削除対象情報を取得
//		UserForm userForm = (UserForm) session.getAttribute("userForm");
//
//		if (userForm == null) {
//			return "redirect:/syserror";
//		}
//
//		
