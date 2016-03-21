package com.vyiyun.common.weixin.interceptor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.utils.AES;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.HttpsUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 
 * @author tf
 * 
 * @date 下午2:12:29
 */
public class AppManageInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = Logger.getLogger(AppManageInterceptor.class);

	/**
	 * ip白名单
	 */
	private List<String> ipList = new ArrayList<String>();
	/**
	 * suiteSecret
	 */
	private Map<String, String> suiteSecretMap = new HashMap<String, String>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 管理端需要获取当前企业corpId
		HttpSession httpSession = request.getSession();
		HttpRequestUtil.getInst().set("CurrentSession", httpSession);
		String corpId = request.getParameter("corpId");
		String suiteId = request.getParameter("suiteId");
		String remoteAddr = request.getRemoteAddr();
		String remoteHost = request.getRemoteHost();
		String aKey = request.getParameter("aKey");
		String referer = request.getHeader("Referer");
		// 测试
		SystemConfigCache<Object> iCache = (SystemConfigCache<Object>) SystemCacheUtil.getInstance()
				.getSystemConfigCache();
		SystemConfig systemConfig = iCache.getSystemConfig("system", "performance.test");
		if (null != systemConfig) {
			// 开启测试
			if (!"1".equals(systemConfig.getValue())) {
				String isAuth = StringUtil.getString(httpSession.getAttribute("isAuth"));
				if (StringUtil.isEmpty(isAuth)) {
					if (StringUtil.isEmpty(aKey)) {
						LOGGER.debug("远程恶意客户端【0101】调用ip地址:" + remoteAddr + "-" + remoteHost);
						throw new VyiyunException("禁止当前用户非法访问!");
					}
					LOGGER.debug("鉴权密钥aKey:" + aKey);
					byte[] keys = AES.parseHexStr2Byte(aKey);
					byte[] dekey = AES.decrypt(keys, "JsYkhl@2016");
					String key = new String(dekey);
					if ("PNZxTEFjl2Je7SHd".equals(key) && referer.contains("www.vyiyun.com")) {
						isAuth = "isAuth";
						httpSession.setAttribute("isAuth", isAuth);
						response.addCookie(new Cookie("JSESSIONID", httpSession.getId()));
						LOGGER.debug("鉴权成功...");
					}
				}
				if (!"isAuth".equals(isAuth)) {
					// 非法访问
					LOGGER.debug("远程恶意客户端【0101】调用ip地址:" + remoteAddr + "-" + remoteHost);
					throw new VyiyunException("禁止当前用户非法访问!");
				}
			}
		}
		LOGGER.debug("远程客户端调用ip地址:" + remoteAddr + "-" + remoteHost);
		// ip 白名单校验
		// 获取ip白名单
		String[] ips = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, "inIps", "").split(",");

		if (null != ips && ips.length > 0) {
			List<String> ipList = Arrays.asList(ips);
			if (!ipList.contains(remoteAddr)) {
				// throw new VyiyunException("不允许ip【" + remoteAddr +
				// "】访问");
			}
		} else {
			throw new VyiyunException("ip白名单没有配置");
		}

		String _corpId = StringUtil.getString(httpSession.getAttribute("corpId"));
		String _suiteId = StringUtil.getString(httpSession.getAttribute("suiteId"));
		String _suiteSecret = StringUtil.getString(httpSession.getAttribute("suiteSecret"));
		if (StringUtil.isNotEmpty(corpId)) {
			httpSession.setAttribute("corpId", corpId);
		} else if (StringUtil.isEmpty(_corpId)) {
			LOGGER.debug("获取企业corpId失败...");
			throw new VyiyunException("获取企业corpId失败!");
		}
		if (StringUtil.isNotEmpty(suiteId)) {
			httpSession.setAttribute("suiteId", suiteId);
		} else if (StringUtil.isEmpty(_suiteId)) {
			LOGGER.debug("获取企业suiteId失败...");
			throw new VyiyunException("获取企业suiteId失败!");
		}
		String suiteSecret = this.suiteSecretMap.get(suiteId);
		if (StringUtil.isNotEmpty(suiteSecret)) {
			httpSession.setAttribute("suiteSecret", suiteSecret);
		} else if (StringUtil.isEmpty(_suiteSecret)) {
			LOGGER.debug("获取企业suiteSecret失败...");
			throw new VyiyunException("获取企业suiteSecret失败!");
		}
		_corpId = StringUtil.getString(httpSession.getAttribute("corpId"));
		_suiteId = StringUtil.getString(httpSession.getAttribute("suiteId"));
		_suiteSecret = StringUtil.getString(httpSession.getAttribute("suiteSecret"));
		if (StringUtil.isNotEmpty(_corpId)) {
			JSONObject jsonObject = (JSONObject) SystemCacheUtil.getInstance().getDefaultCache()
					.get(_corpId + "_suite");
			LOGGER.debug("获取当前应用ID,corpId:" + _corpId + ",suiteId:" + _suiteId + ",suiteSecret:" + _suiteSecret
					+ ",套件应用信息:" + jsonObject);
			if (CollectionUtils.isEmpty(jsonObject) || !jsonObject.containsKey(suiteId)) {
				ICache<Object> iCahe = SystemCacheUtil.getInstance().getSystemConfigCache();
				systemConfig = (SystemConfig) iCahe.get("system_getAgent");
				if (null != systemConfig && StringUtil.isNotEmpty(systemConfig.getValue())) {
					String url = MessageFormat.format(systemConfig.getValue(), new Object[] { _corpId });
					jsonObject = JSONObject.parseObject(HttpsUtil.sendGet(url));
					LOGGER.debug("获取应用getAgent地址【" + url + "】,响应结果:" + jsonObject);
					if (null != jsonObject && "true".equalsIgnoreCase(jsonObject.getString("success"))) {
						SystemCacheUtil.getInstance().getDefaultCache()
								.add(_corpId + "_suite", jsonObject.getJSONObject("data"));
					}
				}
			}
			// 再次获取应用套件信息
			jsonObject = (JSONObject) SystemCacheUtil.getInstance().getDefaultCache().get(_corpId + "_suite");
			if (CollectionUtils.isEmpty(jsonObject)) {
				LOGGER.debug("获取应用应用ID失败!");
				throw new VyiyunException("系统异常，请联系系统管理员。");
			}
		}

		return true;
	}

	public void setIpList(List<String> ipList) {
		if (!CollectionUtils.isEmpty(ipList)) {
			this.ipList.addAll(ipList);
		}
	}

	public void setSuiteSecretMap(Map<String, String> suiteSecretMap) {
		if (!CollectionUtils.isEmpty(suiteSecretMap)) {
			this.suiteSecretMap.putAll(suiteSecretMap);
		}
	}
}
