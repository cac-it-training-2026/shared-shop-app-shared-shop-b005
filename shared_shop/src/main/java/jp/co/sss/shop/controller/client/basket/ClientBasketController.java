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
import jp.co.sss.shop.bean.CategoryBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;

/**
 * 買い物かごのコントロールクラス
 * 
 * @author 岩本虎太郎
 **/
@Controller
public class ClientBasketController {

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	BeanTools beanTools;

	/**
	 * 買い物かごの商品一覧表示へリダイレクトする
	 *
	 * @return "redirect:/client/basket/list" 買い物かごの商品一覧へのリダイレクト
	 * 
	 **/

	@RequestMapping(path = "/client/basket/list", method = RequestMethod.POST)
	public String showBasket() {

		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品一覧を表示する
	 * 
	 * @param model 在庫不足商品リストの受け渡し 
	 * @param session 買い物かごリストの受け渡し
	 * @return "client/basket/list" 買い物かごの商品一覧画面
	 * 
	 **/
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String showBasket(Model model, HttpSession session) {

		// 買い物かごをセッションスコープから取り出す
		Object basketObject = session.getAttribute("basketBeans");

		// 買い物かごがない場合、その旨を添えた画面を返す
		if (basketObject == null) {
			return "client/basket/list";

			// 買い物かごがリストではない場合、買い物かごをリセットしてエラー画面を返す
		} else if (!(basketObject instanceof List<?>)) {
			session.removeAttribute("basketBeans");
			return "redirect:/syserror";

			//買い物かごがリストの場合、リストにキャストする
		} else {
			List<?> basketList = (List<?>) basketObject;

			// 買い物かごの各要素が商品情報か調べる
			// 商品情報でない場合は買い物かごをリセットして画面を返す
			for (Object item : basketList) {
				if (!(item instanceof BasketBean)) {
					session.removeAttribute("basketBeans");
					return "client/basket/list";
				}
			}
		}

		// 買い物かごをBasketBeanのリストにキャストする
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
			basketItem.setImage(item.getImage());
			basketItem.setName(item.getName());
			basketItem.setNameEn(item.getNameEn());
			basketItem.setNameEs(item.getNameEs());
			basketItem.setNameEo(item.getNameEo());

			// 在庫が0または商品情報が削除されている場合、在庫数と注文数を0にする
			if (itemStock == 0 || itemDeleteFlag == 1) {
				itemNameListZero.add(basketItem.getName());
				basketItem.setStock(0);
				basketItem.setOrderNum(0);

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

		// 各リストをリクエストスコープに追加
		model.addAttribute("itemNameListZero", itemNameListZero);
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);

		// サイドバー用のカテゴリ一覧を追加
		List<Category> categoryList = categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0);
		List<CategoryBean> categoryBeanList = beanTools.copyEntityListToCategoryBeanList(categoryList);
		model.addAttribute("categories", categoryBeanList);

		// 買い物かごが空のとき、削除する
		// 買い物かごが空でないとき、セッションスコープに保存する
		if (basketAvailableBean.size() == 0) {
			session.removeAttribute("basketBeans");
		} else {
			session.setAttribute("basketBeans", basketAvailableBean);
		}

		return "client/basket/list";

	}

	/**
	 * 買い物かごに商品を追加する
	 * 
	 * @param item 追加する商品のエンティティ
	 * @param session 買い物かご情報の受け渡し
	 * @return "redirect:/client/basket/list" 買い物かごの商品一覧表示へのリダイレクト
	 **/
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket(Item item, Integer orderNum, HttpSession session) {

		// 注文個数が指定されていない場合は1とする
		if (orderNum == null || orderNum < 1) {
			orderNum = 1;
		}

		// 買い物かごをセッションスコープから取り出す
		Object basketObject = session.getAttribute("basketBeans");

		// 追加する商品のエンティティを検索
		Item addItem = itemRepository.getReferenceById(item.getId());
		if (orderNum > addItem.getStock()) {
			orderNum = addItem.getStock();
		}
		if (orderNum < 1) {
			return "redirect:/client/basket/list";
		}

		// 買い物かごがない場合、新たに買い物かごを生成して商品を追加
		if (basketObject == null) {

			// 買い物かごの生成
			List<BasketBean> basketBeans = new ArrayList<BasketBean>();

			// 買い物かごに商品を追加
			BasketBean addItemBean = new BasketBean();
			addItemBean.setId(addItem.getId());
			addItemBean.setName(addItem.getName());
			addItemBean.setNameEn(addItem.getNameEn());
			addItemBean.setNameEs(addItem.getNameEs());
			addItemBean.setNameEo(addItem.getNameEo());
			addItemBean.setStock(addItem.getStock());
			addItemBean.setOrderNum(orderNum);
			addItemBean.setPrice(addItem.getPrice());
			addItemBean.setImage(addItem.getImage());
			basketBeans.add(addItemBean);

			session.setAttribute("basketBeans", basketBeans);

			return "redirect:/client/basket/list";
		}

		// 買い物かごがある場合、BasketBeanのリストにキャストする
		// (買い物かご画面が表示されている時点で、セッションスコープにあるbasketBeansはBasketBeanのリストであることが保障されている)
		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) basketObject;

		// 追加商品が既にあるか調べる
		boolean hasItemFlag = false;
		BasketBean basketItem = null;

		for (BasketBean basketBean : basketBeans) {
			if (basketBean.getId().equals(addItem.getId())) {
				basketItem = basketBean;
				hasItemFlag = true;
				break;
			}
		}

		// 商品がある場合、在庫数を超えない範囲で購入個数を増やす
		if (hasItemFlag) {
			int newOrderNum = basketItem.getOrderNum() + orderNum;
			if (newOrderNum > addItem.getStock()) {
				newOrderNum = addItem.getStock();
			}
			basketItem.setOrderNum(newOrderNum);
			basketItem.setStock(addItem.getStock());
			basketItem.setImage(addItem.getImage());
			basketItem.setPrice(addItem.getPrice());
			basketItem.setName(addItem.getName());
			basketItem.setNameEn(addItem.getNameEn());
			basketItem.setNameEs(addItem.getNameEs());
			basketItem.setNameEo(addItem.getNameEo());

			// 商品がない場合買い物かごの先頭に新たに追加
		} else {
			BasketBean addItemBean = new BasketBean();
			addItemBean.setId(addItem.getId());
			addItemBean.setName(addItem.getName());
			addItemBean.setNameEn(addItem.getNameEn());
			addItemBean.setNameEs(addItem.getNameEs());
			addItemBean.setNameEo(addItem.getNameEo());
			addItemBean.setStock(addItem.getStock());
			addItemBean.setOrderNum(orderNum);
			addItemBean.setPrice(addItem.getPrice());
			addItemBean.setImage(addItem.getImage());
			basketBeans.add(0, addItemBean);
		}

		session.setAttribute("basketBeans", basketBeans);
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごから商品を削除する
	 * 
	 * @param id 削除する商品の製品id
	 * @param session 買い物かご情報の受け渡し
	 * @return "redirect:/client/basket/list" 買い物かごの商品一覧表示へのリダイレクト
	 **/
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteBasket(Integer id, HttpSession session) {

		// セッションスコープから買い物かごを取り出す
		// (買い物かご画面が表示されている時点で、セッションスコープにあるbasketBeansはBasketBeanのリストであることが保障されている)
		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		// 買い物かごがないとき、エラー画面を返す
		if (basketBeans == null) {
			return "redirect:/syserror";
		}

		// 数量の増減は /client/basket/update で行うため、削除ボタンでは商品行を丸ごと削除する
		basketBeans.removeIf(basketBean -> id.equals(basketBean.getId()));

		// 買い物かごに何も入っていない場合、買い物かご自体を削除
		if (basketBeans.size() == 0) {
			session.removeAttribute("basketBeans");

			// 買い物かごに商品が入っている場合は、更新後の買い物かごをセッションスコープに保存
		} else {
			session.setAttribute("basketBeans", basketBeans);
		}

		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品の個数を変更する
	 *
	 * @param id 変更する商品の製品id
	 * @param orderNum 変更後の個数
	 * @param session 買い物かご情報の受け渡し
	 * @return "redirect:/client/basket/list" 買い物かごの商品一覧表示へのリダイレクト
	 **/
	@RequestMapping(path = "/client/basket/update", method = RequestMethod.POST)
	public String updateBasket(Integer id, Integer orderNum, HttpSession session) {

		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketBeans == null || orderNum == null || orderNum < 1) {
			return "redirect:/client/basket/list";
		}

		for (BasketBean basketBean : basketBeans) {
			if (id.equals(basketBean.getId())) {
				Item item = itemRepository.getReferenceById(id);
				int stock = item.getStock();
				if (orderNum > stock) {
					orderNum = stock;
				}
				if (orderNum < 1) {
					orderNum = 1;
				}
				basketBean.setOrderNum(orderNum);
				basketBean.setStock(stock);
				basketBean.setName(item.getName());
				basketBean.setNameEn(item.getNameEn());
				basketBean.setNameEs(item.getNameEs());
				basketBean.setNameEo(item.getNameEo());
				basketBean.setImage(item.getImage());
				break;
			}
		}

		session.setAttribute("basketBeans", basketBeans);
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの全商品を削除する
	 * 
	 * @param session 買い物かご情報の受け渡し
	 * @return "redirect:/client/basket/list" 買い物かごの商品一覧表示へのリダイレクト
	 **/
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String allDeleteBasket(HttpSession session) {

		session.removeAttribute("basketBeans");
		return "redirect:/client/basket/list";
	}
}
