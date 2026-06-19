package jp.co.sss.shop.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.util.Constant;

/**
 * 二段階認証用コントローラ
 */
@Controller
public class TwoFactorAuthController {

    @Autowired
    HttpSession session;

    /**
     * 二段階認証画面の表示
     */
    @RequestMapping(path = "/login/2fa", method = RequestMethod.GET)
    public String show2fa() {
        if (session.getAttribute("tempUser") == null) {
            return "redirect:/login";
        }
        return "login/two_factor_auth";
    }

    /**
     * 二段階認証コードの検証
     */
    @RequestMapping(path = "/login/2fa", method = RequestMethod.POST)
    public String verify2fa(@RequestParam String authCode, Model model) {
        UserBean tempUser = (UserBean) session.getAttribute("tempUser");
        String correctCode = (String) session.getAttribute("authCode");
        Long codeTime = (Long) session.getAttribute("authCodeTime");

        if (tempUser == null || correctCode == null || codeTime == null) {
            return "redirect:/login";
        }

        // 有効期限チェック（5分）
        if (System.currentTimeMillis() - codeTime > 5 * 60 * 1000) {
            model.addAttribute("errorMessage", "認証コードの有効期限が切れています。再度ログインしてください。");
            session.invalidate();
            return "login";
        }

        if (correctCode.equals(authCode)) {
            // 認証成功
            session.setAttribute("user", tempUser);
            session.removeAttribute("tempUser");
            session.removeAttribute("authCode");
            session.removeAttribute("authCodeTime");

            if (tempUser.getAuthority() == Constant.AUTH_CLIENT) {
                return "redirect:/";
            } else {
                return "redirect:/admin/menu";
            }
        } else {
            model.addAttribute("errorMessage", "認証コードが正しくありません。");
            return "login/two_factor_auth";
        }
    }
}
