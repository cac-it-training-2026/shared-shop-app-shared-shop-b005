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

/**
 * パスワード再設定用コントローラ
 */
@Controller
public class PasswordResetController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    HttpSession session;

    /**
     * パスワード再設定申請画面の表示
     */
    @RequestMapping(path = "/password_reset/request", method = RequestMethod.GET)
    public String showRequestForm() {
        return "login/password_reset_request";
    }

    /**
     * 再設定メール送信処理（模擬）
     */
    @RequestMapping(path = "/password_reset/request", method = RequestMethod.POST)
    public String handleRequest(@RequestParam String email, @RequestParam String secretAnswer, Model model) {
        User user = userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED);

        if (user != null && secretAnswer.equals(user.getSecretAnswer())) {
            // トークン生成
            String token = TokenUtil.generateToken();
            user.setResetToken(token);
            // 30分有効
            user.setResetTokenExpire(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000));
            userRepository.save(user);

            // TODO: 実際はJavaMailSenderで送信するが、ここではコンソール出力で代用
            String resetUrl = "/password_reset/form?token=" + token;
            System.out.println("Password Reset URL for " + email + ": " + resetUrl);

            model.addAttribute("message", "再設定用のメールを送信しました（コンソールを確認してください）。");
            return "login/password_reset_request";
        } else {
            model.addAttribute("errorMessage", "メールアドレスまたは秘密の質問の回答が正しくありません。");
            return "login/password_reset_request";
        }
    }

    /**
     * パスワード再設定フォームの表示
     */
    @RequestMapping(path = "/password_reset/form", method = RequestMethod.GET)
    public String showResetForm(@RequestParam String token, Model model) {
        User user = userRepository.findByResetToken(token);

        if (user == null || user.getResetTokenExpire().before(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("errorMessage", "トークンが無効か期限切れです。");
            return "login";
        }

        model.addAttribute("token", token);
        return "login/password_reset_form";
    }

    /**
     * パスワード更新処理
     */
    @RequestMapping(path = "/password_reset/form", method = RequestMethod.POST)
    public String handleReset(@RequestParam String token, @RequestParam String newPassword, Model model) {
        User user = userRepository.findByResetToken(token);

        if (user == null || user.getResetTokenExpire().before(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("errorMessage", "トークンが無効か期限切れです。");
            return "login";
        }

        // パスワード更新（ハッシュ化）
        user.setPassword(PasswordHashUtil.hashPassword(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpire(null);
        userRepository.save(user);

        model.addAttribute("message", "パスワードを再設定しました。新しいパスワードでログインしてください。");
        return "login";
    }
}
