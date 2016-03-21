/**
 * 
 */
package com.vyy.weixin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author tf
 * 
 * @date 下午9:05:25
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface App {

	public String id();
}
