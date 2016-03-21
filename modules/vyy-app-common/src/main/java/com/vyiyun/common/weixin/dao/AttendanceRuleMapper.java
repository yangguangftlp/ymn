package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.AttendanceRule;

/**
 * 考勤规则DAO
 * @author zb.shen
 * @since 2015年12月29日
 */
public interface AttendanceRuleMapper {

	/**
	 * 新增考勤规则
	 * @param attendanceRule 考勤规则列表
	 * @return 更新操作数
	 */
	int addAttendanceRule(List<AttendanceRule> attendanceRule);

	/**
	 * 更新考勤规则
	 * @param attendanceRule 考勤规则
	 * @return 更新操作数
	 */
	int updateAttendanceRule(AttendanceRule attendanceRule);

	/**
	 * 根据企业ID获取考勤规则
	 * @param attendanceRule 考勤规则（ID和CorpId检索）
	 * @return 考勤规则
	 */
	AttendanceRule getAttendanceRule(AttendanceRule attendanceRule);

	/**
	 * 获取全部考勤规则
	 * @return 考勤规则列表
	 */
	List<AttendanceRule> getAllAttendanceRule();

}
