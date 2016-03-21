/**
 * 
 */
package com.vyiyun.weixin.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.util.CollectionUtils;

import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinUser;

/**
 * @author tf
 * 
 * @date 2015年7月15日 下午2:31:46
 * @version 1.0
 */
public class HttpRequestUtil {
	// 线程副本
	private static ThreadLocal<Map<String, Object>> threadLocalVar = new ThreadLocal<Map<String, Object>>();

	private static HttpRequestUtil instance;

	public static HttpRequestUtil getInst() {
		if (null == instance) {
			synchronized (HttpRequestUtil.class) {
				if (null == instance) {
					instance = new HttpRequestUtil();
				}
			}
		}
		return instance;
	}

	public void set(Map<String, Object> params) {
		Map<String, Object> keyValue = threadLocalVar.get();
		if (null == keyValue) {
			keyValue = new HashMap<String, Object>();
			threadLocalVar.set(keyValue);
		}
		if (!CollectionUtils.isEmpty(params)) {
			keyValue.putAll(params);
		}
	}

	public ThreadLocal<Map<String, Object>> getThreadLocalVar() {
		return threadLocalVar;
	}

	public void set(String key, Object value) {
		Map<String, Object> keyValue = threadLocalVar.get();
		if (null == keyValue) {
			keyValue = new HashMap<String, Object>();
			threadLocalVar.set(keyValue);
		}
		keyValue.put(key, value);
	}

	public Object get(String key) {
		Map<String, Object> params = threadLocalVar.get();
		if (!CollectionUtils.isEmpty(params)) {
			return params.get(key);
		}
		return null;
	}

	public String getString(String key) {
		Map<String, Object> params = threadLocalVar.get();
		if (!CollectionUtils.isEmpty(params)) {
			return StringUtil.getString(params.get(key));
		}
		return null;
	}

	public WeixinUser getCurrentWeixinUser() {
		String is_debug = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, "is_debug", "false");
		if ("true".equalsIgnoreCase(is_debug)) {
			WeixinUser weixinUser = (WeixinUser) this.get("CurrentSession");
			if (null != weixinUser) {
				return weixinUser;
			}
			return weixinUser;
		} else {
			HttpSession httpSession = (HttpSession) get("CurrentSession");
			if (null != httpSession) {
				return (WeixinUser) httpSession.getAttribute("CurrentUser");
			} else {
				throw new VyiyunException("当前用户不存在!");
			}

		}
	}

	/***************************************************** 判断当前登录应用用户session *****************************************************************/
	public boolean isOAuthSuccess() {
		HttpSession httpSession = (HttpSession) get("CurrentSession");
		if (null != httpSession) {
			return null != (WeixinUser) httpSession.getAttribute("CurrentUser");
		}
		return false;
	}

	public String getCurrentCorpId() {
		HttpSession httpSession = (HttpSession) get("CurrentSession");
		// CurrentManagerSession
		// HttpSession currentManagerSession = (HttpSession)
		// get("CurrentManagerSession");
		if (null != httpSession) {
			return (String) httpSession.getAttribute("corpId");
			// } else if (currentManagerSession != httpSession) {
			// return (String) currentManagerSession.getAttribute("corpId");
		} else {
			throw new VyiyunException("当前CorpId不存在!");
		}
	}

	public String getCurrentSuiteId() {
		HttpSession httpSession = (HttpSession) get("CurrentSession");
		if (null != httpSession) {
			return (String) httpSession.getAttribute("suiteId");
		} else {
			throw new VyiyunException("当前suiteId不存在!");
		}
	}

	public String getCurrentSuiteSecret() {
		HttpSession httpSession = (HttpSession) get("CurrentSession");
		if (null != httpSession) {
			return (String) httpSession.getAttribute("suiteSecret");
		} else {
			throw new VyiyunException("当前suiteSecret不存在!");
		}
	}
}
