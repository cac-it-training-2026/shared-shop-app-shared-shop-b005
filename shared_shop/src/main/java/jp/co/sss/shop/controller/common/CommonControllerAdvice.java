package jp.co.sss.shop.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.service.BeanTools;

/**
 * 全コントローラ共通の処理（サイドバーのカテゴリ表示用）
 */
@ControllerAdvice(basePackages = {"jp.co.sss.shop.controller.client", "jp.co.sss.shop.controller.admin"})
public class CommonControllerAdvice {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeanTools beanTools;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("categories",
                beanTools.copyEntityListToCategoryBeanList(categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(0)));
    }
}
