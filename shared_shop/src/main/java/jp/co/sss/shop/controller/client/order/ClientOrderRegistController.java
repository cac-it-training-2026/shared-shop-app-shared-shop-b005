package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.CreditCardBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.CreditCard;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.CreditCardRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.CipherUtil;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.DiscountCalcUtil;
import jp.co.sss.shop.util.LotteryUtil;
import jp.co.sss.shop.util.PointCalcUtil;

/**
 * 注文手続きのコントロールクラス
 * 複数Issue（クレジットカード・ポイント/くじ・レコメンド・多言語）を統合。
 */
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
		Object userObject = session.getAttribute("user");
		if (!(userObject instanceof UserBean)) {
			return "redirect:/syserror";
		}

		UserBean userBean = (UserBean) userObject;
		User user = userRepository.getReferenceById(userBean.getId());

		OrderForm orderForm = new OrderForm();
		orderForm.setId(user.getId());
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());
		orderForm.setPayMethod(1);
		orderForm.setUsedPoint(0);

		session.setAttribute("orderForm", orderForm);
		session.removeAttribute("creditCardId");
		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(Model model, HttpSession session) {
		Object orderFormObject = session.getAttribute("orderForm");
		if (!(orderFormObject instanceof OrderForm)) {
			return "redirect:/syserror";
		}

		model.addAttribute("orderForm", orderFormObject);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));

		Object bindingResultObject = session.getAttribute("bindingResult");
		if (bindingResultObject != null) {
			if (!(bindingResultObject instanceof BindingResult)) {
				return "redirect:/syserror";
			}
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", bindingResultObject);
			session.removeAttribute("bindingResult");
		}
		return "client/order/address_input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, HttpSession session) {
		Object orderFormObject = session.getAttribute("orderForm");
		if (!(orderFormObject instanceof OrderForm)) {
			return "redirect:/syserror";
		}

		OrderForm newOrderForm = (OrderForm) orderFormObject;
		BeanUtils.copyProperties(orderForm, newOrderForm, "payMethod", "usedPoint");
		session.setAttribute("orderForm", newOrderForm);

		if (result.hasErrors()) {
			session.setAttribute("bindingResult", result);
			return "redirect:/client/order/address/input";
		}
		return "redirect:/client/order/payment/input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String paymentInput(Model model, HttpSession session) {
		Object orderFormObject = session.getAttribute("orderForm");
		if (!(orderFormObject instanceof OrderForm)) {
			return "redirect:/syserror";
		}
		OrderForm orderForm = (OrderForm) orderFormObject;
		model.addAttribute("payMethod", orderForm.getPayMethod());
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));
		return "client/order/payment_input";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheck(Integer payMethod, HttpSession session) {
		Object orderFormObject = session.getAttribute("orderForm");
		if (!(orderFormObject instanceof OrderForm) || payMethod == null) {
			return "redirect:/syserror";
		}

		OrderForm orderForm = (OrderForm) orderFormObject;
		orderForm.setPayMethod(payMethod);
		session.setAttribute("orderForm", orderForm);

		if (payMethod == 1) {
			return "redirect:/client/order/card/select";
		}
		session.removeAttribute("creditCardId");
		return "redirect:/client/order/check";
	}

	@RequestMapping(path = "/client/order/card/select", method = RequestMethod.GET)
	public String cardSelect(Model model, HttpSession session) {
		Object userObject = session.getAttribute("user");
		if (!(userObject instanceof UserBean)) {
			return "redirect:/login";
		}

		UserBean userBean = (UserBean) userObject;
		List<CreditCard> cards = creditCardRepository.findByUser_IdOrderByInsertDateDescIdDesc(userBean.getId());
		List<CreditCardBean> cardBeans = new ArrayList<>();
		for (CreditCard card : cards) {
			CreditCardBean bean = new CreditCardBean();
			BeanUtils.copyProperties(card, bean);
			String num = CipherUtil.decrypt(card.getCardNumber());
			if (num != null && num.length() >= 4) {
				bean.setCardNumber("****-****-****-" + num.substring(num.length() - 4));
			}
			cardBeans.add(bean);
		}
		model.addAttribute("creditCards", cardBeans);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));
		return "client/order/card_selection";
	}

	@RequestMapping(path = "/client/order/card/select", method = RequestMethod.POST)
	public String cardSelectProcess(Integer creditCardId, HttpSession session) {
		if (creditCardId == null) {
			return "redirect:/syserror";
		}
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

	@RequestMapping(path = "/client/order/check/point", method = RequestMethod.POST)
	public String updateUsedPoint(Integer usedPoint, HttpSession session) {
		Object orderFormObject = session.getAttribute("orderForm");
		Object orderItemBeansObject = session.getAttribute("orderItemBeans");
		Object userObject = session.getAttribute("user");

		if (!(orderFormObject instanceof OrderForm) || !(orderItemBeansObject instanceof List<?>) || !(userObject instanceof UserBean)) {
			return "redirect:/syserror";
		}
		for (Object item : (List<?>) orderItemBeansObject) {
			if (!(item instanceof OrderItemBean)) {
				return "redirect:/syserror";
			}
		}

		@SuppressWarnings("unchecked")
		List<OrderItemBean> orderItemBeans = (List<OrderItemBean>) orderItemBeansObject;
		OrderForm orderForm = (OrderForm) orderFormObject;
		User user = userRepository.getReferenceById(((UserBean) userObject).getId());

		int inputPoint = PointCalcUtil.nvl(usedPoint);
		int currentPoint = PointCalcUtil.nvl(user.getCurrentPoint());
		int totalPrice = calcOrderItemTotal(orderItemBeans);

		if (usedPoint == null) {
			inputPoint = 0;
		}
		if (inputPoint < 0) {
			session.setAttribute("pointError", "利用ポイントは0以上で入力してください。");
			return "redirect:/client/order/check";
		}
		if (inputPoint > currentPoint) {
			session.setAttribute("pointError", "保有ポイントを超えて利用することはできません。");
			return "redirect:/client/order/check";
		}
		if (inputPoint > totalPrice) {
			session.setAttribute("pointError", "注文金額を超えてポイントを利用することはできません。");
			return "redirect:/client/order/check";
		}

		orderForm.setUsedPoint(inputPoint);
		session.setAttribute("orderForm", orderForm);
		session.removeAttribute("pointError");
		return "redirect:/client/order/check";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model, HttpSession session) {
		Object orderFormObject = session.getAttribute("orderForm");
		if (!(orderFormObject instanceof OrderForm)) {
			return "redirect:/syserror";
		}
		OrderForm orderForm = (OrderForm) orderFormObject;

		Object basketObject = session.getAttribute("basketBeans");
		if (!(basketObject instanceof List<?>)) {
			session.removeAttribute("basketBeans");
			return "redirect:/syserror";
		}
		for (Object item : (List<?>) basketObject) {
			if (!(item instanceof BasketBean)) {
				session.removeAttribute("basketBeans");
				return "redirect:/syserror";
			}
		}

		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) basketObject;
		List<String> itemNameListZero = new ArrayList<>();
		List<String> itemNameListLessThan = new ArrayList<>();
		List<BasketBean> basketAvailableBean = new ArrayList<>();

		for (BasketBean basketItem : basketBeans) {
			Item item = itemRepository.getReferenceById(basketItem.getId());
			int itemStock = item.getStock();
			int itemDeleteFlag = item.getDeleteFlag();

			if (itemStock == 0 || itemDeleteFlag == Constant.DELETED) {
				itemNameListZero.add(basketItem.getName());
				basketItem.setStock(0);
				basketItem.setOrderNum(0);
			} else if (itemStock < basketItem.getOrderNum()) {
				itemNameListLessThan.add(basketItem.getName());
				basketItem.setStock(itemStock);
				basketItem.setOrderNum(itemStock);
				basketAvailableBean.add(basketItem);
			} else {
				basketItem.setStock(itemStock);
				basketAvailableBean.add(basketItem);
			}
		}

		model.addAttribute("itemNameListZero", itemNameListZero);
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		session.setAttribute("basketBeans", basketAvailableBean);

		if (basketAvailableBean.isEmpty()) {
			return "client/order/check";
		}

		List<OrderItemBean> orderItemBeans = new ArrayList<>();
		int totalPrice = 0;
		int totalDiscount = 0;

		for (BasketBean basketBean : basketAvailableBean) {
			Item item = itemRepository.getReferenceById(basketBean.getId());
			OrderItemBean orderItemBean = new OrderItemBean();
			orderItemBean.setId(item.getId());
			orderItemBean.setItemId(item.getId());
			orderItemBean.setName(item.getName());
			orderItemBean.setNameEn(item.getNameEn());
			orderItemBean.setNameEs(item.getNameEs());
			orderItemBean.setNameEo(item.getNameEo());
			orderItemBean.setPrice(item.getPrice());
			orderItemBean.setImage(item.getImage());
			orderItemBean.setOrderNum(basketBean.getOrderNum());

			int discount = DiscountCalcUtil.calculateDiscount(item.getPrice(), basketBean.getOrderNum());
			totalDiscount += discount;
			orderItemBean.setSubtotal(item.getPrice() * basketBean.getOrderNum() - discount);
			totalPrice += orderItemBean.getSubtotal();
			orderItemBeans.add(orderItemBean);
		}
		session.setAttribute("orderItemBeans", orderItemBeans);

		Object userObject = session.getAttribute("user");
		if (!(userObject instanceof UserBean)) {
			return "redirect:/syserror";
		}
		User user = userRepository.getReferenceById(((UserBean) userObject).getId());
		int currentPoint = PointCalcUtil.nvl(user.getCurrentPoint());
		int usedPoint = PointCalcUtil.nvl(orderForm.getUsedPoint());
		int paymentTotal = PointCalcUtil.calcPaymentTotal(totalPrice, usedPoint);
		int earnedPoint = PointCalcUtil.calcEarnedPoint(paymentTotal);

		Object pointError = session.getAttribute("pointError");
		if (pointError != null) {
			model.addAttribute("pointError", pointError);
			session.removeAttribute("pointError");
		}

		if (orderForm.getPayMethod() != null && orderForm.getPayMethod() == 1) {
			Integer cardId = (Integer) session.getAttribute("creditCardId");
			if (cardId != null) {
				CreditCard card = creditCardRepository.findById(cardId).orElse(null);
				if (card != null) {
					String num = CipherUtil.decrypt(card.getCardNumber());
					if (num != null && num.length() >= 4) {
						model.addAttribute("selectedCardNumber", "****-****-****-" + num.substring(num.length() - 4));
					}
				}
			}
		}

		model.addAttribute("orderForm", orderForm);
		model.addAttribute("orderItemBeans", orderItemBeans);
		model.addAttribute("total", totalPrice);
		model.addAttribute("totalDiscount", totalDiscount);
		model.addAttribute("currentPoint", currentPoint);
		model.addAttribute("usedPoint", usedPoint);
		model.addAttribute("paymentTotal", paymentTotal);
		model.addAttribute("earnedPoint", earnedPoint);
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));
		return "client/order/check";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderComplete(HttpSession session) {
		Object orderItemBeansObject = session.getAttribute("orderItemBeans");
		if (!(orderItemBeansObject instanceof List<?>)) {
			session.removeAttribute("orderItemBeans");
			return "redirect:/syserror";
		}
		for (Object item : (List<?>) orderItemBeansObject) {
			if (!(item instanceof OrderItemBean)) {
				session.removeAttribute("orderItemBeans");
				return "redirect:/syserror";
			}
		}

		@SuppressWarnings("unchecked")
		List<OrderItemBean> orderItemBeans = (List<OrderItemBean>) orderItemBeansObject;
		for (OrderItemBean orderItemBean : orderItemBeans) {
			Item item = itemRepository.getReferenceById(orderItemBean.getId());
			if (item.getStock() < orderItemBean.getOrderNum() || item.getDeleteFlag() == Constant.DELETED) {
				return "redirect:/client/order/check";
			}
		}

		Object orderFormObject = session.getAttribute("orderForm");
		Object userObject = session.getAttribute("user");
		if (!(orderFormObject instanceof OrderForm) || !(userObject instanceof UserBean)) {
			return "redirect:/syserror";
		}

		OrderForm orderForm = (OrderForm) orderFormObject;
		User user = userRepository.getReferenceById(((UserBean) userObject).getId());

		int totalPrice = calcOrderItemTotal(orderItemBeans);
		int usedPoint = PointCalcUtil.nvl(orderForm.getUsedPoint());
		int currentPoint = PointCalcUtil.nvl(user.getCurrentPoint());
		if (usedPoint < 0) {
			session.setAttribute("pointError", "利用ポイントは0以上で入力してください。");
			return "redirect:/client/order/check";
		}
		if (usedPoint > currentPoint) {
			session.setAttribute("pointError", "保有ポイントを超えて利用することはできません。");
			return "redirect:/client/order/check";
		}
		if (usedPoint > totalPrice) {
			session.setAttribute("pointError", "注文金額を超えてポイントを利用することはできません。");
			return "redirect:/client/order/check";
		}
		int paymentTotal = PointCalcUtil.calcPaymentTotal(totalPrice, usedPoint);
		int earnedPoint = PointCalcUtil.calcEarnedPoint(paymentTotal);

		Order order = new Order();
		order.setPostalCode(orderForm.getPostalCode());
		order.setAddress(orderForm.getAddress());
		order.setName(orderForm.getName());
		order.setPhoneNumber(orderForm.getPhoneNumber());
		order.setPayMethod(orderForm.getPayMethod());
		order.setUser(user);
		order.setUsedPoint(usedPoint);
		order.setEarnedPoint(earnedPoint);
		order.setLotteryExecuted(0);
		order.setLotteryRank(null);
		order.setLotteryPoint(0);

		if (order.getPayMethod() != null && order.getPayMethod() == 1) {
			Integer cardId = (Integer) session.getAttribute("creditCardId");
			if (cardId != null) {
				order.setCreditCard(creditCardRepository.getReferenceById(cardId));
			}
		}
		orderRepository.save(order);

		List<OrderItem> orderItems = new ArrayList<>();
		for (OrderItemBean orderItemBean : orderItemBeans) {
			Item item = itemRepository.getReferenceById(orderItemBean.getId());

			OrderItem orderItem = new OrderItem();
			orderItem.setQuantity(orderItemBean.getOrderNum());
			orderItem.setOrder(order);
			orderItem.setItem(item);
			int discount = DiscountCalcUtil.calculateDiscount(orderItemBean.getPrice(), orderItemBean.getOrderNum());
			orderItem.setPrice((orderItemBean.getPrice() * orderItemBean.getOrderNum() - discount) / orderItemBean.getOrderNum());
			orderItem.setNameEn(orderItemBean.getNameEn());
			orderItem.setNameEs(orderItemBean.getNameEs());
			orderItem.setNameEo(orderItemBean.getNameEo());
			orderItemRepository.save(orderItem);
			orderItems.add(orderItem);

			item.setStock(item.getStock() - orderItemBean.getOrderNum());
			itemRepository.save(item);
		}

		order.setOrderItemsList(orderItems);
		orderRepository.save(order);

		int updatedCurrentPoint = currentPoint - usedPoint + earnedPoint;
		int updatedTotalPoint = PointCalcUtil.nvl(user.getTotalPoint()) + earnedPoint;
		user.setCurrentPoint(updatedCurrentPoint);
		user.setTotalPoint(updatedTotalPoint);
		user.setRank(PointCalcUtil.judgeRank(updatedTotalPoint));
		userRepository.save(user);
		updateSessionUser(user, session);

		// 注文完了画面に遷移したタイミングでワクワク感のある演出を出すため、
		// 注文確定直後に1回だけ抽選結果を確定しておく。
		applyLotteryToOrder(order, user.getId(), session);
		session.setAttribute("lotteryJustDrawn", Boolean.TRUE);

		if (!orderItemBeans.isEmpty()) {
			OrderItemBean lastItem = orderItemBeans.get(0);
			Integer itemId = (lastItem.getItemId() != null) ? lastItem.getItemId() : lastItem.getId();
			if (itemId != null) {
				Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
				if (item != null && item.getCategory() != null) {
					List<Item> recommendItems = itemRepository.findRecommendItems(
							item.getCategory().getId(), item.getId(), PageRequest.of(0, 5));
					session.setAttribute("recommendItems", recommendItems);
				}
			}
		}

		session.setAttribute("completeOrderId", order.getId());
		session.removeAttribute("orderForm");
		session.removeAttribute("basketBeans");
		session.removeAttribute("orderItemBeans");
		session.removeAttribute("creditCardId");
		return "redirect:/client/order/complete";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete(Model model, HttpSession session) {
		Object completeOrderIdObject = session.getAttribute("completeOrderId");
		Object userObject = session.getAttribute("user");
		boolean lotteryJustDrawn = Boolean.TRUE.equals(session.getAttribute("lotteryJustDrawn"));
		session.removeAttribute("lotteryJustDrawn");

		if (completeOrderIdObject instanceof Integer && userObject instanceof UserBean) {
			Integer completeOrderId = (Integer) completeOrderIdObject;
			Integer userId = ((UserBean) userObject).getId();
			Order order = orderRepository.findByIdAndUserId(completeOrderId, userId);
			if (order != null) {
				if (PointCalcUtil.nvl(order.getLotteryExecuted()) == 0) {
					// 旧データや途中状態の注文でも、完了画面表示時に1回だけ自動抽選する。
					applyLotteryToOrder(order, userId, session);
					lotteryJustDrawn = true;
				}

				model.addAttribute("order", order);
				int completeTotal = 0;
				if (order.getOrderItemsList() != null) {
					for (OrderItem orderItem : order.getOrderItemsList()) {
						completeTotal += PointCalcUtil.nvl(orderItem.getPrice()) * PointCalcUtil.nvl(orderItem.getQuantity());
					}
				}
				model.addAttribute("completeTotal", completeTotal);
				model.addAttribute("completePaymentTotal", PointCalcUtil.calcPaymentTotal(completeTotal, order.getUsedPoint()));
				model.addAttribute("usedPoint", PointCalcUtil.nvl(order.getUsedPoint()));
				model.addAttribute("earnedPoint", PointCalcUtil.nvl(order.getEarnedPoint()));
				model.addAttribute("lotteryExecuted", PointCalcUtil.nvl(order.getLotteryExecuted()));
				model.addAttribute("lotteryRank", order.getLotteryRank());
				model.addAttribute("lotteryPoint", PointCalcUtil.nvl(order.getLotteryPoint()));
				model.addAttribute("lotteryJustDrawn", lotteryJustDrawn);
				addLotteryViewAttributes(model, order.getLotteryRank());
			}
		}

		Object lotteryMessage = session.getAttribute("lotteryMessage");
		if (lotteryMessage != null) {
			model.addAttribute("lotteryMessage", lotteryMessage);
			session.removeAttribute("lotteryMessage");
		}

		@SuppressWarnings("unchecked")
		List<Item> recommendItems = (List<Item>) session.getAttribute("recommendItems");
		if (recommendItems != null) {
			model.addAttribute("recommendItems", recommendItems);
			session.removeAttribute("recommendItems");
		}
		model.addAttribute("categories", categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED));
		return "client/order/complete";
	}

	@RequestMapping(path = "/client/order/lottery", method = RequestMethod.POST)
	public String executeLottery(HttpSession session) {
		Object completeOrderIdObject = session.getAttribute("completeOrderId");
		Object userObject = session.getAttribute("user");
		if (!(completeOrderIdObject instanceof Integer) || !(userObject instanceof UserBean)) {
			return "redirect:/syserror";
		}

		Integer completeOrderId = (Integer) completeOrderIdObject;
		UserBean userBean = (UserBean) userObject;
		Order order = orderRepository.findByIdAndUserId(completeOrderId, userBean.getId());
		if (order == null) {
			return "redirect:/syserror";
		}
		if (PointCalcUtil.nvl(order.getLotteryExecuted()) == 1) {
			session.setAttribute("lotteryMessage", "この注文ではすでにくじを実行済みです。");
			return "redirect:/client/order/complete";
		}

		LotteryUtil.LotteryResult result = applyLotteryToOrder(order, userBean.getId(), session);
		session.setAttribute("lotteryJustDrawn", Boolean.TRUE);
		session.setAttribute("lotteryMessage", "くじ結果：" + result.getRank() + " / 獲得ポイント：" + result.getPoint() + "ポイント");
		return "redirect:/client/order/complete";
	}

	private LotteryUtil.LotteryResult applyLotteryToOrder(Order order, Integer userId, HttpSession session) {
		LotteryUtil.LotteryResult result = LotteryUtil.draw();
		order.setLotteryExecuted(1);
		order.setLotteryRank(result.getRank());
		order.setLotteryPoint(result.getPoint());
		orderRepository.save(order);

		User user = userRepository.getReferenceById(userId);
		int updatedCurrentPoint = PointCalcUtil.nvl(user.getCurrentPoint()) + result.getPoint();
		int updatedTotalPoint = PointCalcUtil.nvl(user.getTotalPoint()) + result.getPoint();
		user.setCurrentPoint(updatedCurrentPoint);
		user.setTotalPoint(updatedTotalPoint);
		user.setRank(PointCalcUtil.judgeRank(updatedTotalPoint));
		userRepository.save(user);
		updateSessionUser(user, session);
		return result;
	}

	private void addLotteryViewAttributes(Model model, String lotteryRank) {
		String resultClass = "rank-miss";
		String title = "また遊びに来てね";
		String message = "今回ははずれでした。次のお買い物でまたチャレンジしてください♪";
		String mascotImage = "mascot_sleep.png";

		if (LotteryUtil.LOTTERY_FIRST.equals(lotteryRank)) {
			resultClass = "rank-first";
			title = "1等 大当たり！";
			message = "ミッキスもびっくりの特賞です。1000ポイントをプレゼント！";
			mascotImage = "mascot_luxury.png";
		} else if (LotteryUtil.LOTTERY_SECOND.equals(lotteryRank)) {
			resultClass = "rank-second";
			title = "2等 おめでとう！";
			message = "とってもラッキーです。500ポイントをプレゼント！";
			mascotImage = "mascot_box.png";
		} else if (LotteryUtil.LOTTERY_THIRD.equals(lotteryRank)) {
			resultClass = "rank-third";
			title = "3等 ラッキー！";
			message = "お買い物のお礼に100ポイントをプレゼントします♪";
			mascotImage = "mascot_wave.png";
		}

		model.addAttribute("lotteryResultClass", resultClass);
		model.addAttribute("lotteryTitle", title);
		model.addAttribute("lotteryResultMessage", message);
		model.addAttribute("lotteryMascotImage", mascotImage);
	}

	private int calcOrderItemTotal(List<OrderItemBean> orderItemBeans) {
		int total = 0;
		for (OrderItemBean orderItemBean : orderItemBeans) {
			total += PointCalcUtil.nvl(orderItemBean.getSubtotal());
		}
		return total;
	}

	private void updateSessionUser(User user, HttpSession session) {
		Object userObject = session.getAttribute("user");
		if (userObject instanceof UserBean) {
			UserBean userBean = (UserBean) userObject;
			userBean.setName(user.getName());
			userBean.setAuthority(user.getAuthority());
			userBean.setCurrentPoint(user.getCurrentPoint());
			userBean.setTotalPoint(user.getTotalPoint());
			userBean.setRank(user.getRank());
			session.setAttribute("user", userBean);
		}
	}
}
