/**
 * 
 */
package com.ykkhl.ymn.web.interceptor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinUtil;
import com.vyy.weixin.annotation.Suite;

/**
 * 所有Controller 请求拦截
 * 
 * @author tf
 * 
 * @date 2015年7月3日 上午11:29:56
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class ControllerInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = Logger.getLogger(ControllerInterceptor.class);

	private List<String> excludeUrl = new ArrayList<String>();

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 当前用户点击微信菜单会传递code，此处需要到session中获取当前用户信息，如果不存在需要通过code 去获取用户信息
		// 其实只要用code一次即可，系统会根据code 拿到用户信息并将它放进session中(不存就创建一个)浏览器会得到一个sessionid
		String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
		for (String url : excludeUrl) {
			if (requestUrl.contains(url)) {
				return true;
			}
		}
		
		Suite wSuite = handler.getClass().getAnnotation(Suite.class);
		String suiteId = null;
		if(null != wSuite){
			//获取套件id
			suiteId = ConfigUtil.get(Constants.WEIXIN_APP_PATH,MessageFormat.format("suite{0}Id", wSuite.value()));
		}
		String code = request.getParameter("code");
		String userId = request.getParameter("userId");
		HttpSession httpSession = request.getSession();
		Object currentUser = httpSession.getAttribute("CurrentUser");
		JSONObject jsonObject = null;
		StringBuffer errorMsg = new StringBuffer();
		if (null != currentUser) {
			SystemCacheUtil.getInstance().set("CurrentSession", httpSession);
		} else if (StringUtils.isNotEmpty(code)) {
			jsonObject = WeixinUtil.getWXAuthUser(code);
			if (null != jsonObject && jsonObject.containsKey("UserId")) {
				LOGGER.debug("根据微信传递的code:【" + code + "】 获取用户信息:" + jsonObject);
				userId = jsonObject.getString("UserId");
				jsonObject = WeixinUtil.getUserDetail(userId);
				if (null != jsonObject) {
					if (jsonObject.containsKey("userid")) {
						initWeixinUser(httpSession, jsonObject);
						LOGGER.debug("从微信端获取用户userId:【" + userId + "】详情信息:" + jsonObject);
						return true;
					} else if (jsonObject.containsKey("OpenId")) {
						initWeixinUser(httpSession, jsonObject);
						LOGGER.debug("从微信端获取用户userId:【" + userId + "】详情信息:" + jsonObject);
						return true;
					}
				}

				else {
					errorMsg.append(jsonObject.toJSONString());
				}
			} else {
				errorMsg.append(jsonObject.toJSONString());
			}
			LOGGER.error("获取用户信息失败!原因是：" + errorMsg.toString());
			request.getRequestDispatcher(request.getContextPath() + "/common/wxuerrorView.action").forward(request,
					response);
			return false;
		} else if (StringUtils.isNotEmpty(userId))/** 当存在用户ID时 */
		{
			// 测试
			SystemConfigCache<Object> sysConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
			SystemConfig systemConfig = sysConfigCache.getSystemConfig("system","performance.test");
			if (null != systemConfig) {
				// 开启测试
				if ("1".equals(systemConfig.getValue())) {
					ICache<Object> defaultCache = SystemCacheUtil.getInstance().getWeixinContactCache();
					List<WeixinUser> weixinUserList = (List<WeixinUser>) defaultCache.get("AllUser");
					if (!CollectionUtils.isEmpty(weixinUserList)) {
						for (WeixinUser weixinUser : weixinUserList) {
							if (userId.equals(weixinUser.getUserid())) {
								httpSession.setAttribute("CurrentUser", weixinUser);
								SystemCacheUtil.getInstance().set("CurrentSession", httpSession);
								return true;
							}
						}
					}
					jsonObject = WeixinUtil.getUserDetail(userId);
					if (null != jsonObject && jsonObject.containsKey("userid")) {
						initWeixinUser(httpSession, jsonObject);
						return true;
					} else {
						errorMsg.append(jsonObject.toJSONString());
					}
					LOGGER.error("获取用户信息失败!原因是：" + errorMsg.toString());
				}
			}
			// 其他情况一律失败
			request.getRequestDispatcher(request.getContextPath() + "/common/wxuerrorView.action").forward(request,
					response);
			return false;
		} else if (StringUtils.isEmpty(code)) {
			//throw new VyiyunException("当前用户不存在!");
		}
		return true;
	}

	private void initWeixinUser(HttpSession httpSession, JSONObject jsonObject) {
		httpSession.setAttribute("CurrentUser", JSON.toJavaObject(jsonObject, WeixinUser.class));
		SystemCacheUtil.getInstance().set("CurrentSession", httpSession);
	}

	public List<String> getExcludeUrl() {
		return excludeUrl;
	}

	public void setExcludeUrl(List<String> excludeUrl) {
		if (null != excludeUrl) {
			this.excludeUrl.addAll(excludeUrl);
		}
	}
}
