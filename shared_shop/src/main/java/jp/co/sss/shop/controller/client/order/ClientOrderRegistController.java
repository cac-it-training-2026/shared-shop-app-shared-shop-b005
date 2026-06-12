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
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 注文手続きのコントロールクラス
 * 
 * @author 岩本虎太郎
 **/

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

	/**
	 * 届け先入力画面へリダイレクトする
	 * 
	 * @param session ユーザー情報の受け渡し
	 * @return "redirect:/client/order/address/input" 届け先入力画面表示へのリダイレクト
	 * 
	 **/
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInput(HttpSession session) {

		// セッションスコープからユーザーIDを取得し、それに基づきユーザー情報をDBから取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		User user = userRepository.getReferenceById(userId);

		// 注文情報のフォームを新規作成し、ユーザー情報を登録
		// 支払い方法は1 (クレジットカード) に設定
		OrderForm orderForm = new OrderForm();
		orderForm.setId(userId);
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());
		orderForm.setPayMethod(1);

		// 注文情報をセッションスコープに登録
		session.setAttribute("orderForm", orderForm);

		return "redirect:/client/order/address/input";
	}

	/**
	 * 届け先入力画面を表示する
	 * 
	 * @param model 注文情報の受け渡し
	 * @param session エラー情報の受け渡し
	 * @return "client/order/address_input" 届け先入力画面
	 ***/
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(Model model, HttpSession session) {

		// 注文情報をリクエストスコープに登録
		model.addAttribute("orderForm", (OrderForm) session.getAttribute("orderForm"));

		// セッションスコープにエラー情報がある場合はエラー情報をリクエストスコープに移す
		BindingResult result = (BindingResult) session.getAttribute("bindingResult");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", result);
			session.removeAttribute("bindingResult");
		}

		return "client/order/address_input";
	}

	/**
	 * 支払い方法入力画面へリダイレクトする
	 * 
	 * @param orderForm 注文情報
	 * @param result 入力エラー情報
	 * @param session 注文情報の受け渡し
	 * @return "redirect:/client/order/payment/input" 支払い方法入力画面へのリダイレクト
	 * @return "redirect:/client/order/address/input" 届け先入力画面へのリダイレクト (入力エラーがある場合)
	 * 
	 **/
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result,
			HttpSession session) {

		// 入力された注文情報をセッションスコープに登録
		OrderForm newOrderForm = (OrderForm) session.getAttribute("orderForm");
		BeanUtils.copyProperties(orderForm, newOrderForm, "payMethod");
		session.setAttribute("orderForm", newOrderForm);

		// 入力エラーがある場合は注文情報入力画面へリダイレクト
		if (result.hasErrors()) {
			session.setAttribute("bindingResult", result);
			return "redirect:/client/order/address/input";

			// 入力エラーがない場合は支払い方法入力ページへリダイレクト
		} else {
			return "redirect:/client/order/payment/input";
		}
	}

	/**
	 * 支払い方法入力画面を表示する
	 * 
	 * @param model 支払い方法の受け渡し
	 * @param session 注文情報の受け渡し
	 * @return "client/order/payment_input" 支払い方法入力画面
	 *
	 **/
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String paymentInput(Model model, HttpSession session) {

		// セッションスコープより注文情報を取り出し、リクエストスコープに保存
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("payMethod", orderForm.getPayMethod());

		return "client/order/payment_input";
	}

	/**
	 * 注文確認画面表示へリダイレクトする
	 * 
	 * @param payMethod 支払い方法
	 * @session 
	 * @return "redirect:/client/order/check" 注文確認画面表示へのリダイレクト
	 **/
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheck(Integer payMethod, HttpSession session) {

		// セッションスコープから注文情報を取り出し、支払い方法を更新
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		orderForm.setPayMethod(payMethod);
		session.setAttribute("orderForm", orderForm);

		return "redirect:/client/order/check";
	}

	/**
	 * 届け先入力画面へリダイレクトする (戻るボタン)
	 * 
	 * @return "redirect:/client/order/address/input" 届け先入力画面へのリダイレクト
	 **/
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String paymentBack() {

		return "redirect:/client/order/address/input";
	}

	/**
	 * 注文確認画面を表示する
	 * 
	 * @param model 不足商品・注文情報・注文商品リスト・合計金額の受け渡し
	 * @param session 注文情報・注文商品リストの受け渡し
	 * @return "client/order/check" 注文確認画面
	 *
	 **/
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model, HttpSession session) {

		//セッションスコープから注文情報と買い物かご情報を取り出す
		// (買い物かご画面から進められている時点で、セッションスコープにあるbasketBeansはBasketBeanのリストであることが保障されている)
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		// 在庫不足商品を入れるリストを作成
		List<String> itemNameListZero = new ArrayList<String>();
		List<String> itemNameListLessThan = new ArrayList<String>();

		// 買い物かご中の各商品に対して在庫を調べる
		for (int i = 0; i < basketBeans.size(); i++) {

			BasketBean basketItem = basketBeans.get(i);
			Item item = itemRepository.getReferenceById(basketItem.getId());
			int itemStock = item.getStock();
			int itemDeleteFlag = item.getDeleteFlag();

			// 在庫が0、または商品情報が削除されている場合、在庫数と注文数を0にする
			if (itemStock == 0 || itemDeleteFlag == 1) {
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

		// 注文できる商品がない場合、その旨を表示する
		if (basketAvailableBean.size() == 0) {
			return "client/order/check";
		}

		// 注文商品リストを新たに作成
		List<OrderItemBean> orderItemBeans = new ArrayList<OrderItemBean>();

		// 買い物かご中の各商品の情報を注文商品リストに登録
		for (BasketBean basketBean : basketAvailableBean) {

			OrderItemBean orderItemBean = new OrderItemBean();

			Item item = itemRepository.getReferenceById(basketBean.getId());
			orderItemBean.setId(item.getId());
			orderItemBean.setName(item.getName());
			orderItemBean.setPrice(item.getPrice());
			orderItemBean.setImage(item.getImage());
			orderItemBean.setOrderNum(basketBean.getOrderNum());
			orderItemBean.setSubtotal(orderItemBean.getPrice() * orderItemBean.getOrderNum());

			orderItemBeans.add(orderItemBean);
		}

		session.setAttribute("orderItemBeans", orderItemBeans);

		// 合計金額を計算
		int totalPrice = 0;

		for (OrderItemBean orderItemBean : orderItemBeans) {
			totalPrice += orderItemBean.getSubtotal();
		}

		// 注文情報・注文商品リスト・合計金額をリクエストスコープに追加
		model.addAttribute("orderForm", orderForm);
		model.addAttribute("orderItemBeans", orderItemBeans);
		model.addAttribute("total", totalPrice);

		return "client/order/check";
	}

	/**
	 * 注文完了画面表示へリダイレクトする
	 * 
	 * @param session 注文商品リスト・ユーザー情報・買い物かご情報の受け渡し
	 * @return "redirect:/client/order/complete"  注文完了画面表示へのリダイレクト
	 * 
	 **/
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderComplete(HttpSession session) {

		// セッションスコープから注文商品のリストを取得
		// (買い物かご画面から進められている時点で、セッションスコープにあるbasketBeansはBasketBeanのリストであることが保障されている)
		@SuppressWarnings("unchecked")
		List<OrderItemBean> orderItemBeans = (List<OrderItemBean>) session.getAttribute("orderItemBeans");

		// 各商品の注文個数と在庫を比較し、不足がある場合は注文確認表示へリダイレクト
		// 商品情報が削除されている場合も注文確認表示へリダイレクト
		for (OrderItemBean orderItemBean : orderItemBeans) {

			Item item = itemRepository.getReferenceById(orderItemBean.getId());

			if (item.getStock() < orderItemBean.getOrderNum() || item.getDeleteFlag() == 1) {
				return "redirect:/client/order/check";
			}
		}

		// 注文情報エンティティを作成
		Order order = new Order();

		// 注文情報フォームをセッションスコープから取り出し、情報を注文情報エンティティにセット
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		order.setPostalCode(orderForm.getPostalCode());
		order.setAddress(orderForm.getAddress());
		order.setName(orderForm.getName());
		order.setPhoneNumber(orderForm.getPhoneNumber());
		order.setPayMethod(orderForm.getPayMethod());

		// セッションスコープからユーザーIDを取得し、それに基づきユーザー情報をDBから取得
		// 取得したユーザー情報を注文情報エンティティにセット
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		User user = userRepository.getReferenceById(userId);
		order.setUser(user);

		// 注文情報を保存
		orderRepository.save(order);

		// 商品エンティティのリストを作成
		List<OrderItem> orderItems = new ArrayList<OrderItem>();

		// 注文商品リストの各商品に対して、対応する商品エンティティを作成し保存したのち、リストに追加
		for (OrderItemBean orderItemBean : orderItemBeans) {

			Item item = itemRepository.getReferenceById(orderItemBean.getId());

			OrderItem orderItem = new OrderItem();
			orderItem.setQuantity(orderItemBean.getOrderNum());
			orderItem.setOrder(order);
			orderItem.setItem(item);
			orderItem.setPrice(orderItemBean.getPrice());
			orderItemRepository.save(orderItem);

			orderItems.add(orderItem);

			item.setStock(item.getStock() - orderItemBean.getOrderNum());
			itemRepository.save(item);
		}

		// 注文情報エンティティに注文商品リストを追加し、再度保存
		order.setOrderItemsList(orderItems);
		orderRepository.save(order);

		// セッションスコープに保存している各注文情報を破棄
		session.removeAttribute("orderForm");
		session.removeAttribute("basketBeans");
		session.removeAttribute("orderItemBeans");

		return "redirect:/client/order/complete";
	}

	/**
	 * 注文完了画面の表示
	 * 
	 * @return "client/order/complete" 注文完了画面
	 **/
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete() {
		return "client/order/complete";
	}

}
