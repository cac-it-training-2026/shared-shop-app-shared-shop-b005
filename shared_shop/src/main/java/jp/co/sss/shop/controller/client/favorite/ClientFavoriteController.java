package jp.co.sss.shop.controller.client.favorite;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.FavoriteRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.Constant;

/**
 * お気に入り機能のコントローラクラス
 */
@Controller
public class ClientFavoriteController {

	@Autowired
	FavoriteRepository favoriteRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	HttpSession session;

	/**
	 * お気に入り登録・解除処理
	 */
	@RequestMapping(path = "/client/favorite/toggle/{itemId}", method = RequestMethod.POST)
	public String toggleFavorite(@PathVariable Integer itemId) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null || userBean.getAuthority() != 2) {
			return "redirect:/syserror";
		}

		Favorite favorite = favoriteRepository.findByUserIdAndItemId(userBean.getId(), itemId);
		if (favorite != null) {
			favoriteRepository.delete(favorite);
		} else {
			Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
			if (item == null) {
				return "redirect:/syserror";
			}
			favorite = new Favorite();
			User user = new User();
			user.setId(userBean.getId());
			favorite.setUser(user);
			favorite.setItem(item);
			favoriteRepository.save(favorite);
		}

		return "redirect:/client/item/detail/" + itemId;
	}

	/**
	 * お気に入り一覧表示
	 */
	@RequestMapping(path = "/client/favorite/list", method = RequestMethod.GET)
	public String favoriteList(Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/syserror";
		}

		List<Favorite> favoriteList = favoriteRepository.findByUserIdOrderByInsertDateDesc(userBean.getId());
		model.addAttribute("favorites", favoriteList);

		return "client/favorite/list";
	}
}
