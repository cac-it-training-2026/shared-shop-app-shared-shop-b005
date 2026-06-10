package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

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
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientOrderRegistController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ItemRepository itemRepository;

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInput(HttpSession session) {

		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		User buyUser = userRepository.getReferenceById(userId);

		OrderForm orderForm = new OrderForm();
		orderForm.setId(userId);
		orderForm.setPostalCode(buyUser.getPostalCode());
		orderForm.setAddress(buyUser.getAddress());
		orderForm.setName(buyUser.getName());
		orderForm.setPhoneNumber(buyUser.getPhoneNumber());
		orderForm.setPayMethod(1);

		session.setAttribute("orderForm", orderForm);

		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(Model model, HttpSession session) {

		model.addAttribute("orderForm", (OrderForm) session.getAttribute("orderForm"));

		BindingResult result = (BindingResult) session.getAttribute("bindingResult");
		if (result != null) {
			model.addAttribute("bindingResult", result);
			session.removeAttribute("bindingResult");
		}

		return "client/order/address_input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result,
			HttpSession session) {

		session.setAttribute("orderForm", orderForm);

		if (result.hasErrors()) {
			session.setAttribute("bindingResult", result);
			return "redirect:/client/order/address/input";
		} else {
			return "redirect:/client/order/payment/input";
		}

	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String paymentInput(Model model, HttpSession session) {

		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);

		return "client/order/payment_input";

	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheck(Integer payMethod, HttpSession session) {

		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		orderForm.setPayMethod(payMethod);

		session.setAttribute("orderForm", orderForm);

		return "redirect:/client/order/check";

	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model, HttpSession session) {

		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		// 在庫不足商品を入れるリストを作成
		List<String> itemNameListZero = new ArrayList<String>();
		List<String> itemNameListLessThan = new ArrayList<String>();

		// 買い物かご中の各商品に対して在庫を調べる
		for (int i = 0; i < basketBeans.size(); i++) {

			BasketBean basketItem = basketBeans.get(i);
			Item item = itemRepository.getReferenceById(basketItem.getId());
			int itemStock = item.getStock();

			// 在庫が0の場合、在庫数と注文数を0にする
			if (itemStock == 0) {
				itemNameListZero.add(basketItem.getName());
				basketItem.setStock(itemStock);
				basketItem.setOrderNum(itemStock);

				//在庫が不足している場合、在庫数と注文数を実際の在庫数に合わせる
			} else if (itemStock < basketItem.getOrderNum()) {
				itemNameListLessThan.add(basketItem.getName());
				basketItem.setStock(itemStock);
				basketItem.setOrderNum(itemStock);
			}
		}

		// 実際に買うことのできる商品を入れる買い物かごを生成
		List<BasketBean> basketAvailableBean = new ArrayList<BasketBean>();

		// 在庫が0でない商品のみ、新たな買い物かごに追加
		for (BasketBean basketBean : basketBeans) {

			if (basketBean.getOrderNum() != 0) {
				basketAvailableBean.add(basketBean);
			}
		}

		// 各リスト・買い物かごをリクエストスコープ・セッションスコープに追加
		model.addAttribute("itemNameListZero", itemNameListZero);
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		session.setAttribute("basketBeans", basketAvailableBean);

		List<OrderItemBean> orderItemList = new ArrayList<OrderItemBean>();

		for (BasketBean basketBean : basketAvailableBean) {

			OrderItemBean orderItemBean = new OrderItemBean();

			Item item = itemRepository.getReferenceById(basketBean.getId());
			orderItemBean.setId(item.getId());
			orderItemBean.setName(item.getName());
			orderItemBean.setPrice(item.getPrice());
			orderItemBean.setImage(item.getImage());
			orderItemBean.setOrderNum(basketBean.getOrderNum());
			orderItemBean.setSubtotal(orderItemBean.getPrice() * orderItemBean.getOrderNum());

			orderItemList.add(orderItemBean);
		}

		int sumPrice = 0;

		for (OrderItemBean orderItemBean : orderItemList) {

			sumPrice += orderItemBean.getSubtotal();

		}

		model.addAttribute("orderForm", orderForm);
		model.addAttribute("orderItemBeans", orderItemList);
		model.addAttribute("total", sumPrice);

		return "client/order/check";

	}

}
