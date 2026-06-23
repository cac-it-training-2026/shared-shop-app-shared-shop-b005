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

		User user = null;
		try {
			user = userRepository.findByEmailAndDeleteFlag(emailProp, Constant.NOT_DELETED);
		} catch (Exception e) {
			// リポジトリが利用できない場合などの例外ハンドリング
			return false;
		}

		if (user == null) {
			return false;
		}

		// アカウントロック状態の確認
		if (user.getAccountLocked() != null && user.getAccountLocked() == 1) {
			if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().before(new Timestamp(System.currentTimeMillis()))) {
				// ロック期限を過ぎている場合はここでは何もしない（Controllerでリセットする）
			} else {
				// ロック中
				return false;
			}
		}

		String hashedInputPassword = PasswordHashUtil.hash(passwordProp);
		// 互換性のため平文もチェック
		if (user.getPassword() != null && (hashedInputPassword.equals(user.getPassword()) || passwordProp.equals(user.getPassword()))) {
			isValidFlg = true;
		} else {
			isValidFlg = false;
		}
		return isValidFlg;
	}
}
