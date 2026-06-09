package jp.co.sss.shop.controller.client.user;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import jakarta.servlet.http.HttpSession;
//import jp.co.sss.shop.bean.UserBean;
//import jp.co.sss.shop.entity.User;
//import jp.co.sss.shop.repository.UserRepository;
//import jp.co.sss.shop.util.Constant;
//
//@Controller
//public class ClientUserShowController {
//
//	@Autowired
//	UserRepository userRepository;
//
//	@Autowired
//	HttpSession session;
//
//	@RequestMapping(path = "/client/user/detail", method = { RequestMethod.GET, RequestMethod.POST })
//	public String showUser(Model model) {
//		UserBean loginUser = (UserBean) session.getAttribute("user");
//
//		if (loginUser == null) {
//			return "redirect:/login";
//		}
//		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);
//	}
//
//}
