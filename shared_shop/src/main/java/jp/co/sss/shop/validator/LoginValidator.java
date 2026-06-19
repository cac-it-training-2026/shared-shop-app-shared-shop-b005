package jp.co.sss.shop.validator;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sss.shop.annotation.LoginCheck;
import jp.co.sss.shop.bean.UserBean;
import java.sql.Timestamp;

import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.util.AuthCodeUtil;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.PasswordHashUtil;

/**
 * ログインチェックの独自検証クラス
 *
 * @author System Shared
 */
public class LoginValidator implements ConstraintValidator<LoginCheck, Object> {
	private String email;
	private String password;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Override
	public void initialize(LoginCheck annotation) {
		this.email = annotation.fieldEmail();
		this.password = annotation.fieldPassword();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		boolean isValidFlg = false;
		String emailProp = (String) beanWrapper.getPropertyValue(this.email);
		String passwordProp = (String) beanWrapper.getPropertyValue(this.password);

		User user = userRepository.findByEmailAndDeleteFlag(emailProp, Constant.NOT_DELETED);

		if (user != null) {
			// アカウントロック状態の確認
			if (user.getAccountLocked() == 1) {
				if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().before(new Timestamp(System.currentTimeMillis()))) {
					// ロック期限を過ぎている場合は自動解除(本来はService等で行うべきだが、現状の制約に従いここで判定のみ行う)
					// DB更新はValidator内では行わず、コントローラ側で制御するように設計変更が必要だが
					// 既存の仕組みを維持するため、ここでは検証のみ行う
				} else {
					// ロック中
					return false;
				}
			}

			String hashedInputPassword = PasswordHashUtil.hashPassword(passwordProp);
			// 暫定対応：平文パスワードでもログイン可能にする（移行措置）
			if (hashedInputPassword.equals(user.getPassword()) || passwordProp.equals(user.getPassword())) {
				// ログイン成功
				isValidFlg = true;
			} else {
				// パスワード不一致
				isValidFlg = false;
			}
		} else {
			// ユーザが存在しない
			isValidFlg = false;
		}
		return isValidFlg;
	}
}
