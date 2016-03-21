/**
 * 
 */
package com.vyiyun.weixin.service;

import java.util.List;
import java.util.Map;

import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.model.DataResult;

/**
 * @author tf
 * 
 *         2015年6月27日 下午7:27:30
 */
public interface ISystemStatusService {
	/**
	 * 添加系统状态
	 * 
	 * @param absent
	 */
	public void addSystemStatus(SystemStatus systemStatus);

	/**
	 * 获取系统状态信息 参数为空及所有
	 * 
	 * @return
	 */
	public List<SystemStatus> getSystemStatus(SystemStatus systemStatus);

	/**
	 * 获取系统状态信息
	 * 
	 * @return
	 */
	public DataResult getSystemStatusByPage(SystemStatus systemStatus, Map<String, Object> params, int pageIndex,
			int pageSize);

	/**
	 * 根据类型获取状态
	 * 
	 * @param type
	 * @return
	 */
	public List<SystemStatus> getSystemStatus(String corpId, String type);

	/**
	 * 根据类型获取状态
	 * 
	 * @param corpId
	 * @param type
	 * @param filterFlag
	 * 
	 * @return 系统状态列表
	 */
	public List<SystemStatus> getSystemStatus(String corpId, String type, boolean filterFlag);

	/**
	 * 根据类型获取状态
	 * 
	 * @param type
	 * @param status
	 * @return
	 */
	public SystemStatus getSystemStatus(String corpId, String type, String status);

	/**
	 * 根据id删除系统状态信息
	 * 
	 * @param id
	 */
	public int deleteBySystemStatusId(String... id);

	/**
	 * 更新系统状态信息
	 * 
	 * @param absent
	 */
	public void updateSystemStatus(SystemStatus systemStatus);
}
