package com.vyiyun.weixin.service;

import java.util.List;

import com.vyiyun.weixin.entity.SystemConfig;

/**
 * 
 * @author tf
 * 
 * @date 2015年7月7日 下午1:40:30
 * @version 1.0
 */
public interface ISystemConfigService {

	/**
	 * 根据id删除系统配置信息
	 * 
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 更新系统配置信息
	 * 
	 * @param systemConfig
	 */
	public int update(SystemConfig systemConfig);

	/**
	 * 添加系统配置信息
	 * 
	 * @param systemConfig
	 */
	public void addSystemConfig(SystemConfig... systemConfig);

	/**
	 * 获取系统配置信息 如果为空将返回所有
	 * 
	 * @return
	 */
	public List<SystemConfig> getSystemConfig(SystemConfig systemConfig);

	/**
	 * 获取系统配置
	 * 
	 * @param corpId
	 * @param Name
	 * @return
	 */
	public SystemConfig getSystemConfig(String corpId, String type, String name);
}
