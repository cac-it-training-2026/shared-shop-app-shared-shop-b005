package jp.co.sss.shop.controller.login;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.PasswordHashUtil;
import jp.co.sss.shop.util.TokenUtil;

@Controller
public class PasswordResetController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(path = "/password_reset/request", method = RequestMethod.GET)
    public String showRequest() {
        return "login/password_reset_request";
    }

    @RequestMapping(path = "/password_reset/request", method = RequestMethod.POST)
    public String handleRequest(@RequestParam String email, @RequestParam String secretAnswer, Model model) {
        User user = userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED);
        if (user != null && secretAnswer.equals(user.getSecretAnswer())) {
            String token = TokenUtil.generate();
            user.setResetToken(token);
            user.setResetTokenExpire(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000));
            userRepository.save(user);

            System.out.println("Password Reset Link: /password_reset/form?token=" + token);
            model.addAttribute("message", "再設定メールを送信しました（コンソールを確認してください）。");
        } else {
            model.addAttribute("errorMessage", "メールアドレスまたは秘密の質問の回答が正しくありません。");
        }
        return "login/password_reset_request";
    }

    @RequestMapping(path = "/password_reset/form", method = RequestMethod.GET)
    public String showForm(@RequestParam String token, Model model) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpire().before(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("errorMessage", "トークンが無効か期限切れです。");
            return "login";
        }
        model.addAttribute("token", token);
        return "login/password_reset_form";
    }

    @RequestMapping(path = "/password_reset/form", method = RequestMethod.POST)
    public String handleReset(@RequestParam String token, @RequestParam String newPassword, Model model) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpire().before(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("errorMessage", "トークンが無効か期限切れです。");
            return "login";
        }

        user.setPassword(PasswordHashUtil.hash(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpire(null);
        userRepository.save(user);

        model.addAttribute("message", "パスワードを更新しました。新しいパスワードでログインしてください。");
        return "login";
    }
}
