package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		/*TODO 現在は全件表示を行っている
		 * これを売れ筋（注文回数が多い順）に改修する*/

		// 注文情報の商品情報を全件表示
		List<Item> itemList = itemRepository.findAll();

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		return "index";
	}

	/**
	 * 詳細表示処理
	 *
	 * @param id      表示対象ID
	 * @param model   Viewとの値受渡し
	 * @return "client/item/detail" 詳細画面 表示
	 */
	@RequestMapping(path = "/client/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model) {

		// 商品IDに該当する商品情報を取得する
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}

		// Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);

		return "client/item/detail";
	}

	/**
	 * 商品一覧画面
	 * @author 児島涼音
	 * @param model  モデルスコープ格納用
	 * @param categoryId  商品のカテゴライズID
	 * @param sortType  並び替えする番号
	 * @return   "client/item/list"商品一覧画面
	 */
	@RequestMapping(path = "/client/item/list/{sortType}", method = RequestMethod.GET)
	public String showSortList(Model model,

			// categoryId(カテゴリ別の商品)を取得
			@RequestParam(required = false) Integer categoryId,

			// sortType(新着or売れ筋)を取得
			@PathVariable Integer sortType) {

		// 商品一覧を入れるItem型のリスト変数を用意する。初期値はnull
		List<Item> itemList = null;

		// もしsortType == 1(新着順)の場合
		if (sortType == 1) {

			// categoryId未指定の場合は全商品の新着順を取得
			if (categoryId == null || categoryId == 0) {

				// itemListにメソッドで並び替えた（新着順）itemRepositoryを格納する。
				itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(0);

			} else {

				// itemListにカテゴリ別の新着順商品を格納する。
				itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(categoryId, 0);
			}

			// もしsortType == 2(売れ筋順)の場合
		} else if (sortType == 2) {

			// categoryId未指定の場合は全商品の売れ筋順を取得
			if (categoryId == null || categoryId == 0) {

				// itemListにメソッドで並び替えた（売れ筋順）itemRepositoryを格納する。
				itemList = itemRepository.findHotItems();

			} else {

				// itemListにカテゴリ別の売れ筋順商品を格納する。
				itemList = itemRepository.findHotItemsByCategory(categoryId);
			}
		}

		// itemListのRepositoryデータをEntityからBean形式リストへコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// HTMLへの一覧表示ようにモデルスコープへ格納
		model.addAttribute("items", itemBeanList);

		// 選択したsortTypeをHTMLへ渡す。
		model.addAttribute("sortType", sortType);

		// HTMLのサイドバーにcategoriesを渡す。
		model.addAttribute("categories",
				categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0));

		// 選択したカテゴリIDをHTMLへ渡す。
		model.addAttribute("categoryId", categoryId);

		return "client/item/list";
	}
}
