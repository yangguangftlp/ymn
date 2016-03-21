/**
 * 
 */
package com.vyiyun.weixin.dao;

import java.util.List;

import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * @author tf
 * 
 *         2015年6月27日 下午6:33:35
 */
public interface SystemStatusMapper {

	/**
	 * 添加系统状态
	 * 
	 * @param systemStatus
	 */
	public void addSystemStatus(List<SystemStatus> systemStatus);

	/**
	 * 添加系统状态(当Value为空且Value值为数字的场合) <br>
	 * 如果Value为空，且预设的Value不为数字，该条记录的Value将会被设置为1，如需要，请手动修改数据
	 * 
	 * @param systemStatus
	 */
	public void addSystemStatusOnNullValue(SystemStatus systemStatus);

	/**
	 * 获取系统状态信息
	 * 
	 * @return
	 */
	public List<SystemStatus> getAllSystemStatus();

	/**
	 * 获取系统状态信息
	 * 
	 * @return
	 */
	public List<SystemStatus> getSystemStatus(SystemStatus systemStatus);

	/**
	 * 根据类型获取状态 该方法支持缓存
	 * 
	 * @param type
	 * @return
	 */
	public List<SystemStatus> getSystemStatus(String type);

	/**
	 * 更新系统状态信息
	 * 
	 * @param absent
	 */
	public void updateSystemStatus(SystemStatus systemStatus);

	/**
	 * 获取分页数据
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<SystemStatus> getSystemStatusByPage(SqlQueryParameter sqlQueryParameter);

	/**
	 * 删除
	 * 
	 * @param id
	 */
	public int deleteBySystemStatusId(List<String> ids);

}
