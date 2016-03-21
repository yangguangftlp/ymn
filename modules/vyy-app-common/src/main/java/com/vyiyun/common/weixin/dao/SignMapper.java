package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Sign;
import com.vyiyun.weixin.model.SqlQueryParameter;

public interface SignMapper {

	/**
	 * 添加签到信息
	 * 
	 * @param sign
	 */

	public int addSign(List<Sign> signs);

	/**
	 * 获取签到信息
	 * 
	 * @param sign
	 * @return
	 */
	public List<Sign> getSign(Sign sign);

	/**
	 * 根据id删除签到信息
	 * 
	 * @param id
	 */
	public void deleteById(Sign sign);

	/**
	 * 更新签到信息
	 * 
	 * @param sign
	 */
	public void update(Sign sign);

	/**
	 * 查询记录数
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public long querySignCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * 分页查询
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Sign> querySignByPage(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据id获取签到
	 * 
	 * @param id
	 * @return
	 */
	public Sign getSignById(Sign sign);

	/**
	 * 查询考勤数据列表
	 * 
	 * @param sqlQueryParameter
	 *            查询条件
	 * @return 考勤数据列表
	 */
	public List<Map<String, Object>> queryAttendanceList(SqlQueryParameter sqlQueryParameter);
}
