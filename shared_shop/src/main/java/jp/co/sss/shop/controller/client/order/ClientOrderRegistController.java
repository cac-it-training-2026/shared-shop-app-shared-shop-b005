package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;
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
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.bean.CreditCardBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.CreditCard;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.repository.CreditCardRepository;
import jp.co.sss.shop.util.CipherUtil;
import jp.co.sss.shop.util.DiscountCalcUtil;

@Controller
public class ClientOrderRegistController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	OrderItemRepository orderItemRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CreditCardRepository creditCardRepository;

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInput(HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) return "redirect:/syserror";
		User user = userRepository.getReferenceById(userBean.getId());
		OrderForm orderForm = new OrderForm();
		orderForm.setId(user.getId());
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());
		orderForm.setPayMethod(1);
		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(Model model, HttpSession session) {
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		if (orderForm == null) return "redirect:/syserror";
		model.addAttribute("orderForm", orderForm);
		BindingResult result = (BindingResult) session.getAttribute("bindingResult");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", result);
			session.removeAttribute("bindingResult");
		}
		return "client/order/address_input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, HttpSession session) {
		OrderForm currentForm = (OrderForm) session.getAttribute("orderForm");
		if (currentForm == null) return "redirect:/syserror";
		BeanUtils.copyProperties(orderForm, currentForm, "payMethod");
		session.setAttribute("orderForm", currentForm);
		if (result.hasErrors()) {
			session.setAttribute("bindingResult", result);
			return "redirect:/client/order/address/input";
		}
		return "redirect:/client/order/payment/input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String paymentInput(Model model, HttpSession session) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		if (orderForm == null) return "redirect:/syserror";
		model.addAttribute("payMethod", orderForm.getPayMethod());
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/order/payment_input";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheck(Integer payMethod, HttpSession session) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		if (orderForm == null || payMethod == null) return "redirect:/syserror";
		orderForm.setPayMethod(payMethod);
		session.setAttribute("orderForm", orderForm);
		if (payMethod == 1) return "redirect:/client/order/card/select";
		return "redirect:/client/order/check";
	}

	@RequestMapping(path = "/client/order/card/select", method = RequestMethod.GET)
	public String cardSelect(Model model, HttpSession session) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) return "redirect:/login";
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		List<CreditCard> cards = creditCardRepository.findByUser_IdOrderByInsertDateDescIdDesc(userBean.getId());
		List<CreditCardBean> cardBeans = new ArrayList<>();
		for (CreditCard card : cards) {
			CreditCardBean bean = new CreditCardBean();
			BeanUtils.copyProperties(card, bean);
			String num = CipherUtil.decrypt(card.getCardNumber());
			bean.setCardNumber("****-****-****-" + num.substring(num.length() - 4));
			cardBeans.add(bean);
		}
		model.addAttribute("creditCards", cardBeans);
		return "client/order/card_selection";
	}

	@RequestMapping(path = "/client/order/card/select", method = RequestMethod.POST)
	public String cardSelectProcess(Integer creditCardId, HttpSession session) {
		if (creditCardId == null) return "redirect:/syserror";
		session.setAttribute("creditCardId", creditCardId);
		return "redirect:/client/order/check";
	}

	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String paymentBack() {
		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/card/back", method = RequestMethod.POST)
	public String cardBack() {
		return "redirect:/client/order/payment/input";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model, HttpSession session) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		if (orderForm == null || basketBeans == null) return "redirect:/syserror";

		List<String> itemNameListZero = new ArrayList<>();
		List<String> itemNameListLessThan = new ArrayList<>();
		List<BasketBean> basketAvailable = new ArrayList<>();

		for (BasketBean bb : basketBeans) {
			Item item = itemRepository.getReferenceById(bb.getId());
			if (item.getStock() == 0 || item.getDeleteFlag() == 1) {
				itemNameListZero.add(bb.getName());
			} else if (item.getStock() < bb.getOrderNum()) {
				itemNameListLessThan.add(bb.getName());
				bb.setOrderNum(item.getStock());
				bb.setStock(item.getStock());
				basketAvailable.add(bb);
			} else {
				bb.setStock(item.getStock());
				basketAvailable.add(bb);
			}
		}
		session.setAttribute("basketBeans", basketAvailable);
		model.addAttribute("itemNameListZero", itemNameListZero);
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		if (basketAvailable.isEmpty()) return "client/order/check";

		List<OrderItemBean> orderItemBeans = new ArrayList<>();
		int totalPrice = 0;
		int totalDiscount = 0;
		for (BasketBean bb : basketAvailable) {
			OrderItemBean oib = new OrderItemBean();
			oib.setId(bb.getId());
			oib.setName(bb.getName());
			oib.setPrice(bb.getPrice());
			oib.setImage(itemRepository.getReferenceById(bb.getId()).getImage());
			oib.setOrderNum(bb.getOrderNum());
			int discount = DiscountCalcUtil.calculateDiscount(bb.getPrice(), bb.getOrderNum());
			totalDiscount += discount;
			oib.setSubtotal(bb.getPrice() * bb.getOrderNum() - discount);
			totalPrice += oib.getSubtotal();
			orderItemBeans.add(oib);
		}
		session.setAttribute("orderItemBeans", orderItemBeans);
		model.addAttribute("orderForm", orderForm);
		model.addAttribute("orderItemBeans", orderItemBeans);
		model.addAttribute("total", totalPrice);
		model.addAttribute("totalDiscount", totalDiscount);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));

		if (orderForm.getPayMethod() == 1) {
			Integer cardId = (Integer) session.getAttribute("creditCardId");
			if (cardId != null) {
				CreditCard card = creditCardRepository.findById(cardId).orElse(null);
				if (card != null) {
					String num = CipherUtil.decrypt(card.getCardNumber());
					model.addAttribute("selectedCardNumber", "****-****-****-" + num.substring(num.length() - 4));
				}
			}
		}
		return "client/order/check";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderComplete(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<OrderItemBean> oibs = (List<OrderItemBean>) session.getAttribute("orderItemBeans");
		OrderForm of = (OrderForm) session.getAttribute("orderForm");
		UserBean ub = (UserBean) session.getAttribute("user");
		if (oibs == null || of == null || ub == null) return "redirect:/syserror";

		for (OrderItemBean oib : oibs) {
			Item item = itemRepository.getReferenceById(oib.getId());
			if (item.getStock() < oib.getOrderNum() || item.getDeleteFlag() == 1) return "redirect:/client/order/check";
		}

		Order order = new Order();
		BeanUtils.copyProperties(of, order);
		order.setUser(userRepository.getReferenceById(ub.getId()));
		if (order.getPayMethod() == 1) {
			Integer cardId = (Integer) session.getAttribute("creditCardId");
			if (cardId != null) order.setCreditCard(creditCardRepository.getReferenceById(cardId));
		}
		orderRepository.save(order);

		List<OrderItem> items = new ArrayList<>();
		for (OrderItemBean oib : oibs) {
			Item item = itemRepository.getReferenceById(oib.getId());
			OrderItem oi = new OrderItem();
			oi.setOrder(order);
			oi.setItem(item);
			oi.setQuantity(oib.getOrderNum());
			int discount = DiscountCalcUtil.calculateDiscount(oib.getPrice(), oib.getOrderNum());
			oi.setPrice((oib.getPrice() * oib.getOrderNum() - discount) / oib.getOrderNum());
			orderItemRepository.save(oi);
			items.add(oi);
			item.setStock(item.getStock() - oib.getOrderNum());
			itemRepository.save(item);
		}
		order.setOrderItemsList(items);
		orderRepository.save(order);

		session.removeAttribute("orderForm");
		session.removeAttribute("basketBeans");
		session.removeAttribute("orderItemBeans");
		session.removeAttribute("creditCardId");
		return "redirect:/client/order/complete";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete(Model model) {
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));
		return "client/order/complete";
	}
}
