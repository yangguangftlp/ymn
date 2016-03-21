/**
 * 
 */
package com.vyiyun.weixin.utils;

import org.apache.commons.lang.StringUtils;

/**
 * @author tf
 * 
 *         2015年6月25日
 */
public class StringUtil extends StringUtils {

	/**
	 * 获得对象描述字符串
	 * 
	 * @param obj
	 * @return 字符串
	 */
	public static String getString(Object obj) {
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	/**
	 * 截掉最后一个字符
	 * 
	 * @param sb
	 * @return
	 */
	public static String cutLastChar(StringBuffer sb) {
		if (sb != null) {
			return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb
					.toString();
		} else {
			return null;
		}
	}

	/**
	 * 截掉最后一个字符
	 * 
	 * @param str
	 * @return
	 */
	public static String cutLastChar(String str) {
		if (str != null) {
			return str.length() > 0 ? str.substring(0, str.length() - 1) : str;
		} else {
			return null;
		}
	}

}
