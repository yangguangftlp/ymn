/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.dao.AttendanceRuleMapper;
import com.vyiyun.common.weixin.entity.AttendanceRule;
import com.vyiyun.common.weixin.service.IAttendanceRuleService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;

/**
 * 考勤规则服务实现
 * @author zb.shen
 * @since 2015年12月29日
 */
@Service("attendanceRuleService")
public class AttendanceRuleServiceImpl implements IAttendanceRuleService {

	@Autowired
	private AttendanceRuleMapper attendanceRuleDao;

	@Override
	public boolean modifyAttendanceRule(String attendanceRuleStr) {
		if (StringUtil.isNotBlank(attendanceRuleStr)) {
			AttendanceRule attendanceRule = JSONObject.parseObject(attendanceRuleStr, AttendanceRule.class);
			if (StringUtil.isBlank(attendanceRule.getId())) {
				Date today = new Date();
				attendanceRule.setId(CommonUtil.GeneGUID());
				attendanceRule.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
//				attendanceRule.setCorpId("111111");
				attendanceRule.setCreateTime(today);
				attendanceRule.setUpdateTime(today);
				return attendanceRuleDao.addAttendanceRule(Arrays.asList(attendanceRule)) > 0;
			} else {
				return attendanceRuleDao.updateAttendanceRule(attendanceRule) > 0;
			}
		}
		return false;
	}

	@Override
	public AttendanceRule getAttendanceRule(String corpId) {
		return attendanceRuleDao.getAttendanceRule(new AttendanceRule(null, corpId));
	}

	@Override
	public List<AttendanceRule> getAllAttendanceRule() {
		return attendanceRuleDao.getAllAttendanceRule();
	}

}
