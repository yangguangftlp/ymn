/**
 * 
 */
package com.ykkhl.ymn.web.utils;

import com.ykkhl.ymn.web.ServiceEngine;

/**
 * 服务引擎管理
 * 
 * @author tf
 * 
 */
public class ServiceEngineManagement {

	protected ServiceEngine serviceEngine;

	private static ServiceEngineManagement instance;

	private ServiceEngineManagement() {
	}

	public static ServiceEngineManagement getInstance() {
		if (null == instance) {
			synchronized (ServiceEngineManagement.class) {
				if (null == instance) {
					instance = new ServiceEngineManagement();
				}
			}
		}
		return instance;
	}

	public void registerProcessEngine(ServiceEngine serviceEngine) {
		this.serviceEngine = serviceEngine;
	}

	public void unregister(ServiceEngine serviceEngine) {
		serviceEngine = null;
	}

	/**
	 * 获取默认的流程引擎
	 * 
	 * @return 流程引擎实例
	 */
	public ServiceEngine getDefaultServiceEngine() {
		return serviceEngine;
	}
}
