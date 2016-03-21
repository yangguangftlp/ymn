/**
 * 
 */
package com.vyiyun.weixin.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.servlet.ModelAndView;

import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.WeixinUtil;

/**
 * 抽象controller
 * 
 * @author tf
 * 
 *         2015年6月25日
 */
public abstract class AbstWebController {

	/**
	 * 根据视图名称创建 modelAndView 视图对象
	 * 
	 * @param viewName
	 *            视图名称
	 * @return 返回视图对象
	 */
	public ModelAndView createModelAndView(String viewName) {
		ModelAndView modelView = new ModelAndView(getPrefix() + viewName);
		modelView.addObject("contextPath", HttpRequestUtil.getInst().getString("contextPath"));
		return modelView;
	}

	/**
	 * 根据视图名称创建 modelAndView 视图对象，（附带签名）
	 * 
	 * @param viewName
	 *            视图名称
	 * @return 返回视图对象
	 */
	public ModelAndView createModelAndViewWithSign(String viewName, HttpServletRequest request) {
		ModelAndView modelView = createModelAndView(viewName);
		try {
			modelView.addObject("signature", WeixinUtil.getSignature(StringUtil.getString(request.getRequestURL()),request.getQueryString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelView;
	}

	/**
	 * 获取控制对象视图前缀 例如:jsp/xxxx 子类需要根据业务情况重写该类
	 * 
	 * @return 返回视图前缀
	 */
	protected String getPrefix() {
		//return "WEB-INF/jsp/";
		return "jsp/";
	}
}
