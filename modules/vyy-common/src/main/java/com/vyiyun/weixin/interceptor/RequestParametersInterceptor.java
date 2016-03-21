package com.vyiyun.weixin.interceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 请求参数拦截器
 * 
 * @author tf
 * 
 * @date 下午1:45:59
 */
public class RequestParametersInterceptor implements HandlerInterceptor {

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 请求参数
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("requestURL", request.getRequestURL().toString());
		requestParams.put("requestURI", request.getRequestURI());
		requestParams.put("contextPath", request.getContextPath());
		SystemConfigCache<Object> systemConfigCache = (SystemConfigCache<Object>) SystemCacheUtil.getInstance()
				.getSystemConfigCache();
		SystemConfig systemConfig = systemConfigCache.getSystemConfig("system", "resPath");
		StringBuffer basePath = new StringBuffer();
		basePath.append(request.getScheme()).append("://").append(request.getServerName()).append(':')
				.append(request.getServerPort()).append(request.getContextPath()).append('/');
		request.setAttribute("basePath", basePath.toString());
		if (null != systemConfig) {
			// 获取服务资源地址
			request.setAttribute("resPath", systemConfig.getValue());
			request.setAttribute("_resPath", systemConfig.getValue()+"/01/");
		}
		requestParams.putAll(request.getParameterMap());
		try {
			Enumeration<String> enumeration = null;
			if (ServletFileUpload.isMultipartContent(request)) {
				ServletFileUpload Uploader = new ServletFileUpload(new DiskFileItemFactory());
				Uploader.setHeaderEncoding("utf-8");
				List<FileItem> fileItems = Uploader.parseRequest(request);
				for (FileItem item : fileItems) {
					requestParams.put(item.getFieldName(), item);
				}
				requestParams.put("fileItems", fileItems);
			} else {
				// 获取parmeter中参数
				enumeration = request.getParameterNames();
				if (null != enumeration) {
					String key = null;
					String value = null;
					while (enumeration.hasMoreElements()) {
						key = enumeration.nextElement();
						value = request.getParameter(key);
						if (request.getMethod().equals("GET")) {
							value = new String(request.getParameter(key).getBytes("ISO-8859-1"), "UTF-8");
						}
						requestParams.put(key, value);
					}
				}
			}
			// 获取attribute中参数
			enumeration = request.getAttributeNames();
			if (null != enumeration) {
				String key = null;
				while (enumeration.hasMoreElements()) {
					key = enumeration.nextElement();
					requestParams.put(key, request.getAttribute(key));
				}
			}
		} catch (Exception e) {
		}
		HttpRequestUtil.getInst().set(requestParams);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		// 这里需要清空当前线程副本数据
		HttpRequestUtil.getInst().getThreadLocalVar().remove();
	}

}
