package jp.co.sss.shop.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * 国際化（i18n）設定クラス
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

	/**
	 * ロケールリゾルバの設定
	 * セッションにロケール情報を保持する
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.JAPANESE);
		return slr;
	}

	/**
	 * ロケール変更インターセプターの設定
	 * リクエストパラメータ "lang" でロケールを切り替える
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	/**
	 * インターセプターの登録
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	/**
	 * メッセージソースの設定
	 */
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}
}
