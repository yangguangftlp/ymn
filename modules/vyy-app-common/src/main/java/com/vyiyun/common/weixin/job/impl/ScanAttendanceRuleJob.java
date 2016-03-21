/**
 * 
 */
package com.vyiyun.common.weixin.job.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vyiyun.common.weixin.entity.AttendanceRule;
import com.vyiyun.common.weixin.entity.Sign;
import com.vyiyun.common.weixin.entity.Workday;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IAttendanceRuleService;
import com.vyiyun.common.weixin.service.ISignService;
import com.vyiyun.common.weixin.service.IWorkdayService;
import com.vyiyun.common.weixin.utils.VyiyunUtils;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.utils.CommonUtil;

/**
 * 扫描考勤规则生成考勤记录
 * 
 * @author zb.shen
 * @date 2015年12月30日
 * @version 1.0
 */
@Service("scanAttendanceRuleJob")
public class ScanAttendanceRuleJob extends AbstVvyJob {
	private static final Logger LOGGER = Logger.getLogger(ScanAttendanceRuleJob.class);

	/**
	 * 考勤规则服务
	 */
	@Autowired
	private IAttendanceRuleService attendanceRuleService;

	/**
	 * 签到服务
	 */
	@Autowired
	private ISignService signService;

	/**
	 * 假期服务
	 */
	@Autowired
	private IWorkdayService workdayService;

	@Override
	public void executeJob() {
		// 所有考勤规则
		List<AttendanceRule> rules = attendanceRuleService.getAllAttendanceRule();
		// 当前日期
		Date today = new Date();
		for (int i = 0; i < rules.size(); i++) {
			AttendanceRule rule = rules.get(i);
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

			// 检查今天是否需要签到
			boolean needSign = VyiyunUtils.checkSign4Today(rule, dayOfWeek);
			// 检查今天是否例外
			Boolean needAttend = checkAttend4Today(today, rule.getCorpId());
			if ((null == needAttend && needSign) || (null != needAttend && needAttend.booleanValue())) {
				LOGGER.info(rule.getCorpId() + "签到考勤设置");

				// 判断是否已经插入
				SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
				Sign sign = new Sign();
				sign.setCorpId(rule.getCorpId());
				sign.setSignType(CommonAppType.SignType.KQ.value());
				sqlQueryParameter.setParameter(sign);
				sqlQueryParameter.getKeyValMap().put("isKqInit", true);
				if (signService.querySignCount(sqlQueryParameter) <= 0) {
					sign.setId(CommonUtil.GeneGUID());
					sign.setUserId("admin");
					sign.setUserName("系统管理员");
					sign.setTheme("考勤");
					sign.setRemark("考勤");
					// 开始时间
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(rule.getStartHour()));
					cal.set(Calendar.MINUTE, Integer.parseInt(rule.getStartMinute()));
					cal.set(Calendar.SECOND, 0);
					sign.setBeginTime(cal.getTime());
					// 结束时间
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(rule.getEndHour()));
					cal.set(Calendar.MINUTE, Integer.parseInt(rule.getEndMinute()));
					cal.set(Calendar.SECOND, 0);
					sign.setEndTime(cal.getTime());
					signService.addSign(sign);
				}
			}
		}
	}

	/**
	 * 根据当前日期判断是否需要出勤
	 * 
	 * @param current
	 *            当前日期
	 * @param corpId
	 *            公司ID
	 * @return 是否需要出勤
	 */
	@SuppressWarnings("unchecked")
	private Boolean checkAttend4Today(Date current, String corpId) {
		Workday workDay = new Workday();
		workDay.setStartDay(current);
		workDay.setCorpId(corpId);
		List<Workday> workdays = (List<Workday>) workdayService.queryWorkdays(workDay).getData();
		if (null != workdays && !workdays.isEmpty()) {
			Workday workday = workdays.get(0);
			if (workday.getAttendOrNot().equals(CommonAppType.WorkdayFlag.YES.value())) {
				return Boolean.TRUE;
			} else if (workday.getAttendOrNot().equals(CommonAppType.WorkdayFlag.NO.value())) {
				return Boolean.TRUE;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		System.out.println(dayOfWeek);
	}
}
