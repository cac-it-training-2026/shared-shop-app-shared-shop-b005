package jp.co.sss.shop.controller.client.review;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.util.Constant;

/**
 * レビュー機能のコントローラクラス
 */
@Controller
public class ClientReviewController {

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@Autowired
	HttpSession session;

	/**
	 * レビュー入力画面表示
	 */
	@RequestMapping(path = "/client/review/input/{itemId}", method = RequestMethod.GET)
	public String reviewInput(@PathVariable Integer itemId, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/syserror";
		}

		// 購入済み判定
		if (!orderItemRepository.existsByUserIdAndItemId(userBean.getId(), itemId)) {
			return "redirect:/syserror";
		}

		Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}

		ReviewForm form = new ReviewForm();
		form.setItemId(itemId);

		// 既存レビューがあれば初期値セット
		Review review = reviewRepository.findByUserIdAndItemId(userBean.getId(), itemId);
		if (review != null) {
			form.setRating(review.getRating());
			form.setComment(review.getComment());
		}

		model.addAttribute("reviewForm", form);
		model.addAttribute("item", item);

		return "client/review/input";
	}

	/**
	 * レビュー登録・更新処理
	 */
	@RequestMapping(path = "/client/review/complete", method = RequestMethod.POST)
	public String reviewComplete(@Valid @ModelAttribute ReviewForm form, BindingResult result, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/syserror";
		}

		// 購入済み判定 (サーバーサイド検証)
		if (!orderItemRepository.existsByUserIdAndItemId(userBean.getId(), form.getItemId())) {
			return "redirect:/syserror";
		}

		if (result.hasErrors()) {
			Item item = itemRepository.findByIdAndDeleteFlag(form.getItemId(), Constant.NOT_DELETED);
			model.addAttribute("item", item);
			return "client/review/input";
		}

		Review review = reviewRepository.findByUserIdAndItemId(userBean.getId(), form.getItemId());
		if (review == null) {
			review = new Review();
			User user = new User();
			user.setId(userBean.getId());
			review.setUser(user);
			Item item = new Item();
			item.setId(form.getItemId());
			review.setItem(item);
		}

		review.setRating(form.getRating());
		review.setComment(form.getComment());

		reviewRepository.save(review);

		return "redirect:/client/item/detail/" + form.getItemId();
	}
}
