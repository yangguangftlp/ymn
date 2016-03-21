/**
 * 
 */
package com.vyiyun.weixin.dao;

import java.util.List;

import com.vyiyun.weixin.entity.SystemConfig;

/**
 * @author tf
 * 
 * @date 2015年7月7日 上午9:59:09
 * @version 1.0
 */
public interface SystemConfigMapper {

	/**
	 * 添加系统配置信息
	 * 
	 * @param systemConfig
	 */
	public int addSystemConfig(List<SystemConfig> systemConfig);

	/**
	 * 获取系统配置信息
	 * 
	 * @return
	 */
	public List<SystemConfig> getSystemConfig(SystemConfig systemConfig);

	/**
	 * 更新系统配置
	 * 
	 * @param systemConfig
	 *            系统配置实体
	 */
	public int updateSystemConfig(SystemConfig systemConfig);
}
