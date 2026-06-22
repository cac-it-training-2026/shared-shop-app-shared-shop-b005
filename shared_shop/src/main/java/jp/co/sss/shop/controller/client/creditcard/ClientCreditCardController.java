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
import jp.co.sss.shop.repository.CreditCardRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.CipherUtil;

/**
 * クレジットカード管理のコントローラクラス
 */
@Controller
public class ClientCreditCardController {

	@Autowired
	CreditCardRepository creditCardRepository;

	@Autowired
	UserRepository userRepository;

	/**
	 * カード番号をマスクする
	 */
	private String maskCardNumber(String cardNumber) {
		if (cardNumber == null || cardNumber.length() < 4) {
			return cardNumber;
		}
		int maskLength = cardNumber.length() - 4;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < maskLength; i++) {
			sb.append("*");
		}
		sb.append(cardNumber.substring(maskLength));
		return sb.toString();
	}

	/**
	 * クレジットカード一覧表示
	 */
	@RequestMapping(path = "/client/creditcard/list", method = RequestMethod.GET)
	public String list(HttpSession session, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		List<CreditCard> cards = creditCardRepository.findByUserIdOrderByInsertDateDescIdDesc(userBean.getId());
		List<CreditCardBean> cardBeans = new ArrayList<>();
		for (CreditCard card : cards) {
			CreditCardBean bean = new CreditCardBean();
			BeanUtils.copyProperties(card, bean);
			// 復号してからマスク
			bean.setCardNumber(maskCardNumber(CipherUtil.decrypt(card.getCardNumber())));
			cardBeans.add(bean);
		}
		model.addAttribute("creditCards", cardBeans);
		return "client/creditcard/list";
	}

	/**
	 * 登録入力
	 */
	@RequestMapping(path = "/client/creditcard/regist/input", method = RequestMethod.GET)
	public String registInput(@ModelAttribute CreditCardForm form) {
		return "client/creditcard/regist_input";
	}

	/**
	 * 登録確認
	 */
	@RequestMapping(path = "/client/creditcard/regist/check", method = RequestMethod.POST)
	public String registCheck(@Valid @ModelAttribute CreditCardForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "client/creditcard/regist_input";
		}
		model.addAttribute("creditCardForm", form);
		model.addAttribute("maskedCardNumber", maskCardNumber(form.getCardNumber()));
		return "client/creditcard/regist_check";
	}

	/**
	 * 登録完了
	 */
	@RequestMapping(path = "/client/creditcard/regist/complete", method = RequestMethod.POST)
	public String registComplete(@ModelAttribute CreditCardForm form, HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		User user = userRepository.getReferenceById(userBean.getId());
		CreditCard card = new CreditCard();
		BeanUtils.copyProperties(form, card);
		// カード番号を暗号化して保存
		card.setCardNumber(CipherUtil.encrypt(form.getCardNumber()));
		card.setUser(user);
		creditCardRepository.save(card);

		return "client/creditcard/regist_complete";
	}

	/**
	 * 変更入力
	 */
	@RequestMapping(path = "/client/creditcard/update/input/{id}", method = RequestMethod.POST)
	public String updateInput(@PathVariable Integer id, Model model) {
		CreditCard card = creditCardRepository.findById(id).orElse(null);
		if (card == null) {
			return "redirect:/syserror";
		}
		CreditCardForm form = new CreditCardForm();
		BeanUtils.copyProperties(card, form);
		// 復号してフォームにセット
		form.setCardNumber(CipherUtil.decrypt(card.getCardNumber()));
		model.addAttribute("creditCardForm", form);
		return "client/creditcard/update_input";
	}

	/**
	 * 変更確認
	 */
	@RequestMapping(path = "/client/creditcard/update/check", method = RequestMethod.POST)
	public String updateCheck(@Valid @ModelAttribute CreditCardForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "client/creditcard/update_input";
		}
		model.addAttribute("creditCardForm", form);
		model.addAttribute("maskedCardNumber", maskCardNumber(form.getCardNumber()));
		return "client/creditcard/update_check";
	}

	/**
	 * 変更完了
	 */
	@RequestMapping(path = "/client/creditcard/update/complete", method = RequestMethod.POST)
	public String updateComplete(@ModelAttribute CreditCardForm form, HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		CreditCard card = creditCardRepository.findById(form.getId()).orElse(null);
		if (card == null) {
			return "redirect:/syserror";
		}
		BeanUtils.copyProperties(form, card, "user", "insertDate");
		// カード番号を暗号化して保存
		card.setCardNumber(CipherUtil.encrypt(form.getCardNumber()));
		creditCardRepository.save(card);

		return "client/creditcard/update_complete";
	}

	/**
	 * 削除確認
	 */
	@RequestMapping(path = "/client/creditcard/delete/check/{id}", method = RequestMethod.POST)
	public String deleteCheck(@PathVariable Integer id, Model model) {
		CreditCard card = creditCardRepository.findById(id).orElse(null);
		if (card == null) {
			return "redirect:/syserror";
		}
		CreditCardBean bean = new CreditCardBean();
		BeanUtils.copyProperties(card, bean);
		// 復号してからマスク
		bean.setCardNumber(maskCardNumber(CipherUtil.decrypt(card.getCardNumber())));
		model.addAttribute("creditCard", bean);
		return "client/creditcard/delete_check";
	}

	/**
	 * 削除完了
	 */
	@RequestMapping(path = "/client/creditcard/delete/complete", method = RequestMethod.POST)
	public String deleteComplete(Integer id) {
		creditCardRepository.deleteById(id);
		return "client/creditcard/delete_complete";
	}
}
