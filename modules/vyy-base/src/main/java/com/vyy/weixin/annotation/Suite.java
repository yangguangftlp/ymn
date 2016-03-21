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
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Suite {
	String value();
}
