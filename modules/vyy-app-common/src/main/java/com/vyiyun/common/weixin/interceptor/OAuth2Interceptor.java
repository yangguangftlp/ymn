/**
 * 
 */
package com.vyiyun.common.weixin.interceptor;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.HttpsUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinUtil;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * 身份验证接口
 * 
 * @author tf
 * 
 * @date 下午2:09:10
 */
public class OAuth2Interceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = Logger.getLogger(OAuth2Interceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		HttpSession httpSession = request.getSession();
		HttpRequestUtil.getInst().set("CurrentSession", httpSession);
		// 先判断是否有注解
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Suite suite = handlerMethod.getBean().getClass().getAnnotation(Suite.class);
			OAuth oauth = handlerMethod.getBean().getClass().getAnnotation(OAuth.class);
			if (null == oauth) {
				Method method = handlerMethod.getMethod();
				oauth = method.getAnnotation(OAuth.class);
			}
			if (null != oauth) {
				String resultUrl = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");
				String queryParam = request.getQueryString();
				if (oauth.type() == 0) {
					// 当前登录者动态套件信息
					if (null != suite) {
						String suiteKey = MessageFormat.format("suite_{0}_Id", suite.value());
						httpSession.setAttribute(suiteKey, suite.value());
						String suiteSecretKey = MessageFormat.format("suite_{0}_Secret", suite.value());
						httpSession.setAttribute("suiteId", ConfigUtil.get(Constants.WEIXIN_APP_PATH, suiteKey));
						httpSession.setAttribute("suiteSecret",
								ConfigUtil.get(Constants.WEIXIN_APP_PATH, suiteSecretKey));
					}
					try {
						// 这里处理所有url中带corpId
						String corpId = request.getParameter("corpId");
						String userId = request.getParameter("userId");
						if (StringUtil.isNotEmpty(corpId)) {
							httpSession.setAttribute("corpId", corpId);
						}
						if (StringUtil.isNotEmpty(corpId) && StringUtil.isEmpty(userId)) {
							String[] stringTokenizer = queryParam.split("&");
							String nextToken = null;
							StringBuffer sbParams = new StringBuffer();
							for (int i = 0, length = stringTokenizer.length; i < length; i++) {
								nextToken = stringTokenizer[i];
								if (nextToken.contains("corpId=")) {
									corpId = nextToken.replace("corpId=", "");
								} else {
									sbParams.append('&').append(nextToken);
								}
							}
							if (sbParams.length() > 0) {
								sbParams.replace(0, 1, "?");
							}
							LOGGER.debug("应用访问URL：" + resultUrl);
							resultUrl = java.net.URLEncoder.encode(resultUrl + sbParams.toString(), "utf-8");
							String url = MessageFormat
									.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope=snsapi_base&state=sunlight#wechat_redirect",
											new Object[] { corpId, resultUrl });
							LOGGER.debug("应用鉴权访问URL：" + url);
							response.sendRedirect(url);
							return false;
						} else {
							// 判断是否鉴权是否成功
							// if (!HttpRequestUtil.getInst().isOAuthSuccess())
							{
								corpId = StringUtil.getString(httpSession.getAttribute("corpId"));
								// 判断是否初始化用户成功
								boolean flag = false;
								String code = request.getParameter("code");
								JSONObject jsonObject = null;
								if (StringUtil.isNotEmpty(code)) {
									jsonObject = WeixinUtil.getCurrentUser(code);
									LOGGER.debug("根据微信传递的corpId:【" + corpId + "】,code:【" + code + "】 获取用户信息:"
											+ jsonObject);
									if (null != jsonObject && jsonObject.containsKey("UserId")) {
										userId = jsonObject.getString("UserId");
										jsonObject = WeixinUtil.getUserDetail(userId);
										if (null != jsonObject && jsonObject.containsKey("userid")) {
											WeixinUser weixinUser = JSON.toJavaObject(jsonObject, WeixinUser.class);
											weixinUser.setCorpId(corpId);
											httpSession.setAttribute("CurrentUser", weixinUser);
											HttpRequestUtil.getInst().set("CurrentSession", httpSession);
											flag = true;
											LOGGER.debug("从微信端获取用户userId:【" + userId + "】详情信息:" + jsonObject);
										}
									}
								} else if (StringUtil.isNotEmpty(userId)) {
									// 测试
									SystemConfigCache<Object> iCache = (SystemConfigCache<Object>) SystemCacheUtil
											.getInstance().getSystemConfigCache();
									SystemConfig systemConfig = iCache.getSystemConfig("system", "performance.test");
									if (null != systemConfig) {
										// 开启测试
										//if ("1".equals(systemConfig.getValue()))
										{
											jsonObject = WeixinUtil.getUserDetail(userId);
											LOGGER.debug("从微信端获取用户userId:【" + userId + "】详情信息:" + jsonObject);
											if (null != jsonObject && jsonObject.containsKey("userid")) {
												WeixinUser weixinUser = JSON.toJavaObject(jsonObject, WeixinUser.class);
												weixinUser.setCorpId(corpId);
												httpSession.setAttribute("CurrentUser", weixinUser);
												HttpRequestUtil.getInst().set("CurrentSession", httpSession);
												flag = true;
											} else {
												LOGGER.error("获取微信用户失败 原因:" + jsonObject);
												throw new VyiyunException("获取微信用户失败 原因:" + jsonObject);
											}
										}
									}
								}
								// 这里获取当前企业号所有套件下应用Id
								if (flag) {
									SystemConfigCache<Object> systemConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
									SystemConfig systemConfig = systemConfigCache.getSystemConfig("system","getAgent");
									if (null != systemConfig && StringUtil.isNotEmpty(systemConfig.getValue())) {
										String url = MessageFormat.format(systemConfig.getValue(),
												new Object[] { corpId });
										jsonObject = JSONObject.parseObject(HttpsUtil.sendGet(url));
										LOGGER.debug("获取应用getAgent地址【" + url + "】,响应结果:" + jsonObject);
										if (null != jsonObject
												&& "true".equalsIgnoreCase(jsonObject.getString("success"))) {
											LOGGER.debug("获取应用应用ID，成功：" + jsonObject);
											SystemCacheUtil.getInstance().getDefaultCache()
													.add(corpId + "_suite", jsonObject.getJSONObject("data"));
										} else {
											LOGGER.debug("获取应用应用ID失败，原因是：" + jsonObject);
										}

									} else {
										LOGGER.debug("获取应用应用URL配置不存在!");
										throw new VyiyunException("系统异常，请联系系统管理员。");
									}
								}
							}
							if (!HttpRequestUtil.getInst().isOAuthSuccess()) {
								throw new VyiyunException("当前用户验证失败!");
							}
						}
					} catch (Exception e) {
						LOGGER.error("当前用户未验证...", e);
						throw new VyiyunException("当前用户未验证", e);
					}
				}
			}
		}
		return true;
	}
}
