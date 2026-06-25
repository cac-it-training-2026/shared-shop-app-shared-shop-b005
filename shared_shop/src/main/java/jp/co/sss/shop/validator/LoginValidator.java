package jp.co.sss.shop.validator;

import java.sql.Timestamp;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jp.co.sss.shop.annotation.LoginCheck;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.PasswordHashUtil;

/**
 * ログインチェックの独自検証クラス
 *
 * 入力されたメールアドレスおよびパスワードを検証する。
 * パスワードはSHA-256でハッシュ化し、DBに保存されている
 * ハッシュ値と照合する。
 *
 * @author System Shared
 */
public class LoginValidator implements ConstraintValidator<LoginCheck, Object> {

	/**
	 * メールアドレス項目名
	 */
	private String email;

	/**
	 * パスワード項目名
	 */
	private String password;

	/**
	 * 会員情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * 独自検証の初期化処理
	 *
	 * @param annotation ログインチェック用アノテーション
	 */
	@Override
	public void initialize(LoginCheck annotation) {
		this.email = annotation.fieldEmail();
		this.password = annotation.fieldPassword();
	}

	/**
	 * ログイン情報検証処理
	 *
	 * 入力されたメールアドレスに該当する未削除ユーザーを取得し、
	 * アカウントロック状態およびパスワードの一致を確認する。
	 *
	 * パスワードは平文では比較せず、入力値をハッシュ化した値のみで照合する。
	 *
	 * @param value 検証対象のログインフォーム
	 * @param context 検証コンテキスト
	 * @return ログイン情報が正しい場合 true、不正な場合 false
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		BeanWrapper beanWrapper = new BeanWrapperImpl(value);

		String emailProp = (String) beanWrapper.getPropertyValue(this.email);
		String passwordProp = (String) beanWrapper.getPropertyValue(this.password);

		User user = userRepository.findByEmailAndDeleteFlag(emailProp, Constant.NOT_DELETED);

		if (user == null) {
			return false;
		}

		// アカウントロック状態の確認
		if (user.getAccountLocked() != null && user.getAccountLocked() == 1) {

			// ロック期限が過ぎている場合はController側で解除するため、ここでは検証を継続する
			if (user.getAccountLockedUntil() != null
					&& user.getAccountLockedUntil().before(new Timestamp(System.currentTimeMillis()))) {
				// 処理なし
			} else {
				return false;
			}
		}

		String hashedInputPassword = PasswordHashUtil.hash(passwordProp);

		if (hashedInputPassword.equals(user.getPassword())) {
			return true;
		}

		return false;
	}
}