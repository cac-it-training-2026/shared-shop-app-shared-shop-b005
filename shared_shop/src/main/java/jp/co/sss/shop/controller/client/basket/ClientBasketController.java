package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;

@Controller
public class ClientBasketController {

	@Autowired
	ItemRepository itemRepository;

	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String showBasket(Model model, HttpSession session) {

		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketBeans == null) {

			return "client/basket/list";
		}

		List<String> itemNameListZero = new ArrayList<String>();
		List<String> itemNameLessThan = new ArrayList<String>();

		for (int i = 0; i < basketBeans.size(); i++) {

			BasketBean basketItem = basketBeans.get(i);
			Item item = itemRepository.getReferenceById(basketItem.getId());
			int itemStock = item.getStock();

			if (itemStock == 0) {

				itemNameListZero.add(basketItem.getName());
				basketItem.setStock(itemStock);
				basketItem.setOrderNum(itemStock);

			} else if (itemStock < basketItem.getOrderNum()) {

				itemNameLessThan.add(basketItem.getName());
				basketItem.setStock(itemStock);
				basketItem.setOrderNum(itemStock);
			}
		}

		List<BasketBean> basketAvailableBean = new ArrayList<BasketBean>();

		for (BasketBean basketBean : basketBeans) {

			if (!(basketBean.getOrderNum() == 0)) {
				basketAvailableBean.add(basketBean);
			}
		}

		session.setAttribute("basketBeans", basketAvailableBean);

		return "client/basket/list";

	}

	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket(Integer id, HttpSession session) {

		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketBeans == null) {

			basketBeans = new ArrayList<BasketBean>();

		}

		for (BasketBean basketBean : basketBeans) {

			if (id.equals(basketBean.getId())) {

				basketBean.setOrderNum(basketBean.getOrderNum() + 1);
				break;

			} else {

				Item addItem = itemRepository.getReferenceById(id);

				BasketBean addItemBean = new BasketBean();
				addItemBean.setId(addItem.getId());
				addItemBean.setName(addItem.getName());
				addItemBean.setStock(addItem.getStock());

				basketBeans.add(addItemBean);
			}
		}

		session.setAttribute("basketBean", basketBeans);

		return "redirect:/client/basket/list";

	}

	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteBasket(Integer id, HttpSession session) {

		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		for (BasketBean basketBean : basketBeans) {

			if (id.equals(basketBean.getId())) {

				if (basketBean.getOrderNum() == 1) {

					basketBeans.remove(basketBean);
					break;

				} else {

					basketBean.setOrderNum(basketBean.getOrderNum() - 1);
					break;
				}

			}

		}

		session.setAttribute("basketBean", basketBeans);

		return "redirect:/client/basket/list";

	}

}
