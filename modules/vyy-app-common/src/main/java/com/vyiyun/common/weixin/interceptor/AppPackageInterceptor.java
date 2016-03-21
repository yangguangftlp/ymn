package com.vyiyun.common.weixin.interceptor;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.impl.DefaultCache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.HttpsUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyy.weixin.annotation.App;

/**
 * 公共应用拦截器 针对应用费用提醒 到期提醒
 * 
 * @author tf
 * 
 * @date 下午1:43:05
 */
public class AppPackageInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = Logger.getLogger(AppPackageInterceptor.class);
	/**
	 * 应用停止
	 */
	static String APP_STATUS = "1";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 获取当前所有应用信息
		// 根据当前应用判断是否过期
		// 提示1、系统异常
		// 提示2、应用已到期/请续费

		if (handler instanceof HandlerMethod && HttpRequestUtil.getInst().isOAuthSuccess()) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			App app = handlerMethod.getBean().getClass().getAnnotation(App.class);

			if (null != app) {
				// 获取应用Id
				String appId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, app.id() + "_agentid");
				String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
				String suiteId = HttpRequestUtil.getInst().getCurrentSuiteId();
				String key = corpId + "_appPackage";
				LOGGER.debug("当前获取应用套餐信息处理...");
				DefaultCache<Object> defaultCache = (DefaultCache<Object>) SystemCacheUtil.getInstance()
						.getDefaultCache();
				JSONArray jsonArray = (JSONArray) defaultCache.get(key);
				JSONObject jsonObject = null;
				if (CollectionUtils.isEmpty(jsonArray)) {
					SystemConfigCache<Object> systemConfigCache = (SystemConfigCache<Object>) SystemCacheUtil
							.getInstance().getSystemConfigCache();
					try {
						SystemConfig systemConfig = systemConfigCache.getSystemConfig("system", "ignoreExpireAgent");
						// 判断是否忽略应用过期
						if (null != systemConfig && "0".equals(systemConfig.getValue())) {
							systemConfig = systemConfigCache.getSystemConfig("system", "getExpireAgent");
							String url = MessageFormat.format(systemConfig.getValue(), new Object[] { corpId });
							LOGGER.debug("获取过期应用CorpId【" + corpId + "】url:" + url);
							String responseText = HttpsUtil.sendGet(url);
							LOGGER.debug("获取过期应用CorpId【" + corpId + "】响应结 果:" + responseText);
							jsonObject = JSONObject.parseObject(responseText);
							if ("true".equals(jsonObject.getString("success"))) {
								jsonArray = jsonObject.getJSONArray("data");
								// 缓存5分钟
								defaultCache.add(key, jsonArray, 1000 * 60 * 5);
							}
						} else {
							LOGGER.debug("系统配置ignoreExpireAgent配置不存在或未生效!");
						}
					} catch (Exception e) {
						LOGGER.error("获取当前企业套餐信息失败！", e);
						// 这里需要告警
						// throw new
						// VyiyunBusinessException(SpringContextHolder.getI18n("1000000"),
						// e);
					}
				}
				if (!CollectionUtils.isEmpty(jsonArray)) {
					for (int i = 0, size = jsonArray.size(); i < size; i++) {
						jsonObject = (JSONObject) jsonArray.get(i);
						if (!("2".equals(jsonObject.getString("status")))
								&& appId.equals(jsonObject.getString("appId"))
								&& suiteId.equals(jsonObject.getString("suiteId"))) {
							// 应用过期提醒
							// 应用已被停止
							// 应用已过期、请及时续费或升级应用
							if ("true".equals(jsonObject.getString("isExpire"))) {
								throw new VyiyunBusinessException(SpringContextHolder.getI18n("1002008"));
							} else if (APP_STATUS.equals(jsonObject.getString("status"))) {
								throw new VyiyunBusinessException(SpringContextHolder.getI18n("1002007"));
							}
						}
					}
				}
			}
		}
		return true;
	}
}
