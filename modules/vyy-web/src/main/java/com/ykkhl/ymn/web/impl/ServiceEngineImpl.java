/**
 * 
 */
package com.ykkhl.ymn.web.impl;

import com.ykkhl.ymn.web.ServiceEngine;

/**
 * @author tf
 * 
 */
public class ServiceEngineImpl implements ServiceEngine {

	private String name;

	private ServiceEngineConfigImpl serviceEngineConfig;

	public ServiceEngineImpl(ServiceEngineConfigImpl serviceEngineConfig) {
		this.name = serviceEngineConfig.getName();
		// ServiceEngineManagement.registerProcessEngine(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ServiceEngineConfigImpl getServiceEngineConfig() {
		return serviceEngineConfig;
	}

	@Override
	public <T> T getService(Class<T> interfaceClass) {
		return null;
	}

}
