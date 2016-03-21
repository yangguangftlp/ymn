/**
 * 
 */
package com.vyiyun.common.weixin.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.utils.SpringContextHolder;

/**
 * 
 * 异常统一处理拦截器
 * @author tf
 *
 * 2015年6月26日
 */

/**
 * 常量类
 * 
 * @author tf
 * @date 2015年06月26日
 */
public class ControllerExceptionHandler implements HandlerExceptionResolver {
	private static Logger LOG = Logger.getLogger(ControllerExceptionHandler.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		LOG.error("系统控制器执行异常", ex);
		Map<String, Object> model = new HashMap<String, Object>();
		VyiyunException vyiyunException = null;
		if (ex instanceof VyiyunException) {
			vyiyunException = (VyiyunException) ex;
			model.put("errorCode", vyiyunException.getErrorCode());
			model.put("errorMsg", vyiyunException.getMessage());
			model.put("reason", vyiyunException.getReason());
			model.put("solution", vyiyunException.getSolution());
		} else {
			model.put("errorCode", "1000000");
			model.put("errorMsg", SpringContextHolder.getI18n("1000000"));
			model.put("reason", SpringContextHolder.getI18n("1000000"));
			model.put("solution", SpringContextHolder.getI18n("1000000"));
		}
		return new ModelAndView("jsp/error", model);
	}
}
