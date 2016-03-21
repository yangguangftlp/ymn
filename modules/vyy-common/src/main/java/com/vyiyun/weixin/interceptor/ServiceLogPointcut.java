/**
 * 
 */
package com.vyiyun.weixin.interceptor;

import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.aop.ThrowsAdvice;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * Service 日志拦截器
 * 
 * @author tf
 * 
 */
public class ServiceLogPointcut implements MethodInterceptor, ThrowsAdvice {

	private static final Logger LOGGER = Logger
			.getLogger(ServiceLogPointcut.class);

	public void afterThrowing(Throwable e) {
		e.printStackTrace();
		LOGGER.debug("Servie层执行异常...", e);
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String info = invocation.getMethod().getDeclaringClass() + "."
				+ invocation.getMethod().getName();
		LOGGER.debug("Servie层开始执行【" + info + "】...参数："
				+ JSONObject.toJSONString(invocation.getArguments()));
		// 性能 测试
		SystemConfigCache<Object> systemConfigCache = SystemCacheUtil.getInstance()
				.getSystemConfigCache();
		SystemConfig systemConfig = (SystemConfig) systemConfigCache
				.getSystemConfig("system","performance.test");
		Object object = null;
		if (null != systemConfig) {
			// 开启测试
			if ("1".equals(systemConfig.getValue())) {
				long start = System.currentTimeMillis();
				LOGGER.debug("方法开始执行时间【"
						+ DateUtil.dateToString(new Date(start),
								"yyyy-MM-dd HH:mm:ss.SSS") + "】...");
				object = invocation.proceed();
				long end = System.currentTimeMillis();
				LOGGER.debug("方法结束执行时间【"
						+ DateUtil.dateToString(new Date(end),
								"yyyy-MM-dd HH:mm:ss.SSS") + "】执行时长【"
						+ (end - start) + "】毫秒...");
			} else {
				object = invocation.proceed();
			}
		} else {
			object = invocation.proceed();
		}
		LOGGER.debug("Servie层结束执行【" + info + "】...");
		return object;
	}
}
