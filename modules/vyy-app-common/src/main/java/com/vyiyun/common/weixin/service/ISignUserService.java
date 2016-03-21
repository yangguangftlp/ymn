package com.vyiyun.common.weixin.service;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.SignUser;

public interface ISignUserService {

	/**
	 * 添加用户签到信息
	 * 
	 * @param signUser
	 */
	public int addSignUser(SignUser... signUser);

	/**
	 * 获取用户签到信息
	 * 
	 * @param signUser
	 * @return
	 */
	public List<Map<String, Object>> getSignUser(SignUser signUser);

	/**
	 * 根据id删除用户签到信息
	 * 
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 用户签到统计信息
	 * 
	 * @param singCountInfo
	 */
	public List<Map<String, Object>> getSignCountByMonth(Map<String, Object> singCountInfo);

	/**
	 * 用户签到总时长
	 * 
	 * @param singCountInfo
	 */
	public String getTotalHourByMonth(Map<String, Object> singCountInfo);

	/**
	 * 用户出勤率统计
	 * 
	 * @param singCountInfo
	 */
	public Map<String, Object> getAttendanceRateByMonth(Map<String, Object> singCountInfo);

	/**
	 * 根据月份统计异常考勤记录
	 * 
	 * @param sqlQueryParameter
	 */
	public List<Map<String, Object>> getExceptionKQSignByMonth(Map<String, Object> singCountInfo);

	/**
	 * 更新签到用户
	 * 
	 * @param signUser
	 */
	public void updateSignUser(SignUser signUser);

}
