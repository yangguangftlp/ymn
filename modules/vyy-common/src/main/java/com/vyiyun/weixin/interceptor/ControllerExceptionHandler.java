/**
 * 
 */
package com.vyiyun.weixin.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

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
		/*
		 * EnergyException energyException = null; if (ex instanceof
		 * BusinessEnergyException) { energyException = (EnergyException) ex;
		 * model.put("errorCode", energyException.getErrorCode());
		 * model.put("reason", energyException.getMessage()); model.put("solve",
		 * "请联系系统管理员"); return new ModelAndView("jsp/dataerror", model); } else
		 * { model.put("title", "系统异常"); model.put("msg", "请联系系统管理员"); }
		 */
		return new ModelAndView("WEB-INF/jsp/wxerror", model);
	}
}
