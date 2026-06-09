package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
class ClientUserUpdateController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	/**
	 * @param id
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/input/{id}", method = RequestMethod.POST)
	public String updateInputInit(@PathVariable Integer id) {
		return "redirect:/client/user/update/input";
	}

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "client/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {
		return "client/user/update_input";
	}

	/**
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {
		return "redirect:/client/user/update/check";
	}

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {
		return "client/user/update_check";
	}

	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {
		return "redirect:/client/user/update/complete";
	}

	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
		return "client/user/update_complete";
	}

}
