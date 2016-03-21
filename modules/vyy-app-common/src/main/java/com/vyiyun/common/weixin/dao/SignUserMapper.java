package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.SignUser;
import com.vyiyun.weixin.model.SqlQueryParameter;

public interface SignUserMapper {

	/**
	 * 添加用户签到信息
	 * 
	 * @param signUser
	 */
	public int addSignUser(List<SignUser> signUsers);

	/**
	 * 获取用户签到信息
	 * 
	 * @param signUser
	 * @return
	 */
	public List<SignUser> getSignUser(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据id删除用户签到信息
	 * 
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 用户签到统计
	 * 
	 * @param sqlQueryParameter
	 */
	public List<Map<String, Object>> getSignCountByMonth(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据月份统计工作时长
	 * 
	 * @param sqlQueryParameter
	 */
	public Map<String, Object> getTotalHourByMonth(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据月份统计出勤率
	 * 
	 * @param sqlQueryParameter
	 */
	public List<Map<String, Object>> getAttendanceRateByMonth(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据月份统计异常考勤记录
	 * 
	 * @param sqlQueryParameter
	 */
	public List<Map<String, Object>> getExceptionKQSignByMonth(SqlQueryParameter sqlQueryParameter);

	/**
	 * 更新签到用户信息
	 * 
	 * @param signUser
	 */
	public void updateSignUser(SignUser signUser);

}
