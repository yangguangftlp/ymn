/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.dao.AttendanceRuleMapper;
import com.vyiyun.common.weixin.dao.WorkdayMapper;
import com.vyiyun.common.weixin.entity.AttendanceRule;
import com.vyiyun.common.weixin.entity.Workday;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IWorkdayService;
import com.vyiyun.common.weixin.utils.VyiyunUtils;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;

/**
 * 工作日配置服务实现
 * @author zb.shen
 * @date 2015年12月31日
 * @version 1.0
 */
@Service
public class WorkdayServiceImpl implements IWorkdayService {

	/**
	 * 工作日配置DAO
	 */
	@Autowired
	private WorkdayMapper workdayDao;

	/**
	 * 考勤规则DAO
	 */
	@Autowired
	private AttendanceRuleMapper attendanceRuleDao;

	@Override
	public String[] addWorkday(String workDayStr) {
		List<Workday> workdays = JSONObject.parseArray(workDayStr, Workday.class);
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		Workday param = new Workday();
		param.setCorpId(corpId);
		List<Workday> existDays = workdayDao.queryWorkdays(param); // 已设置工作日列表
		AttendanceRule rule = attendanceRuleDao.getAttendanceRule(new AttendanceRule(null, corpId)); // 考勤规则
		// 工作日ID列表
		List<String> ids = new ArrayList<String>();
		for (int i = 0; i < workdays.size(); i++) {
			Workday workday = workdays.get(i);
			if (!CollectionUtils.isEmpty(existDays)) {
				// 检查是否当前设置日期与已设置工作日冲突
				Iterator<Workday> iter = existDays.iterator();
				for (; iter.hasNext();) {
					Workday existDay = iter.next();
					if (existDay.getStartDay().after(workday.getEndDay()) || existDay.getEndDay().before(workday.getStartDay())) {
					} else {
						if (!existDay.getStartDay().after(workday.getEndDay())
								&& existDay.getEndDay().after(workday.getStartDay())) {
							throw new VyiyunException(
									"当前设置结束日" + DateUtil.dateToString(workday.getEndDay(), "yyyy-MM-dd")
									+ "与"
									+ existDay.getRemark() + "所设置开始日"
									+ DateUtil.dateToString(existDay.getStartDay(), "yyyy-MM-dd") + "冲突");
						} else if (!existDay.getEndDay().before(workday.getStartDay())
								&& existDay.getStartDay().before(workday.getEndDay())) {
							throw new VyiyunException(
									"当前设置开始日" + DateUtil.dateToString(workday.getStartDay(), "yyyy-MM-dd")
									+ "与"
									+ existDay.getRemark() + "所设置结束日"
									+ DateUtil.dateToString(existDay.getEndDay(), "yyyy-MM-dd") + "冲突");
						}
					}
				} 
			}

			if (null != rule) {
				// 检查当前设置日期是否含有正常考勤日
				Calendar cursorCal = Calendar.getInstance();
				cursorCal.setTime(workday.getStartDay());
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(workday.getEndDay());
				// 正常考勤日列表(包括正常上班日与正常休息日)
				List<String> normalAttendList = new ArrayList<String>();
				while (!cursorCal.after(endCal)) {
					int dayOfWeek = cursorCal.get(Calendar.DAY_OF_WEEK);
					if (VyiyunUtils.checkSign4Today(rule, dayOfWeek)&& workday.getAttendOrNot().equals(CommonAppType.WorkdayFlag.YES.value())) {
						normalAttendList.add(DateUtil.dateToString(cursorCal.getTime(), "yyyy-MM-dd"));
					}
					if (!VyiyunUtils.checkSign4Today(rule, dayOfWeek)&& workday.getAttendOrNot().equals(CommonAppType.WorkdayFlag.NO.value())) {
						normalAttendList.add(DateUtil.dateToString(cursorCal.getTime(), "yyyy-MM-dd"));
					}
					cursorCal.add(Calendar.DAY_OF_MONTH, 1);
				}
				if (!CollectionUtils.isEmpty(normalAttendList)) {
					throw new VyiyunException("当前设置日期中" + VyiyunUtils.strArray2Str(',', normalAttendList.toArray(new String[]{}))
												+ "为正常考勤日期(包括正常上班日与正常休息日)，无需重复设置");
				}
			}
			workday.setId(CommonUtil.GeneGUID());
			workday.setCorpId(corpId);
			Calendar cal = Calendar.getInstance();
			cal.setTime(workday.getStartDay());
			workday.setYear(cal.get(Calendar.YEAR));

			ids.add(workday.getId());
		}
		// 工作日添加
		workdayDao.addWorkday(workdays);

		return ids.toArray(new String[]{});
	}

	@Override
	public DataResult queryWorkdays(Workday workDay) {
		String sPageIndex = HttpRequestUtil.getInst().getString("start"); // 页码
		String sPageSize = HttpRequestUtil.getInst().getString("length"); // 页面大小
		int pageIndex = -1;
		int pageSize = -1;
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
			pageSize = Integer.parseInt(sPageSize);
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
			pageIndex = Integer.parseInt(sPageIndex);
			if (pageIndex == 0) {
				pageIndex = 1;
			} else {
				pageIndex = pageIndex / pageSize + 1;
			}
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		sqlQueryParameter.setParameter(workDay);
		DataResult dr = new DataResult();
		dr.setData(workdayDao.queryWorkdayList(sqlQueryParameter));
		dr.setTotal(sqlQueryParameter.getTotalRecord());
		return dr;
	}

	@Override
	public int deleteWorkday(String id) {
		return workdayDao.deleteWorkday(id);
	}

}
