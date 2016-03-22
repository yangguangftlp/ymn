/**
 * 
 */
package com.ykkhl.ymn.web;

import com.ykkhl.ymn.web.impl.ServiceEngineConfigImpl;

/**
 * 服务引擎
 * 
 * @author tf
 * 
 */
public interface ServiceEngine {
	
	/**
	 * 默认名称为 'default'
	 */
	String getName();

	
	/**
	 * 获取流程引擎配置
	 * 
	 * @return
	 */
	ServiceEngineConfigImpl getServiceEngineConfig();

	/**
	 * 获取外部注册的service
	 * 
	 * @param interfaceClass
	 * @return
	 */
	public <T> T getService(Class<T> interfaceClass);
}
