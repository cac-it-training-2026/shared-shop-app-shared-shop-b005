package jp.co.sss.shop.controller.client.creditcard;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
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
import jp.co.sss.shop.bean.CreditCardBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.CreditCard;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.CreditCardForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.CreditCardRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.CipherUtil;

@Controller
public class ClientCreditCardController {
	@Autowired
	CreditCardRepository creditCardRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CategoryRepository categoryRepository;

	private String maskCardNumber(String cardNumber) {
		if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
		int maskLength = cardNumber.length() - 4;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < maskLength; i++) sb.append("*");
		sb.append(cardNumber.substring(maskLength));
		return sb.toString();
	}

	@RequestMapping(path = "/client/creditcard/list", method = RequestMethod.GET)
	public String list(HttpSession session, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) return "redirect:/login";
		List<CreditCard> cards = creditCardRepository.findByUser_IdOrderByInsertDateDescIdDesc(userBean.getId());
		List<CreditCardBean> cardBeans = new ArrayList<>();
		for (CreditCard card : cards) {
			CreditCardBean bean = new CreditCardBean();
			BeanUtils.copyProperties(card, bean);
			bean.setCardNumber(maskCardNumber(CipherUtil.decrypt(card.getCardNumber())));
			cardBeans.add(bean);
		}
		model.addAttribute("creditCards", cardBeans);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/list";
	}

	@RequestMapping(path = "/client/creditcard/regist/input", method = RequestMethod.GET)
	public String registInput(@ModelAttribute("creditCardForm") CreditCardForm form, Model model) {
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/regist_input";
	}

	@RequestMapping(path = "/client/creditcard/regist/check", method = RequestMethod.POST)
	public String registCheck(@Valid @ModelAttribute CreditCardForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
			return "client/creditcard/regist_input";
		}
		model.addAttribute("maskedCardNumber", maskCardNumber(form.getCardNumber()));
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/regist_check";
	}

	@RequestMapping(path = "/client/creditcard/regist/complete", method = RequestMethod.POST)
	public String registComplete(@ModelAttribute CreditCardForm form, HttpSession session, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) return "redirect:/login";
		User user = userRepository.getReferenceById(userBean.getId());
		CreditCard card = new CreditCard();
		BeanUtils.copyProperties(form, card);
		card.setCardNumber(CipherUtil.encrypt(form.getCardNumber()));
		card.setUser(user);
		creditCardRepository.save(card);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/regist_complete";
	}

	@RequestMapping(path = "/client/creditcard/update/input/{id}", method = RequestMethod.POST)
	public String updateInput(@PathVariable Integer id, Model model) {
		CreditCard card = creditCardRepository.findById(id).orElse(null);
		if (card == null) return "redirect:/syserror";
		CreditCardForm form = new CreditCardForm();
		BeanUtils.copyProperties(card, form);
		form.setCardNumber(CipherUtil.decrypt(card.getCardNumber()));
		model.addAttribute("creditCardForm", form);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/update_input";
	}

	@RequestMapping(path = "/client/creditcard/update/check", method = RequestMethod.POST)
	public String updateCheck(@Valid @ModelAttribute CreditCardForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
			return "client/creditcard/update_input";
		}
		model.addAttribute("maskedCardNumber", maskCardNumber(form.getCardNumber()));
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/update_check";
	}

	@RequestMapping(path = "/client/creditcard/update/complete", method = RequestMethod.POST)
	public String updateComplete(@ModelAttribute CreditCardForm form, HttpSession session, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) return "redirect:/login";
		CreditCard card = creditCardRepository.findById(form.getId()).orElse(null);
		if (card == null) return "redirect:/syserror";
		BeanUtils.copyProperties(form, card, "user", "insertDate");
		card.setCardNumber(CipherUtil.encrypt(form.getCardNumber()));
		creditCardRepository.save(card);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/update_complete";
	}

	@RequestMapping(path = "/client/creditcard/delete/check/{id}", method = RequestMethod.POST)
	public String deleteCheck(@PathVariable Integer id, Model model) {
		CreditCard card = creditCardRepository.findById(id).orElse(null);
		if (card == null) return "redirect:/syserror";
		CreditCardBean bean = new CreditCardBean();
		BeanUtils.copyProperties(card, bean);
		bean.setCardNumber(maskCardNumber(CipherUtil.decrypt(card.getCardNumber())));
		model.addAttribute("creditCard", bean);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/delete_check";
	}

	@RequestMapping(path = "/client/creditcard/delete/complete", method = RequestMethod.POST)
	public String deleteComplete(Integer id, Model model) {
		creditCardRepository.deleteById(id);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/creditcard/delete_complete";
	}
}
