package com.vyy.weixin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证OAuth2注解
 * 
 * @author tf
 * @date 2015年12月27日 上午8:00:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = { ElementType.METHOD, ElementType.TYPE })
public @interface OAuth {
	/**
	 * 默认鉴权类型 微信测0
	 * 
	 * @return
	 */
	public int type() default 0;

}
