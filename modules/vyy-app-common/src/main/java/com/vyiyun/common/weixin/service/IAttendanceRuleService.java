package com.vyiyun.common.weixin.service;

import java.util.List;

import com.vyiyun.common.weixin.entity.AttendanceRule;

/**
 * 考勤规则服务
 * @author zb.shen
 * @since 2015年12月29日
 */
public interface IAttendanceRuleService {

	/**
	 * 修改考勤规则
	 * @param attendanceRuleStr 考勤规则字符串
	 * @return 操作结果
	 */
	boolean modifyAttendanceRule(String attendanceRuleStr);

	/**
	 * 根据企业ID获取考勤规则
	 * @param corpId 企业ID
	 * @return 考勤规则
	 */
	AttendanceRule getAttendanceRule(String corpId);

	/**
	 * 获取全部考勤规则
	 * @return 考勤规则列表
	 */
	List<AttendanceRule> getAllAttendanceRule();

}
